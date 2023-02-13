package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionAuthorityDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyAuthorityDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractAuthorityOrder;
import com.xiaojukeji.kafka.manager.common.bizenum.ha.HaRelationTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ha.HaASRelationDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.biz.ha.HaASRelationManager;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.ha.HaTopicService;
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

    @Autowired
    private HaTopicService haTopicService;

    @Autowired
    private HaASRelationManager haASRelationManager;

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

        HaASRelationDO relation = haASRelationManager.getASRelation(physicalClusterId, orderExtensionDTO.getTopicName());

        //是否高可用topic
        Integer haRelation = HaRelationTypeEnum.UNKNOWN.getCode();
        if (relation != null){
            //用户侧不允许操作备topic
            if (relation.getStandbyClusterPhyId().equals(orderExtensionDTO.getClusterId())){
                return ResultStatus.OPERATION_FORBIDDEN;
            }
            haRelation = HaRelationTypeEnum.ACTIVE.getCode();
        }

        ResultStatus resultStatus = applyAuthority(physicalClusterId,
                orderExtensionDTO.getTopicName(),
                userName,
                orderExtensionDTO.getAppId(),
                orderExtensionDTO.getAccess(),
                haRelation);
        if (haRelation.equals(HaRelationTypeEnum.UNKNOWN.getCode())
                && ResultStatus.SUCCESS.getCode() != resultStatus.getCode()){
            return resultStatus;
        }

        //给备topic添加权限
        if (relation.getActiveResName().equals(orderExtensionDTO.getTopicName())){
            return applyAuthority(relation.getStandbyClusterPhyId(),
                    relation.getStandbyResName(),
                    userName,
                    orderExtensionDTO.getAppId(),
                    orderExtensionDTO.getAccess(),
                    HaRelationTypeEnum.STANDBY.getCode());
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

    private ResultStatus applyAuthority(Long physicalClusterId, String topicName, String userName, String appId, Integer access, Integer haRelation){
        ClusterDO clusterDO = PhysicalClusterMetadataManager.getClusterFromCache(physicalClusterId);
        if (clusterDO == null){
            return ResultStatus.CLUSTER_NOT_EXIST;
        }
        TopicQuota topicQuotaDO = new TopicQuota();
        topicQuotaDO.setAppId(appId);
        topicQuotaDO.setTopicName(topicName);
        topicQuotaDO.setClusterId(physicalClusterId);

        AuthorityDO authorityDO = new AuthorityDO();
        authorityDO.setAccess(access);
        authorityDO.setAppId(appId);
        authorityDO.setTopicName(topicName);
        authorityDO.setClusterId(physicalClusterId);

        if (authorityService.addAuthorityAndQuota(authorityDO, topicQuotaDO) < 1) {
            return ResultStatus.OPERATION_FAILED;
        }

        Result result = new Result();
        HaASRelationDO relation = haASRelationManager.getASRelation(physicalClusterId, topicName);
        if (HaRelationTypeEnum.STANDBY.getCode() == haRelation){
            result = haTopicService.activeUserHAInKafka(PhysicalClusterMetadataManager.getClusterFromCache(relation.getActiveClusterPhyId()),
                    PhysicalClusterMetadataManager.getClusterFromCache(relation.getStandbyClusterPhyId()),
                    appId,
                    userName);
        }
        if (result.failed()){
            return ResultStatus.ZOOKEEPER_OPERATE_FAILED;
        }
        return ResultStatus.SUCCESS;
    }

}
