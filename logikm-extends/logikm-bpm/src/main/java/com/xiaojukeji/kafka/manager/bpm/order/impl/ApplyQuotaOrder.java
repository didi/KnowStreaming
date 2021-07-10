package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractOrder;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionQuotaDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.QuotaOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleQuotaDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.NumberUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.config.TopicQuotaData;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.QuotaService;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
@Component("applyQuotaOrder")
public class ApplyQuotaOrder extends AbstractOrder {
    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private AppService appService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private QuotaService quotaService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private RegionService regionService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        QuotaOrderDetailData orderDetailDTO = new QuotaOrderDetailData();
        OrderExtensionQuotaDTO dto = JSONObject.parseObject(extensions, OrderExtensionQuotaDTO.class);
        orderDetailDTO.setAppId(dto.getAppId());
        orderDetailDTO.setTopicName(dto.getTopicName());
        orderDetailDTO.setConsumeQuota(dto.getConsumeQuota());
        orderDetailDTO.setProduceQuota(dto.getProduceQuota());

        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(dto.getClusterId(), dto.getIsPhysicalClusterId());
        // 当前流入流量
        TopicMetrics metrics = KafkaMetricsCache.getTopicMetricsFromCache(physicalClusterId, dto.getTopicName());
        if (!ValidateUtils.isNull(metrics)) {
            orderDetailDTO.setBytesIn(metrics.getBytesInPerSecOneMinuteRate(null));
        }
        // 今天，昨天，前天的峰值均值流入流量(B/s)
        List<Double> doList = topicManagerService.getTopicStatistic(
                physicalClusterId,
                dto.getTopicName(),
                new Date(DateUtils.getDayStarTime(-2)),
                new Date()
        ).stream().map(topic -> topic.getMaxAvgBytesIn()).collect(Collectors.toList());
        orderDetailDTO.setMaxAvgBytesInList(doList);

        // 获取旧的配额
        TopicQuotaData clientQuota =
                KafkaZookeeperUtils.getTopicQuota(
                        PhysicalClusterMetadataManager.getZKConfig(physicalClusterId),
                        dto.getAppId(),
                        dto.getTopicName()
                );
        if (!ValidateUtils.isNull(clientQuota)) {
            orderDetailDTO.setOldProduceQuota(NumberUtils.string2Long(clientQuota.getProducer_byte_rate()));
            orderDetailDTO.setOldConsumeQuota(NumberUtils.string2Long(clientQuota.getConsumer_byte_rate()));
        }

        AppDO appDO = appService.getByAppId(dto.getAppId());
        if (!ValidateUtils.isNull(appDO)) {
            orderDetailDTO.setAppName(appDO.getName());
            orderDetailDTO.setAppPrincipals(appDO.getPrincipals());
        }

        supplyBrokerAndRegion(orderDetailDTO, physicalClusterId, dto.getTopicName());
        return supplyClusterInfo(orderDetailDTO, dto.getClusterId(), dto.getIsPhysicalClusterId());
    }

    private QuotaOrderDetailData supplyBrokerAndRegion(QuotaOrderDetailData orderDetailDTO, Long physicalClusterId, String topicName) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(physicalClusterId, topicName);
        if (!ValidateUtils.isNull(topicMetadata)) {
            orderDetailDTO.setTopicBrokerIdList(new ArrayList<>(topicMetadata.getBrokerIdSet()));
            orderDetailDTO.setPartitionNum(topicMetadata.getPartitionNum());
        }
        List<RegionDO> regionDOList = regionService.getRegionListByTopicName(physicalClusterId, topicName);
        List<String> regionNameList = new ArrayList<>();
        List<Integer> regionBrokerIdList = new ArrayList<>();
        for (RegionDO regionDO : regionDOList) {
            regionNameList.add(regionDO.getName());
            regionBrokerIdList.addAll(ListUtils.string2IntList(regionDO.getBrokerList()));
        }
        orderDetailDTO.setRegionBrokerIdList(regionBrokerIdList);
        orderDetailDTO.setRegionNameList(regionNameList);
        return orderDetailDTO;
    }

    private QuotaOrderDetailData supplyClusterInfo(QuotaOrderDetailData orderDetailDTO, Long clusterId, Boolean isPhysicalClusterId) {
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return orderDetailDTO;
        }
        ClusterDO cluster = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(cluster)) {
            return orderDetailDTO;
        }
        orderDetailDTO.setPhysicalClusterId(cluster.getId());
        orderDetailDTO.setPhysicalClusterName(cluster.getClusterName());

        LogicalClusterDO logicalCluster = logicalClusterMetadataManager.getLogicalCluster(clusterId, isPhysicalClusterId);
        if (ValidateUtils.isNull(logicalCluster)) {
            return orderDetailDTO;
        }
        orderDetailDTO.setLogicalClusterId(logicalCluster.getId());
        orderDetailDTO.setLogicalClusterName(logicalCluster.getName());
        return orderDetailDTO;
    }

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionQuotaDTO orderExtensionQuotaDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionQuotaDTO.class);
        if (!orderExtensionQuotaDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                orderExtensionQuotaDTO.getClusterId(),
                orderExtensionQuotaDTO.getIsPhysicalClusterId()
        );
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, orderExtensionQuotaDTO.getTopicName())) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        if (ValidateUtils.isNull(appService.getByAppId(orderExtensionQuotaDTO.getAppId()))) {
            return Result.buildFrom(ResultStatus.APP_NOT_EXIST);
        }
        String title = String.format(
                "%s-%d-%s",
                OrderTypeEnum.APPLY_QUOTA.getMessage(),
                orderExtensionQuotaDTO.getClusterId(),
                orderExtensionQuotaDTO.getTopicName()
        );
        return new Result<>(title);
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO, OrderHandleBaseDTO orderHandleBaseDTO, String userName) {
        OrderExtensionQuotaDTO extensionDTO = JSONObject.parseObject(orderDO.getExtensions(),
                OrderExtensionQuotaDTO.class);
        OrderHandleQuotaDTO handleDTO = JSONObject.parseObject(orderHandleBaseDTO.getDetail(), OrderHandleQuotaDTO.class);
        AppDO appDO = appService.getByAppId(extensionDTO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return ResultStatus.APP_NOT_EXIST;
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                extensionDTO.getClusterId(),
                extensionDTO.getIsPhysicalClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        if (!PhysicalClusterMetadataManager.isTopicExistStrictly(physicalClusterId, extensionDTO.getTopicName())) {
            return ResultStatus.TOPIC_NOT_EXIST;
        }
        if (!handleDTO.isExistNullParam()) {
            ClusterDO clusterDO = clusterService.getById(physicalClusterId);
            ResultStatus resultStatus = adminService.expandPartitions(
                    clusterDO,
                    extensionDTO.getTopicName(),
                    handleDTO.getPartitionNum(),
                    handleDTO.getRegionId(),
                    handleDTO.getBrokerIdList(),
                    userName);
            if (!ResultStatus.SUCCESS.equals(resultStatus)) {
                return resultStatus;
            }
        }
        TopicQuota topicQuotaDO = new TopicQuota();
        topicQuotaDO.setAppId(extensionDTO.getAppId());
        topicQuotaDO.setTopicName(extensionDTO.getTopicName());
        topicQuotaDO.setConsumeQuota(extensionDTO.getConsumeQuota());
        topicQuotaDO.setProduceQuota(extensionDTO.getProduceQuota());
        topicQuotaDO.setClusterId(physicalClusterId);
        if (quotaService.addTopicQuota(topicQuotaDO) > 0) {
            orderDO.setExtensions(JSONObject.toJSONString(supplyExtension(extensionDTO, handleDTO)));
            return ResultStatus.SUCCESS;
        }
        return ResultStatus.OPERATION_FAILED;
    }

    private OrderExtensionQuotaDTO supplyExtension(OrderExtensionQuotaDTO extensionDTO, OrderHandleQuotaDTO handleDTO){
        extensionDTO.setPartitionNum(handleDTO.getPartitionNum());
        extensionDTO.setRegionId(handleDTO.getRegionId());
        extensionDTO.setBrokerIdList(handleDTO.getBrokerIdList());
        return extensionDTO;
    }

    @Override
    public ResultStatus checkAuthority(OrderDO orderDO, String username) {
        if (!accountService.isAdminOrderHandler(username)) {
            return ResultStatus.USER_WITHOUT_AUTHORITY;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public List<Account> getApproverList(String extensions) {
        return accountService.getAdminOrderHandlerFromCache();
    }
}
