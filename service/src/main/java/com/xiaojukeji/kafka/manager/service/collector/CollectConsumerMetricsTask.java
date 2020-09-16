package com.xiaojukeji.kafka.manager.service.collector;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.dto.consumer.ConsumerDTO;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.service.ConsumerService;
import org.apache.kafka.common.TopicPartition;
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
        Map<TopicPartition, Long> allPartitionOffsetMap = new HashMap<>();
        List<ConsumerDTO> consumerDTOList = consumerService.getMonitoredConsumerList(clusterDO, allPartitionOffsetMap);

        List<ConsumerMetrics> consumerMetricsList = convert2ConsumerMetrics(consumerDTOList);
        KafkaMetricsCache.putConsumerMetricsToCache(clusterId, consumerMetricsList);
    }

    /**
     * 转换为ConsumerMetrics结构
     */
    private List<ConsumerMetrics> convert2ConsumerMetrics(List<ConsumerDTO> consumerDTOList) {
        List<ConsumerMetrics> consumerMetricsList = new ArrayList<>();
        for (ConsumerDTO consumerDTO : consumerDTOList) {
            if (consumerDTO.getPartitionOffsetMap() == null || consumerDTO.getConsumerOffsetMap() == null) {
                continue;
            }

            ConsumerMetrics consumerMetrics = new ConsumerMetrics();
            consumerMetrics.setClusterId(consumerDTO.getClusterId());
            consumerMetrics.setConsumerGroup(consumerDTO.getConsumerGroup());
            consumerMetrics.setLocation(consumerDTO.getLocation());
            consumerMetrics.setTopicName(consumerDTO.getTopicName());

            long sumLag = 0;
            for(Map.Entry<Integer, Long> entry : consumerDTO.getPartitionOffsetMap().entrySet()){
                Long partitionOffset = entry.getValue();
                Long consumerOffset = consumerDTO.getConsumerOffsetMap().get(entry.getKey());
                if (partitionOffset == null || consumerOffset == null) {
                    continue;
                }
                sumLag += Math.max(partitionOffset - consumerOffset, 0);
            }
            consumerMetrics.setSumLag(sumLag);
            consumerMetricsList.add(consumerMetrics);
        }
        return consumerMetricsList;
    }
}
