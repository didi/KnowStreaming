package com.xiaojukeji.kafka.manager.task.dispatch.op;

import com.xiaojukeji.kafka.manager.bpm.OrderService;
import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.LogConstant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.utils.DateUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ConfigDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.bpm.order.impl.ApplyAppOrder;
import com.xiaojukeji.kafka.manager.service.service.ConfigService;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import com.xiaojukeji.kafka.manager.task.component.EmptyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 工单自动化审批
 * @author zhongyuankai
 * @date 2020/6/12
 */
@Component
@CustomScheduled(name = "automatedHandleOrder", cron = "0 0/1 * * * ?", threadNum = 1)
@ConditionalOnProperty(prefix = "task.op.order-auto-exec", name = "app-enabled", havingValue = "true", matchIfMissing = false)
public class AutomatedHandleOrder extends AbstractScheduledTask<EmptyEntry> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogConstant.SCHEDULED_TASK_LOGGER);

    @Autowired
    private OrderService orderService;

    @Autowired
    private ConfigService configService;

    @Override
    public List<EmptyEntry> listAllTasks() {
        EmptyEntry emptyEntry = new EmptyEntry();
        emptyEntry.setId(System.currentTimeMillis() / 1000);
        return Arrays.asList(emptyEntry);
    }

    @Override
    public void processTask(EmptyEntry entryEntry) {
        List<OrderDO> waitDealOrderList = orderService.getWaitDealOrder();
        if (ValidateUtils.isEmptyList(waitDealOrderList)) {
            LOGGER.info("class=AutomatedHandleOrder||method=processTask||msg=waiting deal order is empty");
            return;
        }

        List<OrderDO> passedOrderList = orderService.getPassedOrder(new Date(DateUtils.getDayStarTime(0)));
        Map<Integer, List<OrderDO>> waitDealMap = getOrderTypeMap(waitDealOrderList);
        Map<Integer, List<OrderDO>> passedMap = getOrderTypeMap(passedOrderList);

        handleAppApplyOrder(
                waitDealMap.getOrDefault(OrderTypeEnum.APPLY_APP.getCode(), new ArrayList<>()),
                passedMap.getOrDefault(OrderTypeEnum.APPLY_APP.getCode(), new ArrayList<>())
        );
    }

    private void handleAppApplyOrder(List<OrderDO> waitDealOrderList, List<OrderDO> passedOrderList) {
        LOGGER.info("class=AutomatedHandleOrder||method=processTask||msg=start handle app apply order");
        if (ValidateUtils.isEmptyList(waitDealOrderList)) {
            return;
        }

        Integer maxNum = Constant.HANDLE_APP_APPLY_MAX_NUM_DEFAULT;
        ConfigDO configDO = configService.getByKey(Constant.HANDLE_APP_APPLY_MAX_NUM);
        if (!ValidateUtils.isNull(configDO)) {
            try {
                maxNum = Integer.parseInt(configDO.getConfigValue());
            } catch (Exception e) {
                LOGGER.error("class=AutomatedHandleOrder||method=processTask||configDO={}||msg=config value illegal", configDO, e);
            }
        }
        int handleNum = Math.min(maxNum - passedOrderList.size(), waitDealOrderList.size());
        if (handleNum <= 0) {
            return;
        }
        OrderHandleBaseDTO baseDTO = new OrderHandleBaseDTO();
        baseDTO.setStatus(OrderStatusEnum.PASSED.getCode());
        baseDTO.setOpinion("通过");
        baseDTO.setDetail("{}");
        ApplyAppOrder applyAppOrder = (ApplyAppOrder) SpringTool.getBean(OrderTypeEnum.APPLY_APP.getOrderName());

        for (int i = 0; i < handleNum; i++) {
            OrderDO orderDO = waitDealOrderList.get(i);
            try {
                ResultStatus resultStatus =
                        applyAppOrder.handleOrderDetail(orderDO, baseDTO, Constant.AUTO_HANDLE_USER_NAME);
                if (ResultStatus.SUCCESS.equals(resultStatus)) {
                    applyAppOrder.updateOrder(orderDO, baseDTO, Constant.AUTO_HANDLE_USER_NAME);
                }
            } catch (Exception e) {
                LOGGER.error("class=AutomatedHandleOrder||method=processTask||orderDO={}||msg=auto handle app order failed", orderDO, e);
            }
        }
    }

    private Map<Integer, List<OrderDO>> getOrderTypeMap(List<OrderDO> orderList) {
        if (ValidateUtils.isEmptyList(orderList)) {
            return new HashMap<>();
        }
        Map<Integer, List<OrderDO>> orderMap = new HashMap<>();
        for (OrderDO orderDO : orderList) {
            List<OrderDO> list = orderMap.getOrDefault(orderDO.getType(), new ArrayList<>());
            list.add(orderDO);
            orderMap.put(orderDO.getType(), list);
        }
        return orderMap;
    }
}
