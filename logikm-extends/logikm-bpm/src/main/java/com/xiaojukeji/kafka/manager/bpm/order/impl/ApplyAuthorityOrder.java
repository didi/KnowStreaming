package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionAuthorityDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyAuthorityDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractAuthorityOrder;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
@Component("applyAuthorityOrder")
public class ApplyAuthorityOrder extends AbstractAuthorityOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyAuthorityOrder.class);

    @Autowired
    private AppService appService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderDetailApplyAuthorityDTO orderDetailDTO = new OrderDetailApplyAuthorityDTO();
        OrderExtensionAuthorityDTO orderExtension = JSONObject.parseObject(
                extensions,
                OrderExtensionAuthorityDTO.class
        );
        orderDetailDTO.setTopicName(orderExtension.getTopicName());
        orderDetailDTO.setAccess(orderExtension.getAccess());
        orderDetailDTO.setAppId(orderExtension.getAppId());
        LogicalClusterDO logicalClusterDO =
                logicalClusterMetadataManager.getLogicalCluster(orderExtension.getClusterId());
        if (!orderExtension.isPhysicalClusterId() && !ValidateUtils.isNull(logicalClusterDO)) {
            orderDetailDTO.setLogicalClusterId(logicalClusterDO.getId());
            orderDetailDTO.setLogicalClusterName(logicalClusterDO.getName());
        }
        AppDO appDO = appService.getByAppId(orderExtension.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return orderDetailDTO;
        }
        orderDetailDTO.setAppName(appDO.getName());
        orderDetailDTO.setAppPrincipals(appDO.getPrincipals());
        return orderDetailDTO;
    }

    @Override
    public ResultStatus checkAuthority(OrderDO orderDO, String userName) {
        OrderExtensionAuthorityDTO orderExtensionDTO;
        try {
            orderExtensionDTO = JSONObject.parseObject(orderDO.getExtensions(), OrderExtensionAuthorityDTO.class);
        } catch (JSONException e) {
            LOGGER.error("parse json failed, req:{}.", orderDO.getExtensions(), e);
            return ResultStatus.JSON_PARSER_ERROR;
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                orderExtensionDTO.getClusterId(),
                orderExtensionDTO.isPhysicalClusterId()
        );
        if (ValidateUtils.isNull(physicalClusterId)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        TopicDO topicDO = topicManagerService.getByTopicName(physicalClusterId, orderExtensionDTO.getTopicName());
        if (ValidateUtils.isNull(topicDO)) {
            return ResultStatus.TOPIC_BIZ_DATA_NOT_EXIST;
        }
        AppDO appDO = appService.getByAppId(topicDO.getAppId());
        if (!appDO.getPrincipals().contains(userName)) {
            return ResultStatus.USER_WITHOUT_AUTHORITY;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO, OrderHandleBaseDTO orderHandleBaseDTO, String userName) {
        OrderExtensionAuthorityDTO orderExtensionDTO = JSONObject.parseObject(
                orderDO.getExtensions(),
                OrderExtensionAuthorityDTO.class);
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                orderExtensionDTO.getClusterId(),
                orderExtensionDTO.isPhysicalClusterId()
        );
        if (ValidateUtils.isNull(physicalClusterId)) {
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        TopicQuota topicQuotaDO = new TopicQuota();
        topicQuotaDO.setAppId(orderExtensionDTO.getAppId());
        topicQuotaDO.setTopicName(orderExtensionDTO.getTopicName());
        topicQuotaDO.setClusterId(physicalClusterId);

        AuthorityDO authorityDO = new AuthorityDO();
        authorityDO.setAccess(orderExtensionDTO.getAccess());
        authorityDO.setAppId(orderExtensionDTO.getAppId());
        authorityDO.setTopicName(orderExtensionDTO.getTopicName());
        authorityDO.setClusterId(physicalClusterId);
//        authorityDO.setApplicant(orderDO.getApplicant());

        if (authorityService.addAuthorityAndQuota(authorityDO, topicQuotaDO) < 1) {
            return ResultStatus.OPERATION_FAILED;
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public List<Account> getApproverList(String extensions) {
        List<Account> approverList = new ArrayList<>();
        OrderExtensionAuthorityDTO extensionDTO = JSONObject.parseObject(extensions, OrderExtensionAuthorityDTO.class);
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                extensionDTO.getClusterId(), extensionDTO.isPhysicalClusterId());
        TopicDO topicDO = topicManagerService.getByTopicName(physicalClusterId, extensionDTO.getTopicName());
        if (ValidateUtils.isNull(topicDO)) {
            return approverList;
        }
        AppDO appDO = appService.getByAppId(topicDO.getAppId());
        if (ValidateUtils.isNull(appDO) || ValidateUtils.isNull(appDO.getPrincipals())) {
            return approverList;
        }
        String[] principals = appDO.getPrincipals().split(",");
        for (String principal : principals) {
            Account approver = accountService.getAccountFromCache(principal);
            if (ValidateUtils.isNull(approver)) {
                continue;
            }
            approverList.add(approver);
        }
        return approverList;
    }
}
