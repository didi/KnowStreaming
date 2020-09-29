package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionApplyClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractClusterOrder;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
@Component("applyClusterOrder")
public class ApplyClusterOrder extends AbstractClusterOrder {

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private AppService appService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderDetailApplyClusterDTO orderDetailDTO = new OrderDetailApplyClusterDTO();
        OrderExtensionApplyClusterDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionApplyClusterDTO.class);
        orderDetailDTO.setBytesIn(orderExtensionDTO.getBytesIn());
        orderDetailDTO.setIdc(orderExtensionDTO.getIdc());
        orderDetailDTO.setAppId(orderExtensionDTO.getAppId());
        orderDetailDTO.setMode(orderExtensionDTO.getMode());
        return orderDetailDTO;
    }

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionApplyClusterDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionApplyClusterDTO.class);
        if (!orderExtensionDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        if (ValidateUtils.isNull(appService.getByAppId(orderExtensionDTO.getAppId()))) {
            return Result.buildFrom(ResultStatus.APP_NOT_EXIST);
        }
        if (!orderExtensionDTO.getIdc().equals(configUtils.getIdc())) {
            return Result.buildFrom(ResultStatus.IDC_NOT_EXIST);
        }
        return new Result<>(OrderTypeEnum.APPLY_CLUSTER.getMessage());
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO,
                                          OrderHandleBaseDTO orderHandleBaseDTO,
                                          String userName) {
        return ResultStatus.SUCCESS;
    }
}
