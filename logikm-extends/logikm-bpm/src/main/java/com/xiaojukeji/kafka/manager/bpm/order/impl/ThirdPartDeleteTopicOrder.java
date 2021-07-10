package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionThirdPartDeleteTopicDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteTopicDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractTopicOrder;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/12/2
 */
@Component("thirdPartDeleteTopicOrder")
public class ThirdPartDeleteTopicOrder extends AbstractTopicOrder {
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
        OrderExtensionThirdPartDeleteTopicDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionThirdPartDeleteTopicDTO.class);
        orderDetailDTO.setTopicName(orderExtensionDTO.getTopicName());
        ClusterDO clusterDO = clusterService.getById(orderExtensionDTO.getClusterId());
        if (!ValidateUtils.isNull(clusterDO)) {
            orderDetailDTO.setPhysicalClusterId(clusterDO.getId());
            orderDetailDTO.setPhysicalClusterName(clusterDO.getClusterName());
        }

        List<TopicConnection> connectionDTOList = connectionService.getByTopicName(
                clusterDO.getId(),
                orderExtensionDTO.getTopicName(),
                new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                new Date());
        orderDetailDTO.setConnectionList(connectionDTOList);

        TopicDO topicDO = topicManagerService.getByTopicName(clusterDO.getId(), orderExtensionDTO.getTopicName());
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
        OrderExtensionThirdPartDeleteTopicDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionThirdPartDeleteTopicDTO.class);
        if (!orderExtensionDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(orderExtensionDTO.getClusterId(), true);
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, orderExtensionDTO.getTopicName())) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        AppDO appDO = appService.getByAppId(orderExtensionDTO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return Result.buildFrom(ResultStatus.APP_NOT_EXIST);
        }
        if (!appDO.getPassword().equals(orderExtensionDTO.getPassword())) {
            return Result.buildFrom(ResultStatus.USER_WITHOUT_AUTHORITY);
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
        OrderExtensionThirdPartDeleteTopicDTO extensionDTO = JSONObject.parseObject(orderDO.getExtensions(),
                OrderExtensionThirdPartDeleteTopicDTO.class);
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(extensionDTO.getClusterId(), true);
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

        // 检查申请人是否在应用负责人里面
        AppDO appDO = appService.getByAppId(extensionDTO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return ResultStatus.APP_NOT_EXIST;
        }
        if (!appDO.getPassword().equals(extensionDTO.getPassword())
                || !ListUtils.string2StrList(appDO.getPrincipals()).contains(orderDO.getApplicant())) {
            // 密码错误 or 申请人不在应用负责人里面, 则返回错误
            return ResultStatus.USER_WITHOUT_AUTHORITY;
        }

        ResultStatus resultStatus = adminService.deleteTopic(clusterDO, extensionDTO.getTopicName(), userName);
        if (!ResultStatus.SUCCESS.equals(resultStatus)) {
            return resultStatus;
        }
        return resultStatus;
    }
}