package com.xiaojukeji.kafka.manager.bpm;

import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.OrderResult;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBatchDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;

import java.util.Date;
import java.util.List;

/**
 * @author arthur,zhongyuankai
 * @date 2017/7/30.
 */
public interface OrderService {
    ResultStatus directSaveHandledOrder(OrderDO orderDO);

    /**
     * 创建工单
     */
    Result createOrder(OrderDTO dto);

    /**
     * 工单详情
     */
    Result getOrderDetailData(Long orderId);

    /**
     * 处理工单
     */
    ResultStatus handleOrder(OrderHandleBaseDTO reqObj);


    /**
     * 通过id更新工单
     * @param orderDO orderDO
     * @return int
     */
    int updateOrderById(OrderDO orderDO);

    /**
     * 撤销工单
     *
     * @param id 工单id
     * @param userName 用户名称
     * @return ResultStatus
     */
    ResultStatus cancelOrder(Long id, String userName);

    /**
     * 获取工单申请列表
     *
     * @param applicant 申请人
     * @param status 工单状态
     * @return List<OrderDO>
     */
    List<OrderDO> getOrderApplyList(String applicant, Integer status);

    /**
     * 获取全部的工单审核列表
     *
     * @param approver 审批人
     * @return List<OrderDO>
     */
    List<OrderDO> getApprovalList(String approver);

    /**
     * 获取通过的工单审核列表
     *
     * @param approver 审批人
     * @return List<OrderDO>
     */
    List<OrderDO> getPassApprovalList(String approver);

    /**
     * 获取除指定类型的工单
     * @param userName 用户名称
     * @return List<OrderDO>
     */
    List<OrderDO> getWaitApprovalList(String userName);

    /**
     * 获取所有待审批的工单
     * @return List<OrderDO>
     */
    List<OrderDO> getWaitDealOrder();

    /**
     * 获取从某个时间起通过的工单
     * @param startTime 起始时间
     * @return List<OrderDO>
     */
    List<OrderDO> getPassedOrder(Date startTime);

    List<OrderDO> getAfterTime(Date startTime);

    int updateExtensionsById(OrderDO orderDO);

    List<OrderResult> handleOrderBatch(OrderHandleBatchDTO reqObj, String userName);
}
