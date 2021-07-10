package com.xiaojukeji.kafka.manager.bpm.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.bpm.common.OrderTypeEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.bpm.common.entry.apply.OrderExtensionDeleteClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.AbstractOrderDetailData;
import com.xiaojukeji.kafka.manager.bpm.common.entry.detail.OrderDetailDeleteClusterDTO;
import com.xiaojukeji.kafka.manager.bpm.common.handle.OrderHandleBaseDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.bpm.order.AbstractClusterOrder;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
@Component("deleteClusterOrder")
public class DeleteClusterOrder extends AbstractClusterOrder {
    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private ClusterService clusterService;

    @Override
    public AbstractOrderDetailData getOrderExtensionDetailData(String extensions) {
        OrderDetailDeleteClusterDTO orderDetailDTO = new OrderDetailDeleteClusterDTO();
        OrderExtensionDeleteClusterDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionDeleteClusterDTO.class);
        ClusterNameDTO clusterNameDTO = clusterService.getClusterName(orderExtensionDTO.getClusterId());
        orderDetailDTO.setLogicalClusterId(clusterNameDTO.getLogicalClusterId());
        orderDetailDTO.setLogicalClusterName(clusterNameDTO.getLogicalClusterName());
        orderDetailDTO.setPhysicalClusterId(clusterNameDTO.getPhysicalClusterId());
        orderDetailDTO.setPhysicalClusterName(clusterNameDTO.getPhysicalClusterName());

        orderDetailDTO.setTopicNameList(new ArrayList<>(
                logicalClusterMetadataManager.getTopicNameSet(clusterNameDTO.getLogicalClusterId())));
        return orderDetailDTO;
    }

    @Override
    public Result<String> checkExtensionFieldsAndGenerateTitle(String extensions) {
        OrderExtensionDeleteClusterDTO orderExtensionDTO = JSONObject.parseObject(
                extensions,
                OrderExtensionDeleteClusterDTO.class);
        if (!orderExtensionDTO.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        Long physicalClusterId = logicalClusterMetadataManager.getPhysicalClusterId(orderExtensionDTO.getClusterId());
        if (ValidateUtils.isNull(physicalClusterId)) {
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        String title = String.format(
                "%s-%d",
                OrderTypeEnum.DELETE_CLUSTER.getMessage(),
                orderExtensionDTO.getClusterId()
        );
        return new Result<>(title);
    }

    @Override
    public ResultStatus handleOrderDetail(OrderDO orderDO,
                                          OrderHandleBaseDTO orderHandleBaseDTO,
                                          String userName) {
        OrderExtensionDeleteClusterDTO orderExtensionDTO = JSONObject.parseObject(
                orderDO.getExtensions(),
                OrderExtensionDeleteClusterDTO.class);
        Set<String> topicNameSet = logicalClusterMetadataManager.getTopicNameSet(orderExtensionDTO.getClusterId());
        if (!ValidateUtils.isEmptySet(topicNameSet)) {
            return ResultStatus.OPERATION_FORBIDDEN;
        }
        return ResultStatus.SUCCESS;
    }
}
