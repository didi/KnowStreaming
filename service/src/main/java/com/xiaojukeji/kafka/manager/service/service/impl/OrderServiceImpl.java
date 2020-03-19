package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.OrderPartitionDO;
import com.xiaojukeji.kafka.manager.common.entity.po.OrderTopicDO;
import com.xiaojukeji.kafka.manager.dao.OrderPartitionDao;
import com.xiaojukeji.kafka.manager.dao.OrderTopicDao;
import com.xiaojukeji.kafka.manager.service.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengqiao
 * @date 19/6/18
 */
@Service("orderService")
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderTopicDao orderTopicDao;

    @Autowired
    private OrderPartitionDao orderPartitionDao;

    @Override
    public Boolean createOrderTopic(OrderTopicDO orderTopicDO) {
        return orderTopicDao.insert(orderTopicDO) > 0;
    }

    @Override
    public Result cancelOrder(Long orderId, String operator, OrderTypeEnum orderTypeEnum) {
        if (OrderTypeEnum.APPLY_TOPIC.equals(orderTypeEnum)) {
            OrderTopicDO orderTopicDO = orderTopicDao.getById(orderId);
            if (orderTopicDO != null) {
                orderTopicDO.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
            }
            return modifyOrderTopic(orderTopicDO, operator, false);
        } else if (OrderTypeEnum.APPLY_PARTITION.equals(orderTypeEnum)) {
            OrderPartitionDO orderPartitionDO = orderPartitionDao.getById(orderId);
            if (orderPartitionDO != null) {
                orderPartitionDO.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
            }
            return modifyOrderPartition(orderPartitionDO, operator);
        }
        return new Result(StatusCode.PARAM_ERROR, "order type illegal");
    }

    @Override
    public Result modifyOrderTopic(OrderTopicDO newOrderTopicDO, String operator, boolean admin) {
        if (newOrderTopicDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, order not exist");
        } else if (!admin && !newOrderTopicDO.getApplicant().equals(operator)) {
            return new Result(StatusCode.PARAM_ERROR, "without authority to cancel the order");
        }
        OrderTopicDO oldOrderTopicDO = orderTopicDao.getById(newOrderTopicDO.getId());
        if (!OrderStatusEnum.WAIT_DEAL.getCode().equals(oldOrderTopicDO.getOrderStatus())) {
            return new Result(StatusCode.OPERATION_ERROR, "order already handled");
        }
        if (orderTopicDao.updateById(newOrderTopicDO) > 0) {
            return new Result();
        }
        return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
    }

    @Override
    public Result modifyOrderPartition(OrderPartitionDO newOrderPartitionDO, String operator) {
        if (newOrderPartitionDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal, order not exist");
        } else if (!newOrderPartitionDO.getApplicant().equals(operator)) {
            return new Result(StatusCode.PARAM_ERROR, "without authority to cancel the order");
        }
        OrderPartitionDO oldOrderPartitionDO = orderPartitionDao.getById(newOrderPartitionDO.getId());
        if (!OrderStatusEnum.WAIT_DEAL.getCode().equals(oldOrderPartitionDO.getOrderStatus())) {
            return new Result(StatusCode.OPERATION_ERROR, "order already handled");
        }
        if (orderPartitionDao.updateById(newOrderPartitionDO) > 0) {
            return new Result();
        }
        return new Result(StatusCode.OPERATION_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
    }

    @Override
    public List<OrderTopicDO> getOrderTopics(String username) {
        if (username == null) {
            return orderTopicDao.list();
        }
        return orderTopicDao.getByUsername(username);
    }

    @Override
    public OrderTopicDO getOrderTopicById(Long orderId) {
        return orderTopicDao.getById(orderId);
    }

    @Override
    public Boolean createOrderPartition(OrderPartitionDO orderPartitionDO) {
        return orderPartitionDao.insert(orderPartitionDO) > 0;
    }

    @Override
    public List<OrderPartitionDO> getOrderPartitions(String username, Long orderId) {
        List<OrderPartitionDO> allOrderPartitionDOList = orderPartitionDao.list();
        if (allOrderPartitionDOList == null || allOrderPartitionDOList.isEmpty()) {
            return new ArrayList<>();
        }
        if (username == null) {
            return allOrderPartitionDOList.stream().filter(elem -> (orderId == null || (elem.getId().equals(orderId)))).collect(Collectors.toList());
        }
        return allOrderPartitionDOList.stream().filter(elem -> elem.getApplicant().equals(username) && (orderId == null || (elem.getId().equals(orderId)))).collect(Collectors.toList());
    }

    @Override
    public OrderPartitionDO getOrderPartitionById(Long orderId) {
        return orderPartitionDao.getById(orderId);
    }
}