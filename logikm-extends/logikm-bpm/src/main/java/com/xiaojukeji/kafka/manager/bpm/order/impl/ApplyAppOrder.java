package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionApplyAppDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailApplyAppDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractAppOrder;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.utils.ConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
@Component("applyAppOrder")
public class ApplyAppOrder extends AbstractAppOrder {

    @Autowired
    private AppService appService;

    @Autowired
    private ConfigUtils configUtils;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderExtensionApplyAppDTO orderExtensionDTO = null;
        try {
            orderExtensionDTO = JSONObject.parseObject(extensions, OrderExtensionApplyAppDTO.class);
        } catch (Exception e) {

        }
        OrderDetailApplyAppDTO orderDetailDTO = new OrderDetailApplyAppDTO();
        orderDetailDTO.setName(orderExtensionDTO.getName());
        orderDetailDTO.setPrincipals(orderExtensionDTO.getPrincipals());
        AppDO appDO = appService.getByName(orderExtensionDTO.getName());
        if (ValidateUtils.isNull(appDO)) {
            return orderDetailDTO;
        }
        orderDetailDTO.setAppId(appDO.getAppId());
        orderDetailDTO.setPassword(appDO.getPassword());
        return orderDetailDTO;
    }

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionApplyAppDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionApplyAppDTO.class
        );
        if (!orderExtensionDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        AppDO appDO = appService.getByName(orderExtensionDTO.getName());
        if (!ValidateUtils.isNull(appDO)) {
            return Result.buildFrom(ResultStatus.RESOURCE_ALREADY_EXISTED);
        }
        if (!configUtils.getIdc().equals(orderExtensionDTO.getIdc())) {
            return Result.buildFrom(ResultStatus.IDC_NOT_EXIST);
        }
        String title = String.format(
                "%s-%s",
                OrderTypeEnum.APPLY_APP.getMessage(),
                orderExtensionDTO.getName()
        );
        return new Result<>(title);
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO, OrderHandleBaseDTO orderHandleBaseDTO, String userName) {
        OrderExtensionApplyAppDTO orderExtensionDTO = JSONObject.parseObject(
                orderDO.getExtensions(),
                OrderExtensionApplyAppDTO.class);
        AppDO appDO = new AppDO();
        appDO.setName(orderExtensionDTO.getName());
        appDO.setPrincipals(orderExtensionDTO.getPrincipals());
//        appDO.setId(orderDO.getId());
        appDO.setApplicant(orderDO.getApplicant());
        appDO.setDescription(orderDO.getDescription());
        appDO.generateAppIdAndPassword(orderDO.getId(), configUtils.getIdc());
        appDO.setType(0);
        return appService.addApp(appDO, userName);
    }
}
