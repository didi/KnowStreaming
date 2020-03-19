package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.po.OrderPartitionDO;

import java.util.List;

public interface OrderPartitionDao {
    int insert(OrderPartitionDO orderPartitionDO);

    int deleteById(Long id);

    int updateById(OrderPartitionDO orderPartitionDO);

    OrderPartitionDO getById(Long id);

    List<OrderPartitionDO> list();
}
