package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionDeleteTopicDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteTopicDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractTopicOrder;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
@Component("deleteTopicOrder")
public class DeleteTopicOrder extends AbstractTopicOrder {
    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private AppService appService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private TopicConnectionService connectionService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderDetailDeleteTopicDTO orderDetailDTO = new OrderDetailDeleteTopicDTO();
        OrderExtensionDeleteTopicDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionDeleteTopicDTO.class);
        orderDetailDTO.setTopicName(orderExtensionDTO.getTopicName());
        ClusterNameDTO clusterNameDTO = clusterService.getClusterName(orderExtensionDTO.getClusterId());
        orderDetailDTO.setLogicalClusterId(clusterNameDTO.getLogicalClusterId());
        orderDetailDTO.setLogicalClusterName(clusterNameDTO.getLogicalClusterName());
        orderDetailDTO.setPhysicalClusterId(clusterNameDTO.getPhysicalClusterId());
        orderDetailDTO.setPhysicalClusterName(clusterNameDTO.getPhysicalClusterName());

        List<TopicConnection> connectionDTOList = connectionService.getByTopicName(
                clusterNameDTO.getPhysicalClusterId(),
                orderExtensionDTO.getTopicName(),
                new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                new Date());
        orderDetailDTO.setConnectionList(connectionDTOList);

        TopicDO topicDO = topicManagerService.getByTopicName(clusterNameDTO.getPhysicalClusterId(),
                orderExtensionDTO.getTopicName());
        if (ValidateUtils.isNull(topicDO)) {
            return orderDetailDTO;
        }

        AppDO appDO = appService.getByAppId(topicDO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return orderDetailDTO;
        }
        orderDetailDTO.setAppId(appDO.getAppId());
        orderDetailDTO.setAppName(appDO.getName());
        orderDetailDTO.setAppPrincipals(appDO.getPrincipals());
        return orderDetailDTO;
    }

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionDeleteTopicDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionDeleteTopicDTO.class);
        if (!orderExtensionDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                orderExtensionDTO.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, orderExtensionDTO.getTopicName())) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        String title = String.format(
                "%s-%d-%s",
                OrderTypeEnum.DELETE_TOPIC.getMessage(),
                orderExtensionDTO.getClusterId(),
                orderExtensionDTO.getTopicName()
        );
        return new Result<>(title);
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO,
                                          OrderHandleBaseDTO orderHandleBaseDTO,
                                          String userName) {
        OrderExtensionDeleteTopicDTO extensionDTO = JSONObject.parseObject(orderDO.getExtensions(),
                OrderExtensionDeleteTopicDTO.class);
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(extensionDTO.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        ClusterDO clusterDO = clusterService.getById(physicalClusterId);
        if (!PhysicalClusterMetadataManager.isTopicExistStrictly(physicalClusterId, extensionDTO.getTopicName())) {
            return ResultStatus.TOPIC_NOT_EXIST;
        }
        if (connectionService.isExistConnection(
                physicalClusterId,
                extensionDTO.getTopicName(),
                new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                new Date())
                ) {
            return ResultStatus.OPERATION_FORBIDDEN;
        }

        ResultStatus resultStatus = adminService.deleteTopic(clusterDO, extensionDTO.getTopicName(), userName);

        if (!ResultStatus.SUCCESS.equals(resultStatus)) {
            return resultStatus;
        }
        return resultStatus;
    }
}
