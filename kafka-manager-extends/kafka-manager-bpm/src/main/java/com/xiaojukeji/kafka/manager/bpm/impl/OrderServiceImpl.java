package com.xiaojukeji.kafka.manager.bpm.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.account.AccountService;
import com.xiaojukeji.kafka.manager.bpm.Converts;
import com.xiaojukeji.kafka.manager.bpm.OrderService;
import com.xiaojukeji.kafka.manager.bpm.component.AbstractOrderStorageService;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.bpm.common.OrderResult;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBatchDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionAuthorityDTO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractOrder;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 2020/4/23
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private AppService appService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private AbstractOrderStorageService orderStorageService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Override
    public ResultStatus directSaveHandledOrder(OrderDO orderDO) {
        return orderStorageService.directSaveHandledOrder(orderDO);
    }

    @Override
    public Result createOrder(OrderDTO dto) {
        // 检查参数
        if (ValidateUtils.isNull(dto) || !dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        OrderTypeEnum orderTypeEnum = OrderTypeEnum.getByTypeCode(dto.getType());
        if (ValidateUtils.isNull(orderTypeEnum)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        // 依据工单类型获取service & check 扩展字段
        AbstractOrder abstractOrder = SpringTool.getBean(orderTypeEnum.getOrderName());
        Result<String> rs = abstractOrder.checkExtensionFieldsAndGenerateTitle(dto.getExtensions());
        if (!Constant.SUCCESS.equals(rs.getCode())) {
            return rs;
        }

        // 存储工单
        OrderDO orderDO = Converts.convert2OrderDO(rs.getData(), dto);
        if (orderStorageService.save(orderDO)) {
            return new Result<>(orderDO);
        }
        return Result.buildFrom(ResultStatus.MYSQL_ERROR);
    }

    @Override
    public Result getOrderDetailData(Long orderId) {
        OrderDO orderDO = orderStorageService.getById(orderId);
        if (ValidateUtils.isNull(orderDO)) {
            return Result.buildFrom(ResultStatus.ORDER_NOT_EXIST);
        }
        OrderTypeEnum orderTypeEnum = OrderTypeEnum.getByTypeCode(orderDO.getType());
        if (ValidateUtils.isNull(orderTypeEnum)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        AbstractOrder abstractOrder = SpringTool.getBean(orderTypeEnum.getOrderName());
        return new Result<>(abstractOrder.getOrderDetail(orderDO));
    }

    @Override
    public ResultStatus handleOrder(OrderHandleBaseDTO reqObj) {
        if (ValidateUtils.isNull(reqObj) || !reqObj.paramLegal()) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        OrderDO orderDO = orderStorageService.getById(reqObj.getId());
        if (ValidateUtils.isNull(orderDO)) {
            return ResultStatus.ORDER_NOT_EXIST;
        }
        OrderTypeEnum orderTypeEnum = OrderTypeEnum.getByTypeCode(orderDO.getType());
        if (ValidateUtils.isNull(orderTypeEnum)) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        AbstractOrder abstractOrder = SpringTool.getBean(orderTypeEnum.getOrderName());
        return abstractOrder.handleOrder(orderDO, reqObj, SpringTool.getUserName(), Boolean.TRUE);
    }

    @Override
    public int updateOrderById(OrderDO orderDO) {
        return orderStorageService.updateOrderById(orderDO);
    }

    @Override
    public ResultStatus cancelOrder(Long id, String username) {
        return orderStorageService.cancel(id, username);
    }

    @Override
    public List<OrderDO> getOrderApplyList(String applicant, Integer status) {
        List<OrderDO> orderDOList = new ArrayList<>();
        try {
            orderDOList = orderStorageService.getByApplicantAndStatus(applicant, status);
        } catch (Exception e) {
            LOGGER.error("get apply order list failed, applicant:{} status:{}.", applicant, status, e);
        }
        return orderDOList;
    }

    @Override
    public List<OrderDO> getApprovalList(String approver) {
        try {
            if (!accountService.isAdminOrderHandler(approver)) {
                return orderStorageService.getByApproverAndStatus(approver, null);
            }
            return orderStorageService.list();
        } catch (Exception e) {
            LOGGER.error("get approval order list failed, approver:{}.", approver, e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<OrderDO> getPassApprovalList(String approver) {
        try {
            if (!accountService.isAdminOrderHandler(approver)) {
                return orderStorageService.getByApproverAndStatus(approver, OrderStatusEnum.PASSED.getCode());
            }
            return orderStorageService.getByStatus(OrderStatusEnum.PASSED.getCode());
        } catch (Exception e) {
            LOGGER.error("get approval order list failed, approver:{}.", approver, e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<OrderDO> getWaitApprovalList(String userName) {
        List<OrderDO> orderList = new ArrayList<>();
        List<AppDO> appDOList = new ArrayList<>();
        try {
            orderList = orderStorageService.getByStatus(OrderStatusEnum.WAIT_DEAL.getCode());
            appDOList = appService.getByPrincipal(userName);
        } catch (Exception e) {
            LOGGER.error("get wait approval list failed, userName:{}.", userName, e);
        }
        Set<String> appIdSet = appDOList.stream().map(appDO -> appDO.getAppId()).collect(Collectors.toSet());
        boolean isAdmin = accountService.isAdminOrderHandler(userName);
        Map<Long, Map<String, TopicDO>> topicMap = new HashMap<>();
        orderList = orderList.stream().filter((orderDO) -> {
            if (!OrderTypeEnum.APPLY_AUTHORITY.getCode().equals(orderDO.getType())) {
                return isAdmin;
            }
            try {
                OrderExtensionAuthorityDTO orderExtension = JSONObject.parseObject(
                        orderDO.getExtensions(),
                        OrderExtensionAuthorityDTO.class
                );
                Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                        orderExtension.getClusterId(),
                        orderExtension.isPhysicalClusterId()
                );
                TopicDO topicDO = getTopicDOFromCacheOrDB(
                        physicalClusterId,
                        orderExtension.getTopicName(),
                        topicMap
                );
                if (ValidateUtils.isNull(topicDO)) {
                    return false;
                }
                return appIdSet.contains(topicDO.getAppId());
            } catch (Exception e) {
                LOGGER.error("parse json failed, extensions:{}.", orderDO.getExtensions(), e);
            }
            return false;
        }).collect(Collectors.toList());
        return orderList;
    }

    @Override
    public List<OrderDO> getWaitDealOrder() {
        try {
            return orderStorageService.getByStatus(OrderStatusEnum.WAIT_DEAL.getCode());
        } catch (Exception e) {
            LOGGER.error("get wait deal order failed.", e);
        }
        return null;
    }

    @Override
    public List<OrderDO> getPassedOrder(Date startTime) {
        try {
            return orderStorageService.getByGmtHandle(startTime);
        } catch (Exception e) {
            LOGGER.error("get passed order failed, startTime:{}.", startTime, e);
        }
        return null;
    }

    private TopicDO getTopicDOFromCacheOrDB(Long physicalClusterId,
                                            String topicName,
                                            Map<Long, Map<String, TopicDO>> topicMap) {
        Map<String, TopicDO> subTopicMap = topicMap.getOrDefault(physicalClusterId, new HashMap<>());
        if (subTopicMap.containsKey(topicName)) {
            return subTopicMap.get(topicName);
        }
        TopicDO topicDO = topicManagerService.getByTopicName(physicalClusterId, topicName);
        subTopicMap.put(topicName, topicDO);
        topicMap.put(physicalClusterId, subTopicMap);
        return topicDO;
    }

    @Override
    public List<OrderDO> getAfterTime(Date startTime) {
        return orderStorageService.getByHandleTime(startTime, new Date());
    }

    @Override
    public int updateExtensionsById(OrderDO orderDO) {
        return orderStorageService.updateExtensionsById(orderDO);
    }

    @Override
    public List<OrderResult> handleOrderBatch(OrderHandleBatchDTO reqObj, String userName) {
        List<OrderResult> resultList = new ArrayList<>(reqObj.getOrderIdList().size());
        List<OrderDO> orderDOList = new ArrayList<>(reqObj.getOrderIdList().size());
        for (Long id : reqObj.getOrderIdList()) {
            OrderDO orderDO = orderStorageService.getById(id);
            if (ValidateUtils.isNull(orderDO)) {
                resultList.add(new OrderResult(id, Result.buildFrom(ResultStatus.ORDER_NOT_EXIST)));
                continue;
            }
            // topic申请、topic分区申请不支持批量审批通过.
            if (orderDO.getType().equals(OrderTypeEnum.APPLY_TOPIC.getCode())
              || orderDO.getType().equals(OrderTypeEnum.APPLY_PARTITION.getCode())) {
                if (OrderStatusEnum.PASSED.getCode().equals(reqObj.getStatus())) {
                    continue;
                }
            }

            orderDOList.add(orderDO);
        }
        // 根据创建时间排序
        List<OrderDO> orderList = orderDOList.stream()
                .sorted((o1, o2) -> DateUtils.compare(o1.getGmtCreate(), o2.getGmtCreate()))
                .collect(Collectors.toList());

        for (OrderDO orderDO : orderList) {
            OrderTypeEnum orderTypeEnum = OrderTypeEnum.getByTypeCode(orderDO.getType());
            AbstractOrder abstractOrder = SpringTool.getBean(orderTypeEnum.getOrderName());

            OrderHandleBaseDTO baseDTO = new OrderHandleBaseDTO();
            baseDTO.setId(orderDO.getId());
            baseDTO.setStatus(reqObj.getStatus());
            baseDTO.setOpinion(reqObj.getOpinion());
            baseDTO.setDetail("{}");
            resultList.add(new OrderResult(
                    orderDO.getId(),
                    Result.buildFrom(abstractOrder.handleOrder(orderDO, baseDTO, userName, Boolean.TRUE))
            ));
        }
        return resultList;
    }
}