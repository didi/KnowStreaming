package com.xiaojukeji.kafka.manager.notify;

import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.events.OrderPassedEvent;
import com.xiaojukeji.kafka.manager.notify.common.OrderNotifyTemplate;
import com.xiaojukeji.kafka.manager.notify.notifyer.AbstractNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author zengqiao
 * @date 20/8/27
 */
@Service("orderPassedNotifyService")
public class OrderPassedNotifyService implements ApplicationListener<OrderPassedEvent> {
    @Autowired
    private AbstractNotifyService notifyService;

    @Value("${notify.order.detail-url}")
    private String orderDetailUrl;

    @Async
    @Override
    public void onApplicationEvent(OrderPassedEvent orderPassEvent) {
        OrderDO orderDO = orderPassEvent.getOrderDO();
        String detailUrl = String.format(orderDetailUrl, orderDO.getId(), orderPassEvent.getIdc());
        notifyService.sendMsg(orderDO.getApplicant(),
                OrderNotifyTemplate.getNotifyOrderPassed2ApplicantMessage(
                        orderDO.getApplicant(),
                        orderDO.getTitle(),
                        detailUrl
                )
        );
    }
}