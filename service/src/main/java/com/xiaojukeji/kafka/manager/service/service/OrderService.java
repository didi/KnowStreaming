package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.OrderPartitionDO;
import com.xiaojukeji.kafka.manager.common.entity.po.OrderTopicDO;

import java.util.List;

/**
 * @author arthur
 * @date 2017/7/30.
 */
public interface OrderService {
    /**
     * 创建Topic申请工单
     * @param orderTopicDO 工单信息
     * @return java.lang.Boolean
     */
    Boolean createOrderTopic(OrderTopicDO orderTopicDO);

    /**
     * 取消工单
     * @param orderId 工单Id
     * @param operator 操作人
     * @param orderTypeEnum 工单类型
     * @date 19/6/23
     * @return Result
     */
    Result cancelOrder(Long orderId, String operator, OrderTypeEnum orderTypeEnum);

    /**
     * 修改Topic工单
     * @param orderTopicDO 工单
     * @param operator 操作人
     * @date 19/6/23
     * @return Result
     */
    Result modifyOrderTopic(OrderTopicDO orderTopicDO, String operator, boolean admin);

    /**
     * 修改Partition工单
     * @param orderPartitionDO 工单
     * @param operator 操作人
     * @date 19/6/23
     * @return Result
     */
    Result modifyOrderPartition(OrderPartitionDO orderPartitionDO, String operator, boolean admin);

    /**
     * 查询Topic工单
     * @param username 用户名
     * @return List<OrderTopicDO>
     */
    List<OrderTopicDO> getOrderTopics(String username);

    /**
     * 查询Topic工单
     * @param orderId 工单ID
     * @return OrderTopicDO
     */
    OrderTopicDO getOrderTopicById(Long orderId);

    /**
     * 创建partition申请工单
     * @param orderPartitionDO 工单信息
     * @return java.lang.Boolean
     */
    Boolean createOrderPartition(OrderPartitionDO orderPartitionDO);

    /**
     * 查询partition工单
     * @param username 用户名
     * @param orderId 工单Id
     * @return List<OrderPartitionDO>
     */
    List<OrderPartitionDO> getOrderPartitions(String username, Long orderId);

    /**
     * 查询Partition工单
     * @param orderId 工单ID
     * @return OrderPartitionDO
     */
    OrderPartitionDO getOrderPartitionById(Long orderId);
}
