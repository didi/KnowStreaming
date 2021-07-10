package com.xiaojukeji.kafka.manager.common.events;

import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import org.springframework.context.ApplicationEvent;

/**
 * @author zengqiao
 * @date 20/8/27
 */
public abstract class OrderEvent extends ApplicationEvent {
    private OrderDO orderDO;

    private String idc;

    public OrderEvent(Object source, OrderDO orderDO, String idc) {
        super(source);
        this.orderDO = orderDO;
        this.idc = idc;
    }

    public OrderDO getOrderDO() {
        return orderDO;
    }

    public String getIdc() {
        return idc;
    }
}