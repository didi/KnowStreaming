package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.PartitionOrderExtensionDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.PartitionOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleQuotaDTO;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractOrder;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionQuotaDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.RegionService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 20/9/15
 */
@Component("applyPartitionOrder")
public class ApplyPartitionOrder extends AbstractOrder {
    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private RegionService regionService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        PartitionOrderDetailData detailData = new PartitionOrderDetailData();
        PartitionOrderExtensionDTO dto = JSONObject.parseObject(extensions, PartitionOrderExtensionDTO.class);
        detailData.setTopicName(dto.getTopicName());
        detailData.setNeedIncrPartitionNum(dto.getNeedIncrPartitionNum());

        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(dto.getClusterId(), dto.getIsPhysicalClusterId());
        // 当前流入流量
        TopicMetrics metrics = KafkaMetricsCache.getTopicMetricsFromCache(physicalClusterId, dto.getTopicName());
        if (!ValidateUtils.isNull(metrics)) {
            detailData.setBytesIn(metrics.getBytesInPerSecOneMinuteRate(null));
        }
        // 今天，昨天，前天的峰值均值流入流量(B/s)
        List<Double> doList = topicManagerService.getTopicStatistic(
                physicalClusterId,
                dto.getTopicName(),
                new Date(DateUtils.getDayStarTime(-2)),
                new Date()
        ).stream().map(topic -> topic.getMaxAvgBytesIn()).collect(Collectors.toList());
        detailData.setMaxAvgBytesInList(doList);


        supplyBrokerAndRegion(detailData, physicalClusterId, dto.getTopicName());
        return supplyClusterInfo(detailData, dto.getClusterId(), dto.getIsPhysicalClusterId());
    }

    private PartitionOrderDetailData supplyBrokerAndRegion(PartitionOrderDetailData orderDetailDTO, Long physicalClusterId, String topicName) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(physicalClusterId, topicName);
        if (!ValidateUtils.isNull(topicMetadata)) {
            orderDetailDTO.setTopicBrokerIdList(new ArrayList<>(topicMetadata.getBrokerIdSet()));
            orderDetailDTO.setPresentPartitionNum(topicMetadata.getPartitionNum());
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

    private PartitionOrderDetailData supplyClusterInfo(PartitionOrderDetailData orderDetailDTO, Long clusterId, Boolean isPhysicalClusterId) {
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
        PartitionOrderExtensionDTO orderExtensionQuotaDTO = JSONObject.parseObject(
                extensions,
                PartitionOrderExtensionDTO.class);
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
        String title = String.format(
                "%s-%d-%s",
                OrderTypeEnum.APPLY_PARTITION.getMessage(),
                orderExtensionQuotaDTO.getClusterId(),
                orderExtensionQuotaDTO.getTopicName()
        );
        return new Result<>(title);
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO,
                                          OrderHandleBaseDTO orderHandleBaseDTO,
                                          String userName) {
        PartitionOrderExtensionDTO extensionDTO = JSONObject.parseObject(orderDO.getExtensions(),
                PartitionOrderExtensionDTO.class);

        OrderHandleQuotaDTO handleDTO = JSONObject.parseObject(orderHandleBaseDTO.getDetail(), OrderHandleQuotaDTO.class);

        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                extensionDTO.getClusterId(),
                extensionDTO.getIsPhysicalClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        if (!PhysicalClusterMetadataManager.isTopicExistStrictly(physicalClusterId, extensionDTO.getTopicName())) {
            return ResultStatus.TOPIC_NOT_EXIST;
        }
        if (handleDTO.isExistNullParam()) {
            return ResultStatus.OPERATION_FAILED;
        }
        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        return adminService.expandPartitions(
                clusterDO,
                extensionDTO.getTopicName(),
                handleDTO.getPartitionNum(),
                handleDTO.getRegionId(),
                handleDTO.getBrokerIdList(),
                userName
        );
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