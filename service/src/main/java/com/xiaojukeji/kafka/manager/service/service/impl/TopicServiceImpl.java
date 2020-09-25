package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;
import com.xiaojukeji.kafka.manager.common.entity.dto.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicPartitionDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionMap;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.utils.DefaultThreadFactory;
import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientCache;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author limeng
 * @date 2018/5/4.
 */
@Service("topicService")
public class TopicServiceImpl implements TopicService {
    private final static Logger logger = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private TopicMetricsDao topicMetricsDao;

    @Autowired
    private ZookeeperService zookeeperService;

    @Autowired
    private JmxService jmxService;

    private static final int SPLIT_SIZE = 3;

    private final ExecutorService TOPIC_THREAD_POOL = Executors.newFixedThreadPool(4, new DefaultThreadFactory("TopicServiceImpl-getTopicInfo"));

    @Override
    public Long calTopicMaxAvgBytesIn(List<TopicMetrics> topicMetricsList, Integer maxAvgBytesInDuration) {
        if (topicMetricsList == null || maxAvgBytesInDuration == null) {
            throw new NullPointerException("calTopicMaxAvgBytesIn@TopicServiceImpl, param illegal.");
        }
        Double sumBytesIn = 0.0;
        Double tempSumBytesIn = 0.0;
        for (int i = 0; i < topicMetricsList.size(); ++i) {
            Double bytesIn = topicMetricsList.get(i).getBytesInPerSec();
            if (i < maxAvgBytesInDuration) {
                sumBytesIn += bytesIn;
                tempSumBytesIn += bytesIn;
            }
            if (i >= maxAvgBytesInDuration) {
                tempSumBytesIn = tempSumBytesIn + bytesIn - topicMetricsList.get(i - maxAvgBytesInDuration).getBytesInPerSec();
            }
            if (tempSumBytesIn > sumBytesIn) {
                sumBytesIn = tempSumBytesIn;
            }
        }
        if (topicMetricsList.size() > maxAvgBytesInDuration) {
            return Double.valueOf((sumBytesIn / maxAvgBytesInDuration)).longValue();
        } else if (topicMetricsList.size() == 0) {
            return null;
        }
        return Double.valueOf(sumBytesIn / topicMetricsList.size()).longValue();
    }

    @Override
    public List<TopicMetrics> getTopicMetricsByInterval(Long clusterId, String topic, Date startTime, Date endTime) {
        return topicMetricsDao.getTopicMetricsByInterval(clusterId, topic, startTime, endTime);
    }

    @Override
    public Map<String, List<Integer>> getTopicPartitionIdMap(Long clusterId, Integer brokerId) {
        Map<String, List<Integer>> result = new HashMap<>();
        for (String topicName: ClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
            if (topicMetadata == null) {
                continue;
            }

            Set<Integer> brokerIdSet = topicMetadata.getBrokerIdSet();
            if (brokerIdSet == null || !brokerIdSet.contains(brokerId)) {
                continue;
            }
            PartitionMap partitionMap = topicMetadata.getPartitionMap();
            result.put(topicName, new ArrayList<>(partitionMap.getPartitions().keySet()));
        }
        return result;
    }

    @Override
    public TopicBasicDTO getTopicBasicDTO(Long clusterId, String topicName) {
        TopicBasicDTO topicBasicDTO = new TopicBasicDTO();
        topicBasicDTO.setTopicName(topicName);

        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            return topicBasicDTO;
        }
        topicBasicDTO.setBrokerNum(topicMetadata.getBrokerIdSet().size());
        topicBasicDTO.setReplicaNum(topicMetadata.getReplicaNum());
        topicBasicDTO.setPartitionNum(topicMetadata.getPartitionNum());
        topicBasicDTO.setCreateTime(topicMetadata.getCreateTime());
        topicBasicDTO.setModifyTime(topicMetadata.getModifyTime());
        topicBasicDTO.setRetentionTime(zookeeperService.getRetentionTime(clusterId, topicName));
        return topicBasicDTO;
    }

    @Override
    public List<TopicPartitionDTO> getTopicPartitionDTO(ClusterDO clusterDO, String topicName, Boolean needOffsets) {
        if (clusterDO == null || StringUtils.isEmpty(topicName)) {
            return null;
        }
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterDO.getId(), topicName);
        if (topicMetadata == null) {
            return null;
        }

        List<PartitionState> partitionStateList = new ArrayList<>();
        try {
            partitionStateList = zookeeperService.getTopicPartitionState(clusterDO.getId(), topicName);
        } catch (Exception e) {
            logger.error("getTopicPartitionInfo@TopicServiceImpl, get partition state error.", e);
        }

        Map<TopicPartition, Long> offsetMap = needOffsets? getTopicPartitionOffset(clusterDO, topicName): new HashMap<>(0);

        List<TopicPartitionDTO> topicPartitionDTOList = new ArrayList<>();
        for (PartitionState partitionState: partitionStateList) {
            TopicPartitionDTO topicPartitionDTO = new TopicPartitionDTO();
            topicPartitionDTO.setPartitionId(partitionState.getPartitionId());
            topicPartitionDTO.setLeaderBrokerId(partitionState.getLeader());
            topicPartitionDTO.setLeaderEpoch(partitionState.getLeaderEpoch());
            topicPartitionDTO.setReplicasBroker(topicMetadata.getPartitionMap().getPartitions().get(partitionState.getPartitionId()));
            if (topicPartitionDTO.getReplicasBroker() != null && !topicPartitionDTO.getReplicasBroker().isEmpty()) {
                topicPartitionDTO.setPreferredBrokerId(topicPartitionDTO.getReplicasBroker().get(0));
            }
            topicPartitionDTO.setIsr(partitionState.getIsr());
            topicPartitionDTO.setOffset(offsetMap.get(new TopicPartition(topicName, partitionState.getPartitionId())));

            if (topicPartitionDTO.getIsr().size() < topicPartitionDTO.getReplicasBroker().size()) {
                topicPartitionDTO.setUnderReplicated(true);
            } else {
                topicPartitionDTO.setUnderReplicated(false);
            }
            topicPartitionDTOList.add(topicPartitionDTO);
        }
        return topicPartitionDTOList;
    }

    @Override
    public TopicMetrics getTopicMetrics(Long clusterId, String topicName, List<String> specifiedFieldList) {
        return jmxService.getSpecifiedTopicMetricsFromJmx(clusterId, topicName, specifiedFieldList, false);
    }

    @Override
    public Map<TopicPartition, Long> getTopicPartitionOffset(ClusterDO clusterDO, String topicName) {
        if (clusterDO == null || StringUtils.isEmpty(topicName)) {
            return new HashMap<>(0);
        }
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterDO.getId(), topicName);
        if (topicMetadata == null) {
            return new HashMap<>();
        }
        List<TopicPartition> topicPartitionList = new ArrayList<>();
        for (Integer partitionId = 0; partitionId < topicMetadata.getPartitionNum(); ++partitionId) {
            topicPartitionList.add(new TopicPartition(topicName, partitionId));
        }
        Map<TopicPartition, Long> topicPartitionLongMap = new HashMap<>();
        try {
            KafkaConsumer kafkaConsumer = KafkaClientCache.getApiKafkaConsumerClient(clusterDO);
            topicPartitionLongMap = kafkaConsumer.endOffsets(topicPartitionList);
        } catch (Exception e) {
            logger.error("getTopicOffset@TopicServiceImpl, get topic endOffsets failed, clusterId:{} topicName:{}.", clusterDO.getId(), topicPartitionList.get(0).topic(), e);
        }
        return topicPartitionLongMap;
    }

    @Override
    public List<TopicOverviewDTO> getTopicOverviewDTOList(final Long clusterId,
                                                          final Integer filterBrokerId,
                                                          final List<String> filterTopicNameList) {
        if (clusterId == null) {
            throw new IllegalArgumentException("getTopicInfo@TopicServiceImpl, param illegal");
        }

        // 过滤Topic
        List<String> topicNameList = new ArrayList<>();
        if (filterTopicNameList == null) {
            topicNameList = ClusterMetadataManager.getTopicNameList(clusterId);
        } else {
            for (String filterTopicName: filterTopicNameList) {
                if (ClusterMetadataManager.isTopicExist(clusterId, filterTopicName)) {
                    topicNameList.add(filterTopicName);
                }
            }
        }
        final Map<String, TopicMetrics> topicMetricsMap = getTopicMetricsFromCache(clusterId);

        int split = topicNameList.size() / SPLIT_SIZE;
        if (topicNameList.size() % SPLIT_SIZE != 0) {
            split ++;
        }
        FutureTask<List<TopicOverviewDTO>>[] taskList = new FutureTask[split];
        for (int i = 0; i < split; i++) {
            final List<String> subTopicNameList = topicNameList.subList(i * SPLIT_SIZE, (i + 1 == split) ? topicNameList.size() : (i + 1) * SPLIT_SIZE);
            taskList[i] = new FutureTask<List<TopicOverviewDTO>>(new Callable<List<TopicOverviewDTO>>() {
                @Override
                public List<TopicOverviewDTO> call() throws Exception {
                    List<TopicOverviewDTO> result = new ArrayList<>();
                    for (String topicName : subTopicNameList) {
                        TopicOverviewDTO topicOverviewDTO = getTopicOverviewDTO(clusterId, topicName, filterBrokerId, topicMetricsMap);
                        if (topicOverviewDTO == null) {
                            continue;
                        }
                        result.add(topicOverviewDTO);
                    }
                    return result;
                }
            });
            TOPIC_THREAD_POOL.submit(taskList[i]);
        }

        List<TopicOverviewDTO> result = new ArrayList<>();
        for (int i = 0; i < taskList.length; ++i) {
            try {
                result.addAll(taskList[i].get());
            } catch (Exception e) {
                List<String> subTopicNameList = topicNameList.subList(i * SPLIT_SIZE, (i + 1 == split) ? topicNameList.size() : (i + 1) * SPLIT_SIZE);
                logger.error("getTopicInfo@TopicServiceImpl, get topic simple info failed, clusterId:{} brokerId:{} subTopicNameList:{}.", clusterId, filterBrokerId, subTopicNameList.toString());
            }
        }
        return result;
    }

    @Override
    public Map<String, List<PartitionState>> getTopicPartitionState(Long clusterId, Integer filterBrokerId) {
        if (clusterId == null || filterBrokerId == null) {
            return new HashMap<>();
        }

        List<String> topicNameList = ClusterMetadataManager.getTopicNameList(clusterId);
        int split = topicNameList.size() / SPLIT_SIZE;
        if (topicNameList.size() % SPLIT_SIZE != 0) {
            split++;
        }

        FutureTask<Map<String, List<PartitionState>>>[] taskList = new FutureTask[split];
        for (int i = 0; i < split; i++) {
            final List<String> subTopicNameList = topicNameList.subList(i * SPLIT_SIZE, (i + 1 == split) ? topicNameList.size() : (i + 1) * SPLIT_SIZE);
            taskList[i] = new FutureTask<Map<String, List<PartitionState>>>(new Callable<Map<String, List<PartitionState>>>() {
                @Override
                public Map<String, List<PartitionState>> call() throws Exception {
                    Map<String, List<PartitionState>> subPartitionStateMap = new HashMap<>();
                    for (String topicName : subTopicNameList) {
                        List<PartitionState> partitionStateList = getTopicPartitionState(clusterId, topicName);
                        if (partitionStateList == null || partitionStateList.isEmpty()) {
                            continue;
                        }
                        subPartitionStateMap.put(topicName, partitionStateList);
                    }
                    return subPartitionStateMap;
                }
            });
            TOPIC_THREAD_POOL.submit(taskList[i]);
        }

        Map<String, List<PartitionState>> partitionStateMap = new HashMap<>();
        for (int i = 0; i < taskList.length; ++i) {
            try {
                partitionStateMap.putAll(taskList[i].get());
            } catch (Exception e) {
                List<String> subTopicNameList = topicNameList.subList(i * SPLIT_SIZE, (i + 1 == split) ? topicNameList.size() : (i + 1) * SPLIT_SIZE);
                logger.error("getBrokerTopicPartitionInfo@TopicServiceImpl, get topic partition state failed, clusterId:{} brokerId:{} subTopicNameList:{}.", clusterId, filterBrokerId, subTopicNameList.toString());
            }
        }
        return partitionStateMap;
    }


    private List<PartitionState> getTopicPartitionState(Long clusterId, String topicName) {
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            return null;
        }

        List<PartitionState> partitionStateList = null;
        try {
            partitionStateList = zookeeperService.getTopicPartitionState(clusterId, topicName);
            // 判断分区副本是否在isr
            for (PartitionState partitionState : partitionStateList) {
                if (topicMetadata.getReplicaNum() > partitionState.getIsr().size()) {
                    partitionState.setUnderReplicated(false);
                } else {
                    partitionState.setUnderReplicated(true);
                }
            }
        } catch (Exception e) {
            logger.error("getBrokerTopicPartitionInfo@TopicServiceImpl, get partition state from zk failed, clusterId:{} topicName:{}.", clusterId, topicName);
        }
        return partitionStateList;
    }


    private TopicOverviewDTO getTopicOverviewDTO(Long clusterId, String topicName, Integer filterBrokerId, Map<String, TopicMetrics> topicMetricsMap) {
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterId, topicName);
        if (topicMetadata == null) {
            return null;
        }
        if (filterBrokerId != -1 && !topicMetadata.getBrokerIdSet().contains(filterBrokerId)) {
            return null;
        }

        TopicOverviewDTO topicOverviewDTO = new TopicOverviewDTO();
        topicOverviewDTO.setClusterId(clusterId);
        topicOverviewDTO.setTopicName(topicName);
        topicOverviewDTO.setPartitionNum(topicMetadata.getPartitionNum());
        topicOverviewDTO.setReplicaNum(topicMetadata.getReplicaNum());
        topicOverviewDTO.setUpdateTime(topicMetadata.getModifyTime());

        TopicMetrics topicMetrics = topicMetricsMap.get(topicName);
        if (topicMetrics != null) {
            topicOverviewDTO.setBytesInPerSec(topicMetrics.getBytesInPerSec());
            topicOverviewDTO.setProduceRequestPerSec(topicMetrics.getTotalProduceRequestsPerSec());
        } else {
            topicMetrics = jmxService.getSpecifiedTopicMetricsFromJmx(clusterId, topicName, TopicMetrics.getFieldNameList(MetricsType.TOPIC_FLOW_DETAIL), true);
            topicOverviewDTO.setBytesInPerSec(topicMetrics.getBytesInPerSec());
            topicOverviewDTO.setProduceRequestPerSec(topicMetrics.getBytesOutPerSec());
        }
        return topicOverviewDTO;
    }


    /**
     * 从DataCollectorManager获取Topic的流量信息
     */
    private Map<String, TopicMetrics> getTopicMetricsFromCache(Long clusterId) {
        List<TopicMetrics> topicMetricsList = KafkaMetricsCache.getTopicMetricsFromCache(clusterId);
        if (topicMetricsList == null) {
            return new HashMap<>();
        }

        Map<String, TopicMetrics> result = new HashMap<>();
        for (TopicMetrics topicMetrics: topicMetricsList) {
            result.put(topicMetrics.getTopicName(), topicMetrics);
        }
        return result;
    }

    @Override
    public List<PartitionOffsetDTO> getPartitionOffsetList(ClusterDO clusterDO, String topicName, Long timestamp) {
        TopicMetadata topicMetadata = ClusterMetadataManager.getTopicMetaData(clusterDO.getId(), topicName);
        if (topicMetadata == null) {
            return null;
        }
        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
        for (Integer partitionId: topicMetadata.getPartitionMap().getPartitions().keySet()) {
            timestampsToSearch.put(new TopicPartition(topicName, partitionId), timestamp);
        }
        if (timestampsToSearch.isEmpty()) {
            return new ArrayList<>();
        }

        List<PartitionOffsetDTO> partitionOffsetDTOList = new ArrayList<>();
        try {
            KafkaConsumer kafkaConsumer = KafkaClientCache.getApiKafkaConsumerClient(clusterDO);
            Map<TopicPartition, OffsetAndTimestamp> offsetAndTimestampMap = kafkaConsumer.offsetsForTimes(timestampsToSearch);
            if (offsetAndTimestampMap == null) {
                return new ArrayList<>();
            }
            for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry: offsetAndTimestampMap.entrySet()) {
                TopicPartition tp = entry.getKey();
                OffsetAndTimestamp offsetAndTimestamp = entry.getValue();
                partitionOffsetDTOList.add(new PartitionOffsetDTO(tp.partition(), offsetAndTimestamp.offset(), offsetAndTimestamp.timestamp()));
            }
        } catch (Exception e) {
            logger.error("getTopicOffsetList@TopicServiceImpl, get offset failed, clusterId:{} topicName:{} timestamp:{}.", clusterDO.getId(), topicName, timestamp, e);
        }
        return partitionOffsetDTOList;
    }

    @Override
    public List<String> fetchTopicData(ClusterDO clusterDO, List<TopicPartition> topicPartitionList, int timeout, int maxMsgNum, long offset, boolean truncate) {
        KafkaConsumer kafkaConsumer = KafkaClientCache.getApiKafkaConsumerClient(clusterDO);
        if (kafkaConsumer == null) {
            return null;
        }
        if (offset == -1) {
            Map<TopicPartition, Long> topicPartitionLongMap = kafkaConsumer.endOffsets(topicPartitionList);
            Long tempOffset = topicPartitionLongMap.get(topicPartitionList.get(0));
            if (tempOffset == null) {
                return null;
            }
            offset = Math.max(tempOffset - maxMsgNum, 0);
        }
        return fetchTopicData(kafkaConsumer, topicPartitionList, offset, maxMsgNum, timeout, truncate);
    }

    private List<String> fetchTopicData(KafkaConsumer kafkaConsumer, List<TopicPartition> topicPartitionList, long startOffset, int maxMsgNum, int timeout, boolean truncate) {
        List<String> result = new ArrayList<>();
        long timestamp = System.currentTimeMillis();

        kafkaConsumer.assign(topicPartitionList);
        while (result.size() < maxMsgNum) {
            try {
                kafkaConsumer.seek(topicPartitionList.get(0), startOffset);
                ConsumerRecords<String, String> records = kafkaConsumer.poll(2000);
                for (ConsumerRecord record: records) {
                    startOffset = record.offset();

                    String dataValue = (String) record.value();
                    int maxLength = 2048;
                    if (dataValue.length() > maxLength && truncate) {
                        dataValue = dataValue.substring(0, maxLength);
                    }

                    result.add(dataValue);
                }
                Thread.sleep(10);
                if (System.currentTimeMillis() - timestamp > timeout) {
                    break;
                }
            } catch (Exception e) {
                logger.error("fetchTopicData@TopicServiceImpl, fetch failed, tp:{} offset:{}", topicPartitionList, startOffset, e);
                return null;
            }
        }
        return result;
    }
}
