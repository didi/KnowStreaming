package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicAnomalyFlow;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicInsufficientPartition;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicRegionHot;
import com.xiaojukeji.kafka.manager.common.entity.vo.op.expert.*;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/3/29
 */
public class ExpertConverter {
    public static List<RegionHotTopicVO> convert2RegionHotTopicVOList(List<TopicRegionHot> hotTopicList) {
        if (ValidateUtils.isEmptyList(hotTopicList)) {
            return new ArrayList<>();
        }
        List<RegionHotTopicVO> voList = new ArrayList<>();
        for (TopicRegionHot hotTopic: hotTopicList) {
            RegionHotTopicVO vo = new RegionHotTopicVO();
            vo.setClusterId(hotTopic.getClusterDO().getId());
            vo.setClusterName(hotTopic.getClusterDO().getClusterName());
            vo.setTopicName(hotTopic.getTopicName());

            vo.setDetailList(new ArrayList<>());
            for (Map.Entry<Integer, Integer> entry: hotTopic.getBrokerIdPartitionNumMap().entrySet()) {
                BrokerIdPartitionNumVO numVO = new BrokerIdPartitionNumVO();
                numVO.setBrokeId(entry.getKey());
                numVO.setPartitionNum(entry.getValue());
                vo.getDetailList().add(numVO);
            }
            vo.setRetentionTime(hotTopic.getRetentionTime());
            voList.add(vo);
        }
        return voList;
    }

    public static List<PartitionInsufficientTopicVO> convert2PartitionInsufficientTopicVOList(
            List<TopicInsufficientPartition> dataList) {
        if (ValidateUtils.isEmptyList(dataList)) {
            return new ArrayList<>();
        }

        List<PartitionInsufficientTopicVO> voList = new ArrayList<>();
        for (TopicInsufficientPartition elem: dataList) {
            PartitionInsufficientTopicVO vo = new PartitionInsufficientTopicVO();
            vo.setClusterId(elem.getClusterDO().getId());
            vo.setClusterName(elem.getClusterDO().getClusterName());
            vo.setTopicName(elem.getTopicName());
            vo.setBrokerIdList(elem.getBrokerIdList());
            vo.setPresentPartitionNum(elem.getPresentPartitionNum());
            vo.setSuggestedPartitionNum(elem.getSuggestedPartitionNum());
            vo.setBytesInPerPartition(elem.getBytesInPerPartition());
            vo.setMaxAvgBytesInList(elem.getMaxAvgBytesInList());
            voList.add(vo);
        }
        return voList;
    }

    public static List<AnomalyFlowTopicVO> convert2AnomalyFlowTopicVOList(List<TopicAnomalyFlow> anomalyFlowList) {
        if (ValidateUtils.isEmptyList(anomalyFlowList)) {
            return new ArrayList<>();
        }
        List<AnomalyFlowTopicVO> voList = new ArrayList<>();
        for (TopicAnomalyFlow anomalyFlow: anomalyFlowList) {
            AnomalyFlowTopicVO vo = new AnomalyFlowTopicVO();
            vo.setClusterId(anomalyFlow.getClusterId());
            vo.setClusterName(anomalyFlow.getClusterName());
            vo.setTopicName(anomalyFlow.getTopicName());
            vo.setBytesIn(anomalyFlow.getBytesIn());
            vo.setBytesInIncr(anomalyFlow.getBytesInIncr());
            vo.setIops(anomalyFlow.getIops());
            vo.setIopsIncr(anomalyFlow.getIopsIncr());
            voList.add(vo);
        }
        return voList;
    }

    public static List<ExpiredTopicVO> convert2ExpiredTopicVOList(List<TopicExpiredDO> topicExpiredDOList,
                                                                  List<ClusterDO> clusterDOList) {
        if (ValidateUtils.isEmptyList(topicExpiredDOList)) {
            return new ArrayList<>();
        }
        if (ValidateUtils.isEmptyList(clusterDOList)) {
            clusterDOList = new ArrayList<>();
        }
        Map<Long, String> clusterMap = new HashMap<>(0);
        for (ClusterDO clusterDO: clusterDOList) {
            clusterMap.put(clusterDO.getId(), clusterDO.getClusterName());
        }

        List<ExpiredTopicVO> voList = new ArrayList<>();
        for (TopicExpiredDO topicExpiredDO: topicExpiredDOList) {
            ExpiredTopicVO expiredTopicVO = new ExpiredTopicVO();
            expiredTopicVO.setClusterId(topicExpiredDO.getClusterId());
            expiredTopicVO.setClusterName(clusterMap.getOrDefault(topicExpiredDO.getClusterId(), ""));
            expiredTopicVO.setTopicName(topicExpiredDO.getTopicName());
            expiredTopicVO.setExpiredDay(topicExpiredDO.getExpiredDay());
            expiredTopicVO.setStatus(topicExpiredDO.getStatus());
            voList.add(expiredTopicVO);
        }
        return voList;
    }
}