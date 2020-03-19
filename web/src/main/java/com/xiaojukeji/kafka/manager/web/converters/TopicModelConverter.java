package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.web.model.topic.TopicFavorite;
import com.xiaojukeji.kafka.manager.common.entity.dto.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicPartitionDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicFavoriteDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionMap;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import com.xiaojukeji.kafka.manager.web.vo.topic.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author arthur
 * @date 2017/6/1.
 */
public class TopicModelConverter {
    public static List<TopicFavoriteDO> convert2TopicFavoriteDOList(String username,
                                                                    List<TopicFavorite> topicFavoriteList) {
        if (topicFavoriteList == null) {
            return new ArrayList<>();
        }
        List<TopicFavoriteDO> topicFavoriteDOList = new ArrayList<>();
        for (TopicFavorite topicFavorite: topicFavoriteList) {
            TopicFavoriteDO topicFavoriteDO = new TopicFavoriteDO();
            topicFavoriteDO.setTopicName(topicFavorite.getTopicName());
            topicFavoriteDO.setClusterId(topicFavorite.getClusterId());
            topicFavoriteDO.setUsername(username);
            topicFavoriteDOList.add(topicFavoriteDO);
        }
        return topicFavoriteDOList;
    }

    public static TopicBasicVO convert2TopicBasicVO(TopicBasicDTO topicBasicDTO,
                                                    TopicDO topicDO,
                                                    List<RegionDO> regionList) {
        TopicBasicVO topicBasicVO = new TopicBasicVO();
        CopyUtils.copyProperties(topicBasicVO, topicBasicDTO);
        if (topicDO != null) {
            topicBasicVO.setDescription(topicDO.getDescription());
            topicBasicVO.setPrincipals(topicDO.getPrincipals());
        }
        if (regionList == null) {
            return topicBasicVO;
        }
        List<String> regionNameList = regionList.stream().map(elem -> elem.getRegionName()).collect(Collectors.toList());
        topicBasicVO.setRegionNames(ListUtils.strList2String(regionNameList));
        return topicBasicVO;
    }

    public static List<TopicOverviewVO> convert2TopicOverviewVOList(ClusterDO cluster,
                                                                    List<TopicOverviewDTO> topicOverviewDTOList,
                                                                    List<TopicDO> topicDOList,
                                                                    List<TopicFavoriteDO> topicFavoriteDOList) {
        if (topicOverviewDTOList == null) {
            return new ArrayList<>();
        }
        Map<String, TopicFavoriteDO> favoriteMap = new HashMap<>(0);
        if (topicDOList != null) {
            favoriteMap = topicFavoriteDOList.stream().filter(elem -> cluster.getId().equals(elem.getClusterId())).collect(Collectors.toMap(TopicFavoriteDO::getTopicName, elem -> elem));
        }

        Map<String, String> principalMap = new HashMap<>(0);
        if (topicDOList != null) {
            principalMap = topicDOList.stream().collect(Collectors.toMap(TopicDO::getTopicName, TopicDO::getPrincipals));
        }
        List<TopicOverviewVO> topicInfoVOList = new ArrayList<>();
        for (TopicOverviewDTO topicOverviewDTO: topicOverviewDTOList) {
            TopicOverviewVO topicInfoVO = new TopicOverviewVO();
            topicInfoVO.setClusterId(cluster.getId());
            topicInfoVO.setClusterName(cluster.getClusterName());
            topicInfoVO.setTopicName(topicOverviewDTO.getTopicName());
            topicInfoVO.setPartitionNum(topicOverviewDTO.getPartitionNum());
            topicInfoVO.setUpdateTime(topicOverviewDTO.getUpdateTime());
            topicInfoVO.setReplicaNum(topicOverviewDTO.getReplicaNum());
            topicInfoVO.setByteIn(topicOverviewDTO.getBytesInPerSec());
            topicInfoVO.setProduceRequest(topicOverviewDTO.getProduceRequestPerSec());
            topicInfoVO.setPrincipals(principalMap.get(topicOverviewDTO.getTopicName()));
            if (favoriteMap.containsKey(topicOverviewDTO.getTopicName())) {
                topicInfoVO.setFavorite(Boolean.TRUE);
            } else {
                topicInfoVO.setFavorite(Boolean.FALSE);
            }
            topicInfoVOList.add(topicInfoVO);
        }
        return topicInfoVOList;
    }

    /**
     * 构建TopicBrokerVO
     */
    public static List<TopicBrokerVO> convert2TopicBrokerVOList(ClusterDO clusterDO,
                                                                TopicMetadata topicMetadata,
                                                                List<TopicPartitionDTO> topicPartitionDTOList){
        if(clusterDO == null || topicMetadata == null){
            return new ArrayList<>();
        }
        PartitionMap partitionMap = topicMetadata.getPartitionMap();

        Map<Integer, TopicBrokerVO> brokerIdTopicBrokerVOMap = new HashMap<>();
        for (Integer brokerId: topicMetadata.getBrokerIdSet()) {
            TopicBrokerVO topicBrokerVO = new TopicBrokerVO();
            topicBrokerVO.setBrokerId(brokerId);

            List<Integer> partitionIdList = new ArrayList<>();
            for (Integer partitionId: partitionMap.getPartitions().keySet()) {
                if (partitionMap.getPartitions().get(partitionId).contains(brokerId)) {
                    partitionIdList.add(partitionId);
                }
            }
            topicBrokerVO.setPartitionIdList(partitionIdList);
            topicBrokerVO.setPartitionNum(partitionIdList.size());
            topicBrokerVO.setLeaderPartitionIdList(new ArrayList<Integer>());

            // 设置Broker的主机名
            BrokerMetadata brokerMetadata = ClusterMetadataManager.getBrokerMetadata(clusterDO.getId(), brokerId);
            if (brokerMetadata != null) {
                topicBrokerVO.setHost(brokerMetadata.getHost());
            } else {
                topicBrokerVO.setHost("");
            }
            brokerIdTopicBrokerVOMap.put(brokerId, topicBrokerVO);
        }

        for (TopicPartitionDTO topicPartitionDTO: topicPartitionDTOList) {
            Integer leaderBrokerId = topicPartitionDTO.getLeaderBrokerId();
            if (!brokerIdTopicBrokerVOMap.containsKey(leaderBrokerId)) {
                // 存在异常分区
                continue;
            }
            brokerIdTopicBrokerVOMap.get(leaderBrokerId).getLeaderPartitionIdList().add(topicPartitionDTO.getPartitionId());
        }
        return new ArrayList<>(brokerIdTopicBrokerVOMap.values());
    }

    public static TopicRealTimeMetricsVO convert2TopicRealTimeMetricsVO(TopicMetrics topicMetrics){
        TopicRealTimeMetricsVO topicRealTimeMetricsVO = new TopicRealTimeMetricsVO();
        List<Double> messageIn = new ArrayList<>();
        messageIn.add(topicMetrics.getMessagesInPerSecMeanRate());
        messageIn.add(topicMetrics.getMessagesInPerSec());
        messageIn.add(topicMetrics.getMessagesInPerSecFiveMinuteRate());
        messageIn.add(topicMetrics.getMessagesInPerSecFifteenMinuteRate());
        topicRealTimeMetricsVO.setMessageIn(messageIn);

        List<Double> byteIn = new ArrayList<>();
        byteIn.add(topicMetrics.getBytesInPerSecMeanRate());
        byteIn.add(topicMetrics.getBytesInPerSec());
        byteIn.add(topicMetrics.getBytesInPerSecFiveMinuteRate());
        byteIn.add(topicMetrics.getBytesInPerSecFifteenMinuteRate());
        topicRealTimeMetricsVO.setByteIn(byteIn);

        List<Double> byteOut = new ArrayList<>();
        byteOut.add(topicMetrics.getBytesOutPerSecMeanRate());
        byteOut.add(topicMetrics.getBytesOutPerSec());
        byteOut.add(topicMetrics.getBytesOutPerSecFiveMinuteRate());
        byteOut.add(topicMetrics.getBytesOutPerSecFiveMinuteRate());
        topicRealTimeMetricsVO.setByteOut(byteOut);

        List<Double> byteRejected = new ArrayList<>();
        byteRejected.add(topicMetrics.getBytesRejectedPerSecMeanRate());
        byteRejected.add(topicMetrics.getBytesRejectedPerSec());
        byteRejected.add(topicMetrics.getBytesRejectedPerSecFiveMinuteRate());
        byteRejected.add(topicMetrics.getBytesRejectedPerSecFifteenMinuteRate());
        topicRealTimeMetricsVO.setByteRejected(byteRejected);

        List<Double> failedFetchRequest = new ArrayList<>();
        failedFetchRequest.add(topicMetrics.getFailFetchRequestPerSecMeanRate());
        failedFetchRequest.add(topicMetrics.getFailFetchRequestPerSec());
        failedFetchRequest.add(topicMetrics.getFailFetchRequestPerSecFiveMinuteRate());
        failedFetchRequest.add(topicMetrics.getFailFetchRequestPerSecFifteenMinuteRate());
        topicRealTimeMetricsVO.setFailedFetchRequest(failedFetchRequest);

        List<Double> failedProduceRequest = new ArrayList<>();
        failedProduceRequest.add(topicMetrics.getFailProduceRequestPerSecMeanRate());
        failedProduceRequest.add(topicMetrics.getFailProduceRequestPerSec());
        failedProduceRequest.add(topicMetrics.getFailProduceRequestPerSecFiveMinuteRate());
        failedProduceRequest.add(topicMetrics.getFailFetchRequestPerSecFifteenMinuteRate());
        topicRealTimeMetricsVO.setFailedProduceRequest(failedProduceRequest);

        List<Double> totalProduceRequest = new ArrayList<>();
        totalProduceRequest.add(topicMetrics.getTotalProduceRequestsPerSecMeanRate());
        totalProduceRequest.add(topicMetrics.getTotalProduceRequestsPerSec());
        totalProduceRequest.add(topicMetrics.getTotalProduceRequestsPerSecFiveMinuteRate());
        totalProduceRequest.add(topicMetrics.getTotalProduceRequestsPerSecFifteenMinuteRate());
        topicRealTimeMetricsVO.setTotalProduceRequest(totalProduceRequest);

        List<Double> totalFetchRequest = new ArrayList<>();
        totalFetchRequest.add(topicMetrics.getTotalFetchRequestsPerSecMeanRate());
        totalFetchRequest.add(topicMetrics.getTotalFetchRequestsPerSec());
        totalFetchRequest.add(topicMetrics.getTotalFetchRequestsPerSecFiveMinuteRate());
        totalFetchRequest.add(topicMetrics.getTotalFetchRequestsPerSecFifteenMinuteRate());
        topicRealTimeMetricsVO.setTotalFetchRequest(totalFetchRequest);
        return topicRealTimeMetricsVO;
    }

    public static List<TopicOffsetVO> convert2TopicOffsetVOList(Long clusterId,
                                                                String topicName,
                                                                List<PartitionOffsetDTO> partitionOffsetDTOList) {
        List<TopicOffsetVO> topicOffsetVOList = new ArrayList<>();
        for (PartitionOffsetDTO partitionOffsetDTO: partitionOffsetDTOList) {
            topicOffsetVOList.add(new TopicOffsetVO(clusterId, topicName, partitionOffsetDTO.getPartitionId(), partitionOffsetDTO.getOffset(), partitionOffsetDTO.getTimestamp()));
        }
        return topicOffsetVOList;
    }

    public static List<TopicPartitionVO> convert2TopicPartitionVOList(List<TopicPartitionDTO> topicPartitionDTOList) {
        List<TopicPartitionVO> topicPartitionVOList = new ArrayList<>();
        for (TopicPartitionDTO topicPartitionDTO: topicPartitionDTOList) {
            TopicPartitionVO topicPartitionVO = new TopicPartitionVO();
            topicPartitionVO.setPartitionId(topicPartitionDTO.getPartitionId());
            topicPartitionVO.setOffset(topicPartitionDTO.getOffset());
            topicPartitionVO.setLeaderBrokerId(topicPartitionDTO.getLeaderBrokerId());
            topicPartitionVO.setPreferredBrokerId(topicPartitionDTO.getPreferredBrokerId());
            topicPartitionVO.setLeaderEpoch(topicPartitionDTO.getLeaderEpoch());
            topicPartitionVO.setReplicaBrokerIdList(topicPartitionDTO.getReplicasBroker());
            topicPartitionVO.setIsrBrokerIdList(topicPartitionDTO.getIsr());
            topicPartitionVO.setUnderReplicated(topicPartitionDTO.isUnderReplicated());
            topicPartitionVOList.add(topicPartitionVO);
        }
        return topicPartitionVOList;
    }

    public static TopicMetadataVO convert2TopicMetadataVO(Long clusterId, TopicMetadata topicMetadata) {
        TopicMetadataVO topicMetadataVO = new TopicMetadataVO();
        topicMetadataVO.setClusterId(clusterId);
        topicMetadataVO.setTopicName(topicMetadata.getTopic());
        topicMetadataVO.setBrokerIdList(new ArrayList<>(topicMetadata.getBrokerIdSet()));
        topicMetadataVO.setReplicaNum(topicMetadata.getReplicaNum());
        topicMetadataVO.setPartitionNum(topicMetadata.getPartitionNum());
        topicMetadataVO.setModifyTime(topicMetadata.getModifyTime());
        topicMetadataVO.setCreateTime(topicMetadata.getCreateTime());
        return topicMetadataVO;
    }
}
