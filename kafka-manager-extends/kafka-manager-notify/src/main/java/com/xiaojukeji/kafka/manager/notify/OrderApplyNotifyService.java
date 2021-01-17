package com.xiaojukeji.kafka.manager.notify;


import com.xiaojukeji.kafka.manager.common.entity.ao.account.Account;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.events.OrderApplyEvent;
import com.xiaojukeji.kafka.manager.notify.common.NotifyConstant;
import com.xiaojukeji.kafka.manager.notify.notifyer.AbstractNotifyService;
import com.xiaojukeji.kafka.manager.notify.common.OrderNotifyTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author zengqiao
 * @date 20/8/27
 */
@Service("orderApplyNotifyService")
public class OrderApplyNotifyService implements ApplicationListener<OrderApplyEvent> {
    @Autowired
    private AbstractNotifyService notifyService;

    @Value("${notify.order.detail-url}")
    private String orderDetailUrl;

    @Async
    @Override
    public void onApplicationEvent(OrderApplyEvent orderApplyEvent) {
        OrderDO orderDO = orderApplyEvent.getOrderDO();
        String detailUrl = String.format(orderDetailUrl, orderDO.getId(), orderApplyEvent.getIdc());
        for (Account account : NotifyConstant.accountList) {
            notifyService.sendMsg(account.getUsername(),
                    OrderNotifyTemplate.getNotify2OrderHandlerMessage(
                            account.getChineseName(),
                            orderDO.getApplicant(),
                            orderDO.getTitle(),
                            detailUrl
                    )
            );
        }

    }
}