package com.xiaojukeji.kafka.manager.bpm.order.impl.gateway;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.gateway.OrderExtensionModifyGatewayConfigDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailGatewayConfigData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailGatewayConfigModifyData;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractGatewayConfigOrder;
import com.xiaojukeji.kafka.manager.common.bizenum.gateway.GatewayConfigKeyEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.service.OperateRecordService;
import com.xiaojukeji.kafka.manager.service.service.gateway.GatewayConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zengqiao
 * @date 2021/01/12
 */
@Component("modifyGatewayConfigOrder")
public class ModifyGatewayConfigOrder extends AbstractGatewayConfigOrder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyGatewayConfigOrder.class);

    @Autowired
    private GatewayConfigService gatewayConfigService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionModifyGatewayConfigDTO orderExtensionDTO = null;
        try {
            orderExtensionDTO = JSONObject.parseObject(extensions, OrderExtensionModifyGatewayConfigDTO.class);
        } catch (Exception e) {
            LOGGER.error("class=ModifyGatewayConfigOrder||method=checkExtensionFieldsAndGenerateTitle||params={}||errMsg={}", extensions, e.getMessage());
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        if (!orderExtensionDTO.legal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        GatewayConfigDO gatewayConfigDO = gatewayConfigService.getById(orderExtensionDTO.getId());
        if (ValidateUtils.isNull(gatewayConfigDO)) {
            // 配置不存在
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        GatewayConfigKeyEnum configKeyEnum = GatewayConfigKeyEnum.getByConfigType(orderExtensionDTO.getType());
        if (ValidateUtils.isNull(configKeyEnum)) {
            // 配置类型不对
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        return new Result<>(OrderTypeEnum.MODIFY_GATEWAY_CONFIG.getMessage());
    }

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderExtensionModifyGatewayConfigDTO orderExtensionDTO = null;
        try {
            orderExtensionDTO = JSONObject.parseObject(extensions, OrderExtensionModifyGatewayConfigDTO.class);
        } catch (Exception e) {
            LOGGER.error("class=ModifyGatewayConfigOrder||method=getOrderExtensionDetailData||params={}||errMsg={}", extensions, e.getMessage());
            return null;
        }

        // 返回的数据
        OrderDetailGatewayConfigModifyData orderDetailDTO = new OrderDetailGatewayConfigModifyData();

        // 新的配置
        OrderDetailGatewayConfigData newGatewayConfig = new OrderDetailGatewayConfigData();
        newGatewayConfig.setId(orderExtensionDTO.getId());
        newGatewayConfig.setType(orderExtensionDTO.getType());
        newGatewayConfig.setName(orderExtensionDTO.getName());
        newGatewayConfig.setValue(orderExtensionDTO.getValue());
        orderDetailDTO.setNewGatewayConfig(newGatewayConfig);

        GatewayConfigDO gatewayConfigDO = gatewayConfigService.getById(orderExtensionDTO.getId());
        if (ValidateUtils.isNull(gatewayConfigDO)) {
            // 旧的配置不存在
            return orderDetailDTO;
        }

        // 旧的配置
        OrderDetailGatewayConfigData oldGatewayConfig = new OrderDetailGatewayConfigData();
        newGatewayConfig.setId(gatewayConfigDO.getId());
        newGatewayConfig.setType(gatewayConfigDO.getType());
        newGatewayConfig.setName(gatewayConfigDO.getName());
        newGatewayConfig.setValue(gatewayConfigDO.getValue());
        newGatewayConfig.setVersion(gatewayConfigDO.getVersion());
        orderDetailDTO.setOldGatewayConfig(oldGatewayConfig);

        return orderDetailDTO;
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO, OrderHandleBaseDTO baseDTO, String username) {
        return ResultStatus.SUCCESS;
    }
}
