package com.xiaojukeji.kafka.manager.notify;


import com.xiaojukeji.kafka.manager.common.events.OrderApplyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author zengqiao
 * @date 20/8/27
 */
@Service("orderApplyNotifyService")
public class OrderApplyNotifyService implements ApplicationListener<OrderApplyEvent> {

    @Async
    @Override
    public void onApplicationEvent(OrderApplyEvent orderApplyEvent) {
        // todo 工单通知
    }
}