package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionAuthorityDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteAuthorityDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractAuthorityOrder;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/19
 */
@Component("deleteAuthorityOrder")
public class DeleteAuthorityOrder extends AbstractAuthorityOrder {

    @Autowired
    private AppService appService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private TopicConnectionService connectionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderDetailDeleteAuthorityDTO orderDetailDTO = new OrderDetailDeleteAuthorityDTO();
        OrderExtensionAuthorityDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionAuthorityDTO.class
        );
        orderDetailDTO.setAppId(orderExtensionDTO.getAppId());
        orderDetailDTO.setTopicName(orderExtensionDTO.getTopicName());
        orderDetailDTO.setAccess(orderExtensionDTO.getAccess());

        ClusterNameDTO clusterNameDTO = clusterService.getClusterName(orderExtensionDTO.getClusterId());
        orderDetailDTO.setLogicalClusterId(clusterNameDTO.getLogicalClusterId());
        orderDetailDTO.setLogicalClusterName(clusterNameDTO.getLogicalClusterName());
        orderDetailDTO.setPhysicalClusterId(clusterNameDTO.getPhysicalClusterId());
        orderDetailDTO.setPhysicalClusterName(clusterNameDTO.getPhysicalClusterName());

        AppDO appDO = appService.getByAppId(orderExtensionDTO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return orderDetailDTO;
        }
        orderDetailDTO.setAppName(appDO.getName());
        orderDetailDTO.setAppPrincipals(appDO.getPrincipals());

        List<TopicConnection> connectionDTOList = connectionService.getByTopicName(
                clusterNameDTO.getPhysicalClusterId(),
                orderExtensionDTO.getTopicName(),
                appDO.getAppId(),
                new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                new Date());
        orderDetailDTO.setConnectionList(connectionDTOList);
        return orderDetailDTO;
    }

    @Override
    public ResultStatus checkAuthority(OrderDO orderDO, String username) {
        if (!accountService.isAdminOrderHandler(username)) {
            return ResultStatus.USER_WITHOUT_AUTHORITY;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO,
                                          OrderHandleBaseDTO orderHandleBaseDTO,
                                          String userName) {
        OrderExtensionAuthorityDTO orderExtensionDTO = JSONObject.parseObject(
                orderDO.getExtensions(),
                OrderExtensionAuthorityDTO.class);
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(orderExtensionDTO.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        // 判断app对topic是否存在连接
        if (connectionService.isExistConnection(
                physicalClusterId,
                orderExtensionDTO.getTopicName(),
                orderExtensionDTO.getAppId(),
                new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                new Date())) {
            return ResultStatus.OPERATION_FORBIDDEN;
        }
        return authorityService.deleteSpecifiedAccess(
                orderExtensionDTO.getAppId(),
                physicalClusterId,
                orderExtensionDTO.getTopicName(),
                orderExtensionDTO.getAccess(),
                userName
        );
    }

    @Override
    public List<Account> getApproverList(String extensions) {
        return accountService.getAdminOrderHandlerFromCache();
    }
}
