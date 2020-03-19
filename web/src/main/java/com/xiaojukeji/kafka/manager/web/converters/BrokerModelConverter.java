package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.common.entity.dto.analysis.AnalysisBrokerDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.analysis.AnalysisTopicDTO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerOverallDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.BrokerOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.RegionDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import com.xiaojukeji.kafka.manager.service.utils.ObjectUtil;
import com.xiaojukeji.kafka.manager.web.vo.broker.*;

import java.util.*;

/**
 * @author zengqiao
 * @date 19/4/21
 */
public class BrokerModelConverter {
    private static Map<Integer, String> convert2BrokerIdRegionNameMap(List<RegionDO> regionDOList) {
        Map<Integer, String> brokerIdRegionNameMap = new HashMap<>();
        if (regionDOList == null) {
            regionDOList = new ArrayList<>();
        }
        for (RegionDO regionDO: regionDOList) {
            List<Integer> brokerIdList = ListUtils.string2IntList(regionDO.getBrokerList());
            if (brokerIdList == null || brokerIdList.isEmpty()) {
                continue;
            }
            for (Integer brokerId: brokerIdList) {
                brokerIdRegionNameMap.put(brokerId, regionDO.getRegionName());
            }
        }
        return brokerIdRegionNameMap;
    }

    public static List<BrokerOverviewVO> convert2BrokerOverviewList(List<BrokerOverviewDTO> brokerOverviewDTOList,
                                                                    List<RegionDO> regionDOList) {
        if (brokerOverviewDTOList == null) {
            return new ArrayList<>();
        }
        Map<Integer, String> brokerIdRegionNameMap = convert2BrokerIdRegionNameMap(regionDOList);

        List<BrokerOverviewVO> brokerOverviewVOList = new ArrayList<>();
        for (BrokerOverviewDTO brokerOverviewDTO: brokerOverviewDTOList) {
            BrokerOverviewVO brokerOverviewVO = new BrokerOverviewVO();
            CopyUtils.copyProperties(brokerOverviewVO, brokerOverviewDTO);
            brokerOverviewVO.setRegionName(brokerIdRegionNameMap.getOrDefault(brokerOverviewDTO.getBrokerId(), ""));
            brokerOverviewVOList.add(brokerOverviewVO);
        }
        Collections.sort(brokerOverviewVOList);
        return brokerOverviewVOList;
    }

    public static List<BrokerOverallVO> convert2BrokerOverallVOList(Long clusterId,
                                                                    List<BrokerOverallDTO> brokerOverallDTOList,
                                                                    List<RegionDO> regionDOList) {
        if (brokerOverallDTOList == null) {
            return new ArrayList<>();
        }
        Map<Integer, String> brokerIdRegionNameMap = convert2BrokerIdRegionNameMap(regionDOList);

        List<BrokerOverallVO> brokerOverallVOList = new ArrayList<>();
        for (BrokerOverallDTO brokerOverallDTO: brokerOverallDTOList) {
            BrokerMetadata brokerMetadata = ClusterMetadataManager.getBrokerMetadata(clusterId, brokerOverallDTO.getBrokerId());

            BrokerOverallVO brokerOverviewVO = new BrokerOverallVO();
            brokerOverviewVO.setBrokerId(brokerOverallDTO.getBrokerId());
            brokerOverviewVO.setHost(brokerMetadata.getHost());
            brokerOverviewVO.setPort(brokerMetadata.getPort());
            brokerOverviewVO.setJmxPort(brokerMetadata.getJmxPort());
            if (brokerOverallDTO.getBytesInPerSec() != null) {
                Double bytesInPerSec = brokerOverallDTO.getBytesInPerSec() / 1024.0 / 1024.0;
                brokerOverviewVO.setBytesInPerSec(Math.round(bytesInPerSec * 100) / 100.0);
            }
            brokerOverviewVO.setLeaderCount(brokerOverallDTO.getLeaderCount());
            if (brokerOverallDTO.getPartitionCount() != null && brokerOverallDTO.getUnderReplicatedPartitions() != null) {
                brokerOverviewVO.setNotUnderReplicatedPartitionCount(brokerOverallDTO.getPartitionCount() - brokerOverallDTO.getUnderReplicatedPartitions());
            }
            brokerOverviewVO.setPartitionCount(brokerOverallDTO.getPartitionCount());
            brokerOverviewVO.setStartTime(brokerMetadata.getTimestamp());
            brokerOverviewVO.setRegionName(brokerIdRegionNameMap.getOrDefault(brokerOverallDTO.getBrokerId(), ""));
            brokerOverallVOList.add(brokerOverviewVO);
        }
        return brokerOverallVOList;
    }

    public static List<BrokerMetricsVO> convert2BrokerMetricsVOList(List<BrokerMetrics> brokerMetricsList) {
        if (brokerMetricsList == null) {
            return new ArrayList<>();
        }
        List<BrokerMetricsVO> brokerMetricsVOList = new ArrayList<>(brokerMetricsList.size());
        for (BrokerMetrics brokerMetrics: brokerMetricsList) {
            BrokerMetricsVO brokerMetricsVO = new BrokerMetricsVO();
            brokerMetricsVO.setBytesInPerSec(brokerMetrics.getBytesInPerSec());
            brokerMetricsVO.setBytesOutPerSec(brokerMetrics.getBytesOutPerSec());
            brokerMetricsVO.setMessagesInPerSec(brokerMetrics.getMessagesInPerSec());
            brokerMetricsVO.setBytesRejectedPerSec(brokerMetrics.getBytesRejectedPerSec());
            brokerMetricsVO.setGmtCreate(brokerMetrics.getGmtCreate().getTime());
            brokerMetricsVOList.add(brokerMetricsVO);
        }
        return brokerMetricsVOList;
    }

    public static BrokerStatusVO convertBroker2BrokerMetricsVO(List<BrokerMetrics> brokerMetricsList) {
        if (brokerMetricsList == null) {
            return null;
        }

        BrokerMetrics sumBrokerMetrics = new BrokerMetrics();
        for (BrokerMetrics brokerMetrics : brokerMetricsList) {
            ObjectUtil.add(sumBrokerMetrics, brokerMetrics, "PerSec");
        }

        BrokerStatusVO brokerMetricsVO = new BrokerStatusVO();
        List<Double> byteIn = new ArrayList<>(4);
        List<Double> byteOut = new ArrayList<>(4);
        List<Double> messageIn = new ArrayList<>(4);
        List<Double> byteRejected = new ArrayList<>(4);
        List<Double> failedFetchRequest = new ArrayList<>(4);
        List<Double> failedProduceRequest = new ArrayList<>(4);
        List<Double> fetchConsumerRequest = new ArrayList<>(4);
        List<Double> produceRequest = new ArrayList<>(4);

        byteIn.add(sumBrokerMetrics.getBytesInPerSecMeanRate());
        byteIn.add(sumBrokerMetrics.getBytesInPerSec());
        byteIn.add(sumBrokerMetrics.getBytesInPerSecFiveMinuteRate());
        byteIn.add(sumBrokerMetrics.getBytesInPerSecFifteenMinuteRate());

        byteOut.add(sumBrokerMetrics.getBytesOutPerSecMeanRate());
        byteOut.add(sumBrokerMetrics.getBytesOutPerSec());
        byteOut.add(sumBrokerMetrics.getBytesOutPerSecFiveMinuteRate());
        byteOut.add(sumBrokerMetrics.getBytesOutPerSecFifteenMinuteRate());

        messageIn.add(sumBrokerMetrics.getMessagesInPerSecMeanRate());
        messageIn.add(sumBrokerMetrics.getMessagesInPerSec());
        messageIn.add(sumBrokerMetrics.getMessagesInPerSecFiveMinuteRate());
        messageIn.add(sumBrokerMetrics.getMessagesInPerSecFifteenMinuteRate());

        byteRejected.add(sumBrokerMetrics.getBytesRejectedPerSecMeanRate());
        byteRejected.add(sumBrokerMetrics.getBytesRejectedPerSec());
        byteRejected.add(sumBrokerMetrics.getBytesRejectedPerSecFiveMinuteRate());
        byteRejected.add(sumBrokerMetrics.getBytesRejectedPerSecFifteenMinuteRate());

        failedProduceRequest.add(sumBrokerMetrics.getFailProduceRequestPerSecMeanRate());
        failedProduceRequest.add(sumBrokerMetrics.getFailProduceRequestPerSec());
        failedProduceRequest.add(sumBrokerMetrics.getFailProduceRequestPerSecFiveMinuteRate());
        failedProduceRequest.add(sumBrokerMetrics.getFailProduceRequestPerSecFifteenMinuteRate());

        fetchConsumerRequest.add(sumBrokerMetrics.getFetchConsumerRequestPerSecMeanRate());
        fetchConsumerRequest.add(sumBrokerMetrics.getFetchConsumerRequestPerSec());
        fetchConsumerRequest.add(sumBrokerMetrics.getFetchConsumerRequestPerSecFiveMinuteRate());
        fetchConsumerRequest.add(sumBrokerMetrics.getFetchConsumerRequestPerSecFifteenMinuteRate());

        failedFetchRequest.add(sumBrokerMetrics.getFailFetchRequestPerSecMeanRate());
        failedFetchRequest.add(sumBrokerMetrics.getFailFetchRequestPerSec());
        failedFetchRequest.add(sumBrokerMetrics.getFailFetchRequestPerSecFiveMinuteRate());
        failedFetchRequest.add(sumBrokerMetrics.getFailFetchRequestPerSecFifteenMinuteRate());

        produceRequest.add(sumBrokerMetrics.getProduceRequestPerSecMeanRate());
        produceRequest.add(sumBrokerMetrics.getProduceRequestPerSec());
        produceRequest.add(sumBrokerMetrics.getProduceRequestPerSecFiveMinuteRate());
        produceRequest.add(sumBrokerMetrics.getProduceRequestPerSecFifteenMinuteRate());


        brokerMetricsVO.setByteIn(byteIn);
        brokerMetricsVO.setByteOut(byteOut);
        brokerMetricsVO.setMessageIn(messageIn);
        brokerMetricsVO.setByteRejected(byteRejected);
        brokerMetricsVO.setFailedFetchRequest(failedFetchRequest);
        brokerMetricsVO.setFailedProduceRequest(failedProduceRequest);
        brokerMetricsVO.setProduceRequest(produceRequest);
        brokerMetricsVO.setFetchConsumerRequest(fetchConsumerRequest);

        return brokerMetricsVO;
    }

    public static List<BrokerPartitionsVO> convert2BrokerPartitionsVOList(Long clusterId,
                                                                          Integer brokerId,
                                                                          Map<String, List<PartitionState>> partitionStateMap){
        List<BrokerPartitionsVO> brokerPartitionsVOList = new ArrayList<>();
        for (String topicName: partitionStateMap.keySet()) {
            BrokerPartitionsVO brokerPartitionsVO = convert2BrokerPartitionsVO(clusterId, brokerId, topicName, partitionStateMap.get(topicName));
            if (brokerPartitionsVO == null) {
                continue;
            }
            brokerPartitionsVOList.add(brokerPartitionsVO);
        }
        return brokerPartitionsVOList;
    }

    private static BrokerPartitionsVO convert2BrokerPartitionsVO(Long clusterId,
                                                                 Integer brokerId,
                                                                 String topicName,
                                                                 List<PartitionState> partitionStateList){
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (null == partitionStateList || topicMetadata == null) {
            return null;
        }

        Set<Integer> leaderPartitionIdSet = new HashSet<>();
        Set<Integer> followerPartitionIdSet = new HashSet<>();
        Set<Integer> notUnderReplicatedPartitionIdSet = new HashSet<>();
        for (PartitionState partitionState : partitionStateList) {
            List<Integer> replicaIdList = topicMetadata.getPartitionMap().getPartitions().get(partitionState.getPartitionId());
            if (partitionState.getLeader() == brokerId) {
                leaderPartitionIdSet.add(partitionState.getPartitionId());
            } else if (replicaIdList.contains(brokerId)) {
                followerPartitionIdSet.add(partitionState.getPartitionId());
            }

            if (replicaIdList.contains(brokerId) && partitionState.getIsr().size() < replicaIdList.size()) {
                notUnderReplicatedPartitionIdSet.add(partitionState.getPartitionId());
            }
        }
        BrokerPartitionsVO brokerPartitionsVO = new BrokerPartitionsVO();
        brokerPartitionsVO.setTopicName(topicName);
        brokerPartitionsVO.setLeaderPartitionList(new ArrayList<>(leaderPartitionIdSet));
        brokerPartitionsVO.setFollowerPartitionIdList(new ArrayList<>(followerPartitionIdSet));
        brokerPartitionsVO.setNotUnderReplicatedPartitionIdList(new ArrayList<>(notUnderReplicatedPartitionIdSet));
        brokerPartitionsVO.setUnderReplicated(notUnderReplicatedPartitionIdSet.isEmpty());
        return brokerPartitionsVO;
    }

    public static List<BrokerKeyMetricsVO> convert2BrokerKeyMetricsVOList(List<BrokerMetrics> brokerMetricsList) {
        if (brokerMetricsList == null) {
            return new ArrayList<>();
        }
        List<BrokerKeyMetricsVO> brokerKeyMetricsVOList = new ArrayList<>();
        for (BrokerMetrics brokerMetrics : brokerMetricsList) {
            brokerKeyMetricsVOList.add(BrokerModelConverter.convert2BrokerHealthVO(brokerMetrics));
        }
        return brokerKeyMetricsVOList;
    }

    private static BrokerKeyMetricsVO convert2BrokerHealthVO(BrokerMetrics brokerMetrics) {
        if (null == brokerMetrics) {
            return null;
        }
        BrokerKeyMetricsVO brokerKeyMetricsVO = new BrokerKeyMetricsVO();
        brokerKeyMetricsVO.setId(brokerMetrics.getId());
        brokerKeyMetricsVO.setFailFetchRequest(brokerMetrics.getFailFetchRequestPerSec());
        brokerKeyMetricsVO.setFailProduceRequest(brokerMetrics.getFailProduceRequestPerSec());
        brokerKeyMetricsVO.setLogFlushTime(brokerMetrics.getLogFlushRateAndTimeMs());
        brokerKeyMetricsVO.setNetworkProcessorIdlPercent(brokerMetrics.getNetworkProcessorAvgIdlePercent());
        brokerKeyMetricsVO.setRequestHandlerIdlPercent(brokerMetrics.getRequestHandlerAvgIdlePercent());
        brokerKeyMetricsVO.setRequestQueueSize(brokerMetrics.getRequestQueueSize());
        brokerKeyMetricsVO.setResponseQueueSize(brokerMetrics.getResponseQueueSize());
        brokerKeyMetricsVO.setTotalTimeProduceMean(brokerMetrics.getTotalTimeProduceMean());
        brokerKeyMetricsVO.setTotalTimeProduce99Th(brokerMetrics.getTotalTimeProduce99Th());
        brokerKeyMetricsVO.setTotalTimeFetchConsumerMean(brokerMetrics.getTotalTimeFetchConsumerMean());
        brokerKeyMetricsVO.setTotalTimeFetchConsumer99Th(brokerMetrics.getTotalTimeFetchConsumer99Th());
        brokerKeyMetricsVO.setGmtCreate(brokerMetrics.getGmtCreate().getTime());
        return brokerKeyMetricsVO;
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
        for (AnalysisTopicDTO analysisTopicDTO: analysisBrokerDTO.getTopicAnalysisVOList()) {
            AnalysisTopicVO analysisTopicVO = new AnalysisTopicVO();
            analysisTopicVO.setTopicName(analysisTopicDTO.getTopicName());
            analysisTopicVO.setBytesIn(String.format("%.2f", analysisTopicDTO.getBytesIn() / 1024.0 / 1024.0));
            analysisTopicVO.setBytesInRate(String.format("%.2f", analysisTopicDTO.getBytesInRate()));
            analysisTopicVO.setBytesOut(String.format("%.2f", analysisTopicDTO.getBytesOut() / 1024.0 / 1024.0));
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
}
