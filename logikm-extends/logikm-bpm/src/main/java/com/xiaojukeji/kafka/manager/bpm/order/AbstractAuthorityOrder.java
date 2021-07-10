package com.xiaojukeji.kafka.manager.bpm.order;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionAuthorityDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zhongyuankai
 * @date 2020/5/19
 */
public abstract class AbstractAuthorityOrder extends AbstractOrder {

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private AppService appService;

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionAuthorityDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionAuthorityDTO.class);
        if (!orderExtensionDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(
                orderExtensionDTO.getClusterId(),
                orderExtensionDTO.isPhysicalClusterId()
        );
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if (!PhysicalClusterMetadataManager.isTopicExist(physicalClusterId, orderExtensionDTO.getTopicName())) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }
        if (ValidateUtils.isNull(appService.getByAppId(orderExtensionDTO.getAppId()))) {
            return Result.buildFrom(ResultStatus.APP_NOT_EXIST);
        }

        String title = String.format(
                "权限工单-%d-%s",
                orderExtensionDTO.getClusterId(),
                orderExtensionDTO.getTopicName()
        );
        return new Result<>(title);
    }
}
