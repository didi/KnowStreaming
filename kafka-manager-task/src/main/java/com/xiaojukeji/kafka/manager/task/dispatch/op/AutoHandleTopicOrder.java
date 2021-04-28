package com.xiaojukeji.kafka.manager.task.dispatch.op;

import com.xiaojukeji.kafka.manager.bpm.OrderService;
import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.constant.SystemCodeConstant;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.CreateTopicElemConfig;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionApplyTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import com.xiaojukeji.kafka.manager.task.component.EmptyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/7/28
 */
@Component
@CustomScheduled(name = "autoHandleTopicOrder", cron = "0 0/1 * * * ?", threadNum = 1)
@ConditionalOnProperty(prefix = "task.op.order-auto-exec", name = "topic-enabled", havingValue = "true", matchIfMissing = false)
public class AutoHandleTopicOrder extends AbstractScheduledTask<EmptyEntry> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private ConfigService configService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Override
    public List<EmptyEntry> listAllTasks() {
        EmptyEntry emptyEntry = new EmptyEntry();
        emptyEntry.setId(System.currentTimeMillis() / 1000);
        return Arrays.asList(emptyEntry);
    }

    @Override
    public void processTask(EmptyEntry entryEntry) {
        List<OrderDO> doList = orderService.getWaitDealOrder();
        if (ValidateUtils.isEmptyList(doList)) {
            return ;
        }

        Integer maxPassedOrderNumPerTask = configService.getAutoPassedTopicApplyOrderNumPerTask();
        for (OrderDO orderDO: doList) {
            try {
                if (!OrderTypeEnum.APPLY_TOPIC.getCode().equals(orderDO.getType()) && !handleApplyTopicOrder(orderDO)) {
                    continue;
                }
                maxPassedOrderNumPerTask -= 1;
                if (maxPassedOrderNumPerTask <= 0) {
                    return;
                }
                LOGGER.info("class=AutoHandleTopicOrder||method=processTask||msg=passed id:{}", orderDO.getId());
            } catch (Exception e) {
                LOGGER.error("handle apply topic order failed, orderDO:{}.", orderDO, e);
            }
        }
    }

    private boolean handleApplyTopicOrder(OrderDO orderDO) {
        OrderExtensionApplyTopicDTO dto = JsonUtils.stringToObj(orderDO.getExtensions(), OrderExtensionApplyTopicDTO.class);
        Long physicalClusterId =
                logicalClusterMetadataManager.getPhysicalClusterId(dto.getClusterId(), dto.isPhysicalClusterId());

        CreateTopicElemConfig createConfig =
                configService.getCreateTopicConfig(physicalClusterId, SystemCodeConstant.KAFKA_MANAGER);
        if (ValidateUtils.isNull(createConfig)) {
            return false;
        }

        if (dto.getPeakBytesIn() > createConfig.getAutoExecMaxPeakBytesInUnitB()) {
            return false;
        }

        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return false;
        }

        if (PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, dto.getTopicName())) {
            rejectForRepeatedTopicName(orderDO);
            return false;
        }

        if (ValidateUtils.isNull(dto.isPhysicalClusterId()) || !dto.isPhysicalClusterId()) {
            return handleApplyTopicOrderByLogicalClusterId(clusterDO, orderDO, dto, createConfig);
        }

        // 物理集群ID
        return handleApplyTopicOrderByPhysicalClusterId(clusterDO, orderDO, dto, createConfig);
    }

    private void rejectForRepeatedTopicName(OrderDO orderDO) {
        orderDO.setApplicant(Constant.AUTO_HANDLE_USER_NAME);
        orderDO.setStatus(OrderStatusEnum.REFUSED.getCode());
        orderDO.setOpinion("驳回：该 Topic 已被别人申请并生效");
        orderService.updateOrderById(orderDO);
    }

    /**
     * 逻辑集群申请单
     */
    private boolean handleApplyTopicOrderByLogicalClusterId(ClusterDO clusterDO,
                                                            OrderDO orderDO,
                                                            OrderExtensionApplyTopicDTO orderExtensionApplyTopicDTO,
                                                            CreateTopicElemConfig createConfig) {
        LogicalClusterDO logicalClusterDO =
                logicalClusterMetadataManager.getLogicalCluster(orderExtensionApplyTopicDTO.getClusterId());
        if (ValidateUtils.isNull(logicalClusterDO)) {
            return false;
        }
        return createTopic(
                clusterDO,
                orderExtensionApplyTopicDTO,
                orderDO,
                regionService.getIdleRegionBrokerList(clusterDO.getId(), ListUtils.string2LongList(logicalClusterDO.getRegionList())),
                createConfig
        );
    }


    /**
     * 物理集群申请单
     */
    private boolean handleApplyTopicOrderByPhysicalClusterId(ClusterDO clusterDO,
                                                             OrderDO orderDO,
                                                             OrderExtensionApplyTopicDTO orderExtensionApplyTopicDTO,
                                                             CreateTopicElemConfig createConfig) {
        return createTopic(
                clusterDO,
                orderExtensionApplyTopicDTO,
                orderDO,
                regionService.getIdleRegionBrokerList(clusterDO.getId(), createConfig.getRegionIdList()),
                createConfig
        );
    }

    private boolean createTopic(ClusterDO clusterDO,
                                OrderExtensionApplyTopicDTO orderExtensionApplyTopicDTO,
                                OrderDO orderDO,
                                List<Integer> brokerIdList,
                                CreateTopicElemConfig createConfig) {
        if (ValidateUtils.isEmptyList(brokerIdList)) {
            return false;
        }
        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(orderExtensionApplyTopicDTO.getAppId());
        topicDO.setClusterId(clusterDO.getId());
        topicDO.setTopicName(orderExtensionApplyTopicDTO.getTopicName());
        topicDO.setDescription(orderDO.getDescription());
        topicDO.setPeakBytesIn(orderExtensionApplyTopicDTO.getPeakBytesIn());

        Long partitionNum = Math.max(1, orderExtensionApplyTopicDTO.getPeakBytesIn() / (3 * 1024 * 1024));
        Properties properties = TopicCreationConstant.createNewProperties(
                createConfig.getRetentionTimeUnitHour() * 60 * 60 * 1000L
        );
        ResultStatus rs = adminService.createTopic(
                clusterDO,
                topicDO,
                partitionNum.intValue(),
                createConfig.getReplicaNum(),
                null,
                brokerIdList,
                properties,
                orderDO.getApplicant(),
                Constant.AUTO_HANDLE_USER_NAME
        );
        if (!ResultStatus.SUCCESS.equals(rs)) {
            return false;
        }
        orderDO.setApprover(Constant.AUTO_HANDLE_USER_NAME);
        orderDO.setOpinion(Constant.AUTO_HANDLE_CHINESE_NAME);
        orderDO.setStatus(OrderStatusEnum.PASSED.getCode());
        orderService.updateOrderById(orderDO);
        return true;
    }
}