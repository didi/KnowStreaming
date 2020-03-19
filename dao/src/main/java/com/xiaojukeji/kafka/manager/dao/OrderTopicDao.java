package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.po.OrderTopicDO;

import java.util.List;

public interface OrderTopicDao {
    int insert(OrderTopicDO orderTopicDO);

    int deleteById(Long id);

    int updateById(OrderTopicDO orderTopicDO);

    OrderTopicDO getById(Long id);

    List<OrderTopicDO> list();

    List<OrderTopicDO> getByUsername(String username);
}