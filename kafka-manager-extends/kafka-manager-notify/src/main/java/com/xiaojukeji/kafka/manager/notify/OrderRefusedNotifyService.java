package com.xiaojukeji.kafka.manager.notify;

import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.events.OrderRefusedEvent;
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
@Service("orderRefusedNotifyService")
public class OrderRefusedNotifyService implements ApplicationListener<OrderRefusedEvent> {
    @Autowired
    private AbstractNotifyService notifyService;

    @Value("${notify.order.detail-url}")
    private String orderDetailUrl;

    @Async
    @Override
    public void onApplicationEvent(OrderRefusedEvent orderRefuseEvent) {
        OrderDO orderDO = orderRefuseEvent.getOrderDO();
        String detailUrl = String.format(orderDetailUrl, orderDO.getId(), orderRefuseEvent.getIdc());
        notifyService.sendMsg(orderDO.getApplicant(),
                OrderNotifyTemplate.getNotifyOrderRefused2ApplicantMessage(
                        orderDO.getApplicant(),
                        orderDO.getTitle(),
                        detailUrl
                )
        );
    }
}