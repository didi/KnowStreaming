package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.po.*;
import com.xiaojukeji.kafka.manager.web.model.order.OrderPartitionModel;
import com.xiaojukeji.kafka.manager.web.model.order.OrderTopicModel;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.OrderStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import com.xiaojukeji.kafka.manager.web.vo.order.OrderPartitionVO;
import com.xiaojukeji.kafka.manager.web.vo.order.OrderTopicVO;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zengqiao
 * @date 19/6/18
 */
public class OrderConverter {
    public static OrderTopicDO convert2OrderTopicDO(ClusterDO clusterDO,
                                                    String applicant,
                                                    OrderTopicModel orderTopicModel) {
        OrderTopicDO orderTopicDO = new OrderTopicDO();
        orderTopicDO.setId(orderTopicModel.getOrderId());
        orderTopicDO.setClusterId(clusterDO.getId());
        orderTopicDO.setClusterName(clusterDO.getClusterName());
        orderTopicDO.setTopicName(orderTopicModel.getTopicName());
        orderTopicDO.setApplicant(applicant);
        orderTopicDO.setPrincipals(ListUtils.strList2String(orderTopicModel.getPrincipalList()));
        orderTopicDO.setPeakBytesIn(orderTopicModel.getPeakBytesIn().longValue());
        orderTopicDO.setPartitionNum(0);
        orderTopicDO.setRetentionTime(orderTopicModel.getRetentionTime() * 60 * 60 * 1000);
        orderTopicDO.setReplicaNum(3);
        orderTopicDO.setDescription(orderTopicModel.getDescription());
        return orderTopicDO;
    }

    public static List<OrderTopicVO> convert2OrderTopicVOList(List<OrderTopicDO> orderTopicDOList) {
        if (orderTopicDOList == null) {
            return new ArrayList<>();
        }
        List<OrderTopicVO> orderTopicVOList = new ArrayList<>();
        for (OrderTopicDO orderTopicDO: orderTopicDOList) {
            OrderTopicVO orderTopicVO = new OrderTopicVO();
            CopyUtils.copyProperties(orderTopicVO, orderTopicDO);
            orderTopicVO.setOrderId(orderTopicDO.getId());
            orderTopicVO.setGmtCreate(orderTopicDO.getGmtCreate().getTime());
            orderTopicVO.setGmtModify(orderTopicDO.getGmtModify().getTime());
            orderTopicVOList.add(orderTopicVO);
        }
        return orderTopicVOList;
    }

    public static OrderPartitionDO convert2OrderPartitionDO(ClusterDO clusterDO,
                                                            String applicant,
                                                            OrderPartitionModel orderPartitionModel) {
        OrderPartitionDO orderPartitionDO = new OrderPartitionDO();
        orderPartitionDO.setId(orderPartitionModel.getOrderId());
        orderPartitionDO.setClusterId(clusterDO.getId());
        orderPartitionDO.setClusterName(clusterDO.getClusterName());
        orderPartitionDO.setTopicName(orderPartitionModel.getTopicName());
        orderPartitionDO.setApplicant(applicant);
        orderPartitionDO.setPeakBytesIn(orderPartitionModel.getPredictBytesIn().longValue());
        orderPartitionDO.setOrderStatus(OrderStatusEnum.WAIT_DEAL.getCode());
        orderPartitionDO.setDescription(orderPartitionModel.getDescription());
        return orderPartitionDO;
    }

    public static List<OrderPartitionVO> convert2OrderPartitionVOList(List<OrderPartitionDO> orderPartitionDOList) {
        if (orderPartitionDOList == null) {
            return new ArrayList<>();
        }
        List<OrderPartitionVO> orderPartitionVOList = new ArrayList<>();
        for (OrderPartitionDO orderPartitionDO: orderPartitionDOList) {
            orderPartitionVOList.add(
                    convert2OrderPartitionVO(orderPartitionDO, null,null, null)
            );
        }
        return orderPartitionVOList;
    }

    public static OrderPartitionVO convert2OrderPartitionVO(OrderPartitionDO orderPartitionDO,
                                                            TopicMetadata topicMetadata,
                                                            Long maxAvgBytes, List<RegionDO> regionDOList) {
        if (orderPartitionDO == null) {
            return null;
        }
        OrderPartitionVO orderPartitionVO = new OrderPartitionVO();
        CopyUtils.copyProperties(orderPartitionVO, orderPartitionDO);
        orderPartitionVO.setOrderId(orderPartitionDO.getId());
        orderPartitionVO.setPredictBytesIn(orderPartitionDO.getPeakBytesIn());
        orderPartitionVO.setGmtCreate(orderPartitionDO.getGmtCreate().getTime());
        orderPartitionVO.setGmtModify(orderPartitionDO.getGmtModify().getTime());
        orderPartitionVO.setRealBytesIn(maxAvgBytes);
        if (topicMetadata == null) {
            return orderPartitionVO;
        }
        orderPartitionVO.setPartitionNum(topicMetadata.getPartitionNum());
        orderPartitionVO.setBrokerIdList(new ArrayList<>(topicMetadata.getBrokerIdSet()));

        if (regionDOList == null || regionDOList.isEmpty()) {
            orderPartitionVO.setRegionNameList(new ArrayList<>());
            orderPartitionVO.setRegionBrokerIdList(new ArrayList<>());
            return orderPartitionVO;
        }
        Set<String> regionNameSet = new HashSet<>();
        Set<Integer> brokerIdSet = new HashSet<>();
        for (RegionDO regionDO: regionDOList) {
            regionNameSet.add(regionDO.getRegionName());
            if (StringUtils.isEmpty(regionDO.getBrokerList())) {
                continue;
            }
            brokerIdSet.addAll(ListUtils.string2IntList(regionDO.getBrokerList()));
        }
        orderPartitionVO.setRegionNameList(new ArrayList<>(regionNameSet));
        orderPartitionVO.setRegionBrokerIdList(new ArrayList<>(brokerIdSet));

        return orderPartitionVO;
    }

    public static TopicDO convert2TopicInfoDO(OrderTopicDO orderTopicDO) {
        TopicDO topicDO = new TopicDO();
        topicDO.setClusterId(orderTopicDO.getClusterId());
        topicDO.setTopicName(orderTopicDO.getTopicName());
        topicDO.setApplicant(orderTopicDO.getApplicant());
        topicDO.setPrincipals(orderTopicDO.getPrincipals());
        topicDO.setDescription(orderTopicDO.getDescription());
        topicDO.setStatus(DBStatusEnum.NORMAL.getStatus());
        return topicDO;
    }


}