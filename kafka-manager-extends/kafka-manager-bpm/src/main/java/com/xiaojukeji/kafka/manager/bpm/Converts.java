package com.xiaojukeji.kafka.manager.bpm;

import com.xiaojukeji.kafka.manager.bpm.common.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;

/**
 * @author zengqiao
 * @date 20/9/11
 */
public class Converts {
    public static OrderDO convert2OrderDO(String title, OrderDTO orderDTO) {
        OrderDO orderDO = new OrderDO();
        orderDO.setStatus(OrderStatusEnum.WAIT_DEAL.getCode());
        orderDO.setType(orderDTO.getType());
        orderDO.setTitle(title);
        orderDO.setApplicant(orderDTO.getApplicant());
        orderDO.setDescription(orderDTO.getDescription());
        orderDO.setApprover("");
        orderDO.setOpinion("");
        orderDO.setExtensions(orderDTO.getExtensions());
        orderDO.setType(orderDTO.getType());
        return orderDO;
    }
}