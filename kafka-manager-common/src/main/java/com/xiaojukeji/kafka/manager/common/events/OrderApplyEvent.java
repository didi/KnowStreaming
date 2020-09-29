package com.xiaojukeji.kafka.manager.common.events;

import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;

/**
 * @author zengqiao
 * @date 20/09/03
 */
public class OrderApplyEvent extends OrderEvent {
    public OrderApplyEvent(Object source, OrderDO orderDO, String idc) {
        super(source, orderDO, idc);
    }
}
