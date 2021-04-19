package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionDeleteAppDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteAppDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractAppOrder;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.service.gateway.TopicConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
@Component("deleteAppOrder")
public class DeleteAppOrder extends AbstractAppOrder {

    @Autowired
    private AppService appService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private TopicConnectionService connectionService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderDetailDeleteAppDTO orderDetailDTO = new OrderDetailDeleteAppDTO();
        OrderExtensionDeleteAppDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionDeleteAppDTO.class);
        orderDetailDTO.setAppId(orderExtensionDTO.getAppId());

        AppDO appDO = appService.getByAppId(orderExtensionDTO.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return orderDetailDTO;
        }
        orderDetailDTO.setPassword(appDO.getPassword());
        orderDetailDTO.setPrincipals(appDO.getPrincipals());
        orderDetailDTO.setName(appDO.getName());

        List<TopicConnection> connectionDTOList = connectionService.getByAppId(
                appDO.getAppId(),
                new Date(System.currentTimeMillis() - Constant.TOPIC_CONNECTION_LATEST_TIME_MS),
                new Date());
        orderDetailDTO.setConnectionList(connectionDTOList);
        return orderDetailDTO;
    }

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionDeleteAppDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionDeleteAppDTO.class);
        if (!orderExtensionDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        if (ValidateUtils.isNull(appService.getByAppId(orderExtensionDTO.getAppId()))) {
            return Result.buildFrom(ResultStatus.APP_NOT_EXIST);
        }
        String title = String.format(
                "%s-%s",
                OrderTypeEnum.DELETE_APP.getMessage(),
                orderExtensionDTO.getAppId()
        );
        return new Result<>(title);
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO, OrderHandleBaseDTO orderHandleBaseDTO, String userName) {
        OrderExtensionDeleteAppDTO orderAppExtension = JSONObject.parseObject(
                orderDO.getExtensions(),
                OrderExtensionDeleteAppDTO.class);
        AppDO appDO = appService.getByAppId(orderAppExtension.getAppId());
        if (ValidateUtils.isNull(appDO)) {
            return ResultStatus.APP_NOT_EXIST;
        }
        // 判断app是否对topic有权限
        List<AuthorityDO> authorityList = authorityService.getAuthority(orderAppExtension.getAppId());
        if (!ValidateUtils.isEmptyList(authorityList)) {
            return ResultStatus.APP_OFFLINE_FORBIDDEN;
        }
        if (appService.deleteApp(appDO, userName) > 0) {
            return ResultStatus.SUCCESS;
        }
        return ResultStatus.OPERATION_FAILED;
    }
}
