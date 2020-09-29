package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/4/23
 */
public interface OrderDao {

    int directSaveHandledOrder(OrderDO orderDO);

    /**
     * 新增工单
     *
     * @param orderDO orderDO
     * @return int
     */
    int insert(OrderDO orderDO);

    /**
     * 通过id获取工单
     *
     * @param id 工单id
     * @return OrderDO
     */
    OrderDO getById(Long id);

    /**
     * 获取所有的工单
     *
     * @return List<OrderDO>
     */
    List<OrderDO> list();

    /**
     * 通过id更新工单状态
     *
     * @param id 工单id
     * @param status 工单状态
     * @return int
     */
    int updateOrderStatusById(Long id, Integer status);

    /**
     * 通过id更新工单
     *
     * @param orderDO orderDO
     * @return int
     */
    int updateOrderById(OrderDO orderDO);

    /**
     * 获取我的申请工单
     * @param applicant 申请人
     * @param status 工单状态
     * @return List<OrderDO>
     */
    List<OrderDO> getByApplicantAndStatus(String applicant, Integer status);

    /**
     * 获取我的审批工单
     * @param approver 审批人
     * @param status 工单状态
     * @return List<OrderDO>
     */
    List<OrderDO> getByApproverAndStatus(String approver, Integer status);

    /**
     * 获取指定状态的工单
     * @param status 工单状态
     * @return List<OrderDO>
     */
    List<OrderDO> getByStatus(Integer status);

    /**
     * 获取从某个时间开始的审批工单
     * @param startTime 起始时间
     * @return List<OrderDO>
     */
    List<OrderDO> getByGmtHandle(Date startTime);

    int updateExtensionsById(OrderDO orderDO);

    List<OrderDO> getByHandleTime(Date startTime, Date endTime);
}
