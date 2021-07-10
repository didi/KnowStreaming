package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionModifyClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailModifyClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractClusterOrder;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhongyuankai
 * @date 2020/5/19
 */
@Component("modifyClusterOrder")
public class ModifyClusterOrder extends AbstractClusterOrder {
    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private ClusterService clusterService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderDetailModifyClusterDTO orderDetailDTO = new OrderDetailModifyClusterDTO();
        OrderExtensionModifyClusterDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionModifyClusterDTO.class);
        ClusterNameDTO clusterNameDTO = clusterService.getClusterName(orderExtensionDTO.getClusterId());
        orderDetailDTO.setLogicalClusterId(clusterNameDTO.getLogicalClusterId());
        orderDetailDTO.setLogicalClusterName(clusterNameDTO.getLogicalClusterName());
        orderDetailDTO.setPhysicalClusterId(clusterNameDTO.getPhysicalClusterId());
        orderDetailDTO.setPhysicalClusterName(clusterNameDTO.getPhysicalClusterName());
        return orderDetailDTO;
    }

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionModifyClusterDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionModifyClusterDTO.class);
        if (!orderExtensionDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId =
                logicalClusterMetadataManager.getPhysicalClusterId(orderExtensionDTO.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        String title = String.format(
                "集群修改-%d",
                orderExtensionDTO.getClusterId()
        );
        return new Result<>(title);
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO,
                                          OrderHandleBaseDTO orderHandleBaseDTO,
                                          String userName) {
        return ResultStatus.SUCCESS;
    }
}
