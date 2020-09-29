package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.constant.TopicCreationConstant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionApplyTopicDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyTopicDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleTopicDTO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractTopicOrder;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
@Component("applyTopicOrder")
public class ApplyTopicOrder extends AbstractTopicOrder {
    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private AppService appService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private AdminService adminService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderDetailApplyTopicDTO orderDetailDTO = new OrderDetailApplyTopicDTO();
        OrderExtensionApplyTopicDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionApplyTopicDTO.class);
        orderDetailDTO.setAppId(orderExtensionDTO.getAppId());
        orderDetailDTO.setTopicName(orderExtensionDTO.getTopicName());
        orderDetailDTO.setPeakBytesIn(orderExtensionDTO.getPeakBytesIn());
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                orderExtensionDTO.getClusterId(),
                orderExtensionDTO.isPhysicalClusterId()
        );
        if (ValidateUtils.isNull(orderExtensionDTO.isPhysicalClusterId()) || !orderExtensionDTO.isPhysicalClusterId()) {
            LogicalClusterDO logicalCluster =
                    logicalClusterMetadataManager.getLogicalCluster(orderExtensionDTO.getClusterId());
            if (!ValidateUtils.isNull(logicalCluster)) {
                orderDetailDTO.setLogicalClusterId(logicalCluster.getId());
                orderDetailDTO.setLogicalClusterName(logicalCluster.getName());
                orderDetailDTO.setRegionIdList(ListUtils.string2LongList(logicalCluster.getRegionList()));
            }
        }
        ClusterDO cluster = clusterService.getById(physicalClusterId);
        if (!ValidateUtils.isNull(cluster)) {
            orderDetailDTO.setPhysicalClusterId(cluster.getId());
            orderDetailDTO.setPhysicalClusterName(cluster.getClusterName());
        }
        AppDO appDO = appService.getByAppId(orderExtensionDTO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return orderDetailDTO;
        }
        orderDetailDTO.setAppName(appDO.getName());
        orderDetailDTO.setAppPrincipals(appDO.getPrincipals());
        return orderDetailDTO;
    }

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionApplyTopicDTO orderTopicExtension = JSONObject.parseObject(
                extensions,
                OrderExtensionApplyTopicDTO.class);
        if (!orderTopicExtension.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                orderTopicExtension.getClusterId(),
                orderTopicExtension.isPhysicalClusterId()
        );
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if (PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, orderTopicExtension.getTopicName())) {
            return Result.buildFrom(ResultStatus.TOPIC_ALREADY_EXIST);
        }

        String title = String.format(
                "%s-%d-%s",
                OrderTypeEnum.APPLY_TOPIC.getMessage(),
                orderTopicExtension.getClusterId(),
                orderTopicExtension.getTopicName()
        );
        return new Result<>(title);
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO,
                                          OrderHandleBaseDTO orderHandleBaseDTO,
                                          String userName) {
        OrderExtensionApplyTopicDTO orderExtensionDTO = JSONObject.parseObject(orderDO.getExtensions(),
                OrderExtensionApplyTopicDTO.class);
        OrderHandleTopicDTO orderHandleDTO = JSONObject.parseObject(
                orderHandleBaseDTO.getDetail(),
                OrderHandleTopicDTO.class
        );
        if (orderHandleDTO.isExistNullParam()) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        AppDO appDO = appService.getByAppId(orderExtensionDTO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return ResultStatus.APP_NOT_EXIST;
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                orderExtensionDTO.getClusterId(),
                orderExtensionDTO.isPhysicalClusterId()
        );
        if (ValidateUtils.isNull(physicalClusterId)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (ValidateUtils.isNull(clusterDO)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }

        TopicDO topicDO = new TopicDO();
        topicDO.setAppId(orderExtensionDTO.getAppId());
        topicDO.setDescription(orderDO.getDescription());
        topicDO.setPeakBytesIn(orderExtensionDTO.getPeakBytesIn());
        topicDO.setTopicName(orderExtensionDTO.getTopicName());
        topicDO.setClusterId(physicalClusterId);
        Properties properties =
                TopicCreationConstant.createNewProperties(orderHandleDTO.getRetentionTime() * 3600 * 1000);
        ResultStatus result = adminService.createTopic(
                clusterDO,
                topicDO,
                orderHandleDTO.getPartitionNum(),
                orderHandleDTO.getReplicaNum(),
                orderHandleDTO.getRegionId(),
                orderHandleDTO.getBrokerIdList(),
                properties,
                orderDO.getApplicant(),
                userName);

        if (ResultStatus.SUCCESS.equals(result)) {
            orderDO.setExtensions(JSONObject.toJSONString(supplyExtension(orderExtensionDTO, orderHandleDTO)));
        }
        return result;
    }

    private OrderExtensionApplyTopicDTO supplyExtension(OrderExtensionApplyTopicDTO extensionDTO,
                                                        OrderHandleTopicDTO handleDTO){
        extensionDTO.setReplicaNum(handleDTO.getReplicaNum());
        extensionDTO.setPartitionNum(handleDTO.getPartitionNum());
        extensionDTO.setRetentionTime(handleDTO.getRetentionTime());
        extensionDTO.setRegionId(handleDTO.getRegionId());
        extensionDTO.setBrokerIdList(handleDTO.getBrokerIdList());
        return extensionDTO;
    }
}
