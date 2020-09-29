package com.xiaojukeji.kafka.manager.common.events;

import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/09/03
 */
public class OrderPassedEvent extends OrderEvent {
    public OrderPassedEvent(Object source, OrderDO orderDO, String idc, List<Account> accountList) {
        super(source, orderDO, idc);
    }
}
