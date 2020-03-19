package com.xiaojukeji.kafka.manager.service.collector;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumerDTO;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 消费数据收集
 * @author zengqiao
 * @date 19/4/17
 */
public class CollectConsumerMetricsTask extends BaseCollectTask {
    private final static Logger logger = LoggerFactory.getLogger(Constant.COLLECTOR_METRICS_LOGGER);

    private ConsumerService consumerService;

    public CollectConsumerMetricsTask(Long clusterId, ConsumerService consumerService) {
        super(logger, clusterId);
        this.consumerService = consumerService;
    }

    @Override
    public void collect() {
        ClusterDO clusterDO = ClusterMetadataManager.getClusterFromCache(clusterId);
        if (clusterDO == null) {
            return;
        }
        Map<String, List<PartitionState>> topicNamePartitionStateListMap = new HashMap<>();
        List<ConsumerDTO> consumerDTOList = consumerService.getMonitoredConsumerList(clusterDO, topicNamePartitionStateListMap);

        List<ConsumerMetrics> consumerMetricsList = convert2ConsumerMetrics(consumerDTOList);
        KafkaMetricsCache.putConsumerMetricsToCache(clusterId, consumerMetricsList);
    }

    /**
     * 转换为ConsumerMetrics结构
     */
    private List<ConsumerMetrics> convert2ConsumerMetrics(List<ConsumerDTO> consumerDTOList) {
        List<ConsumerMetrics> consumerMetricsList = new ArrayList<>();
        for (ConsumerDTO consumerDTO : consumerDTOList) {
            Map<String, List<PartitionState>> topicNamePartitionStateListMap = consumerDTO.getTopicPartitionMap();
            for(Map.Entry<String, List<PartitionState>> entry : topicNamePartitionStateListMap.entrySet()){
                String topicName = entry.getKey();
                List<PartitionState> partitionStateList = entry.getValue();
                ConsumerMetrics consumerMetrics = new ConsumerMetrics();
                consumerMetrics.setClusterId(clusterId);
                consumerMetrics.setConsumerGroup(consumerDTO.getConsumerGroup());
                consumerMetrics.setLocation(consumerDTO.getLocation());
                consumerMetrics.setTopicName(topicName);
                long sumLag = 0;
                for (PartitionState partitionState : partitionStateList) {
                    Map.Entry<Long, Long> offsetEntry = new AbstractMap.SimpleEntry<>(partitionState.getOffset(), partitionState.getConsumeOffset());
                    sumLag += (offsetEntry.getKey() - offsetEntry.getValue() > 0 ? offsetEntry.getKey() - offsetEntry.getValue(): 0);
                }
                consumerMetrics.setSumLag(sumLag);
                consumerMetricsList.add(consumerMetrics);
            }
        }
        return consumerMetricsList;
    }
}
