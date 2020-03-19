package com.xiaojukeji.kafka.manager.service.collector;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * TopicMetrics信息收集
 * @author zengqiao
 * @date 19/4/17
 */
public class CollectTopicMetricsTask extends BaseCollectTask {
    private final static Logger logger = LoggerFactory.getLogger(Constant.COLLECTOR_METRICS_LOGGER);

    private JmxService jmxService;

    public CollectTopicMetricsTask(Long clusterId, JmxService jmxService) {
        super(logger, clusterId);
        this.clusterId = clusterId;
        this.jmxService = jmxService;
    }

    @Override
    public void collect() {
        List<String> specifiedFieldList = TopicMetrics.getFieldNameList(MetricsType.TOPIC_METRICS_TO_DB);
        List<String> topicNameList = ClusterMetadataManager.getTopicNameList(clusterId);
        List<TopicMetrics> topicMetricsList = new ArrayList<>();
        for (String topicName: topicNameList) {
            TopicMetrics topicMetrics = jmxService.getSpecifiedTopicMetricsFromJmx(clusterId, topicName, specifiedFieldList, true);
            if (topicMetrics == null) {
                continue;
            }
            topicMetricsList.add(topicMetrics);
        }
        KafkaMetricsCache.putTopicMetricsToCache(clusterId, topicMetricsList);
    }
}
