package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.ao.analysis.AnalysisBrokerDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.analysis.AnalysisTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker.*;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;

import java.util.*;

/**
 * @author zengqiao
 * @date 19/4/21
 */
public class BrokerModelConverter {

    public static List<BrokerPartitionVO> convert2BrokerPartitionVOList(Long clusterId,
                                                                        Integer brokerId,
                                                                        Map<String, List<PartitionState>> stateMap) {
        List<BrokerPartitionVO> voList = new ArrayList<>();
        for (String topicName : stateMap.keySet()) {
            BrokerPartitionVO vo = convert2BrokerPartitionsVO(clusterId, brokerId, topicName, stateMap.get(topicName));
            if (ValidateUtils.isNull(vo)) {
                continue;
            }
            voList.add(vo);
        }
        return voList;
    }

    private static BrokerPartitionVO convert2BrokerPartitionsVO(Long clusterId,
                                                                Integer brokerId,
                                                                String topicName,
                                                                List<PartitionState> stateList) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (ValidateUtils.isNull(stateList) || ValidateUtils.isNull(topicMetadata)) {
            return null;
        }

        Set<Integer> leaderPartitionIdSet = new HashSet<>();
        Set<Integer> followerPartitionIdSet = new HashSet<>();
        Set<Integer> notUnderReplicatedPartitionIdSet = new HashSet<>();
        for (PartitionState partitionState : stateList) {
            List<Integer> replicaIdList =
                    topicMetadata.getPartitionMap().getPartitions().get(partitionState.getPartitionId());
            if (brokerId.equals(partitionState.getLeader())) {
                leaderPartitionIdSet.add(partitionState.getPartitionId());
            }
            if (replicaIdList.contains(brokerId)) {
                followerPartitionIdSet.add(partitionState.getPartitionId());
            }
            if (replicaIdList.contains(brokerId) && partitionState.getIsr().size() < replicaIdList.size()) {
                notUnderReplicatedPartitionIdSet.add(partitionState.getPartitionId());
            }
        }
        BrokerPartitionVO vo = new BrokerPartitionVO();
        vo.setTopicName(topicName);
        vo.setLeaderPartitionList(new ArrayList<>(leaderPartitionIdSet));
        vo.setFollowerPartitionIdList(new ArrayList<>(followerPartitionIdSet));
        vo.setNotUnderReplicatedPartitionIdList(new ArrayList<>(notUnderReplicatedPartitionIdSet));
        vo.setUnderReplicated(notUnderReplicatedPartitionIdSet.isEmpty());
        return vo;
    }

    public static List<BrokerMetricsVO> convert2BrokerMetricsVOList(List<BrokerMetricsDO> metricsDOList) {
        if (ValidateUtils.isNull(metricsDOList)) {
            return new ArrayList<>();
        }
        List<BrokerMetrics> metricsList = MetricsConvertUtils.convert2BrokerMetricsList(metricsDOList);

        List<BrokerMetricsVO> voList = new ArrayList<>();
        for (BrokerMetrics metrics : metricsList) {
            if (ValidateUtils.isNull(metrics)) {
                continue;
            }
            BrokerMetricsVO vo = new BrokerMetricsVO();
            vo.setHealthScore(metrics.getSpecifiedMetrics(JmxConstant.HEALTH_SCORE, Integer.class));
            vo.setBytesInPerSec(
                    metrics.getSpecifiedMetrics("BytesInPerSecOneMinuteRate")
            );
            vo.setBytesOutPerSec(
                    metrics.getSpecifiedMetrics("BytesOutPerSecOneMinuteRate")
            );
            vo.setBytesRejectedPerSec(
                    metrics.getSpecifiedMetrics("BytesRejectedPerSecOneMinuteRate")
            );
            vo.setMessagesInPerSec(
                    metrics.getSpecifiedMetrics("MessagesInPerSecOneMinuteRate")
            );
            vo.setProduceRequestPerSec(
                    metrics.getSpecifiedMetrics("ProduceRequestsPerSecOneMinuteRate")
            );
            vo.setFetchConsumerRequestPerSec(
                    metrics.getSpecifiedMetrics("FetchConsumerRequestsPerSecOneMinuteRate")
            );
            vo.setRequestHandlerIdlPercent(
                    metrics.getSpecifiedMetrics("RequestHandlerAvgIdlePercentOneMinuteRate")
            );
            vo.setNetworkProcessorIdlPercent(
                    metrics.getSpecifiedMetrics("NetworkProcessorAvgIdlePercentValue")
            );
            vo.setRequestQueueSize(
                    metrics.getSpecifiedMetrics("RequestQueueSizeValue", Integer.class)
            );
            vo.setResponseQueueSize(
                    metrics.getSpecifiedMetrics("ResponseQueueSizeValue", Integer.class)
            );
            vo.setLogFlushTime(
                    metrics.getSpecifiedMetrics("LogFlushRateAndTimeMs95thPercentile")
            );
            vo.setFailFetchRequestPerSec(
                    metrics.getSpecifiedMetrics("FailedFetchRequestsPerSecOneMinuteRate")
            );
            vo.setFailProduceRequestPerSec(
                    metrics.getSpecifiedMetrics("FailedProduceRequestsPerSecOneMinuteRate")
            );
            vo.setTotalTimeFetchConsumer99Th(
                    metrics.getSpecifiedMetrics("FetchConsumerTotalTimeMs99thPercentile")
            );
            vo.setTotalTimeProduce99Th(
                    metrics.getSpecifiedMetrics("ProduceTotalTimeMs99thPercentile")
            );
            vo.setGmtCreate(
                    metrics.getSpecifiedMetrics("CreateTime", Long.class)
            );
            voList.add(vo);
        }
        return voList;
    }


    public static AnalysisBrokerVO convert2AnalysisBrokerVO(AnalysisBrokerDTO analysisBrokerDTO) {
        if (analysisBrokerDTO == null) {
            return null;
        }
        AnalysisBrokerVO analysisBrokerVO = new AnalysisBrokerVO();
        analysisBrokerVO.setClusterId(analysisBrokerDTO.getClusterId());
        analysisBrokerVO.setBrokerId(analysisBrokerDTO.getBrokerId());
        analysisBrokerVO.setBaseTime(System.currentTimeMillis());
        analysisBrokerVO.setTopicAnalysisVOList(new ArrayList<>());
        analysisBrokerVO.setBytesIn(analysisBrokerDTO.getBytesIn());
        analysisBrokerVO.setBytesOut(analysisBrokerDTO.getBytesOut());
        analysisBrokerVO.setMessagesIn(analysisBrokerDTO.getMessagesIn());
        analysisBrokerVO.setTotalProduceRequests(analysisBrokerDTO.getTotalProduceRequests());
        analysisBrokerVO.setTotalFetchRequests(analysisBrokerDTO.getTotalFetchRequests());
        for (AnalysisTopicDTO analysisTopicDTO : analysisBrokerDTO.getTopicAnalysisVOList()) {
            AnalysisTopicVO analysisTopicVO = new AnalysisTopicVO();
            analysisTopicVO.setTopicName(analysisTopicDTO.getTopicName());
            analysisTopicVO.setBytesIn(String.format("%.2f", analysisTopicDTO.getBytesIn()));
            analysisTopicVO.setBytesInRate(String.format("%.2f", analysisTopicDTO.getBytesInRate()));
            analysisTopicVO.setBytesOut(String.format("%.2f", analysisTopicDTO.getBytesOut()));
            analysisTopicVO.setBytesOutRate(String.format("%.2f", analysisTopicDTO.getBytesOutRate()));
            analysisTopicVO.setMessagesIn(String.format("%.2f", analysisTopicDTO.getMessagesIn()));
            analysisTopicVO.setMessagesInRate(String.format("%.2f", analysisTopicDTO.getMessagesInRate()));
            analysisTopicVO.setTotalFetchRequests(String.format("%.2f", analysisTopicDTO.getTotalFetchRequests()));
            analysisTopicVO.setTotalFetchRequestsRate(String.format("%.2f", analysisTopicDTO.getTotalFetchRequestsRate()));
            analysisTopicVO.setTotalProduceRequests(String.format("%.2f", analysisTopicDTO.getTotalProduceRequests()));
            analysisTopicVO.setTotalProduceRequestsRate(String.format("%.2f", analysisTopicDTO.getTotalProduceRequestsRate()));
            analysisBrokerVO.getTopicAnalysisVOList().add(analysisTopicVO);
        }
        return analysisBrokerVO;
    }

    public static List<RdBrokerBasicVO> convert2RdBrokerBasicVO(Long clusterId,
                                                                List<Integer> brokerIdList,
                                                                Map<Integer, RegionDO> regionMap) {
        List<RdBrokerBasicVO> basicList = new ArrayList<>();
        for (Integer brokerId : brokerIdList) {
            BrokerMetadata metadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (ValidateUtils.isNull(metadata)) {
                continue;
            }
            RdBrokerBasicVO basicInfoVO = new RdBrokerBasicVO();
            basicInfoVO.setBrokerId(brokerId);
            basicInfoVO.setHost(metadata.getHost());
            if (regionMap.containsKey(brokerId)) {
                basicInfoVO.setLogicClusterId(regionMap.get(brokerId).getId());
            }
            basicList.add(basicInfoVO);
        }
        return basicList;
    }
}
