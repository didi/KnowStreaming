package com.xiaojukeji.kafka.manager.bpm.order.impl.gateway;

import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractGatewayConfigOrder;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import org.springframework.stereotype.Component;

/**
 * @author zengqiao
 * @date 2021/01/12
 */
@Component("deleteGatewayConfigOrder")
public class DeleteGatewayConfigOrder extends AbstractGatewayConfigOrder {
    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        return Result.buildSuc();
    }

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        return null;
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO, OrderHandleBaseDTO baseDTO, String userName) {
        return ResultStatus.SUCCESS;
    }
}
