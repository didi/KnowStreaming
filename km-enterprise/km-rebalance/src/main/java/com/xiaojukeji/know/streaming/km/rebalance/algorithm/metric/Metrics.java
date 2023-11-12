package com.xiaojukeji.know.streaming.km.rebalance.algorithm.metric;

import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Load;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * @author leewei
 * @date 2022/4/29
 */
public class Metrics {
    private final Map<TopicPartition, Metric> metricByTopicPartition;

    public Metrics() {
        this.metricByTopicPartition = new HashMap<>();
    }

    public void addMetrics(Metric metric) {
        TopicPartition topicPartition = new TopicPartition(metric.topic(), metric.partition());
        this.metricByTopicPartition.put(topicPartition, metric);
    }

    public List<Metric> values() {
        return Collections.unmodifiableList(new ArrayList<>(this.metricByTopicPartition.values()));
    }

    public Metric metric(TopicPartition topicPartition) {
        return this.metricByTopicPartition.get(topicPartition);
    }

    public Load load(TopicPartition topicPartition) {
        Metric metric = this.metricByTopicPartition.get(topicPartition);
        if (metric == null) {
            return null;
        }
        Load load = new Load();
        load.setLoad(Resource.CPU, metric.cpu());
        load.setLoad(Resource.NW_IN, metric.bytesIn());
        load.setLoad(Resource.NW_OUT, metric.bytesOut());
        load.setLoad(Resource.DISK, metric.disk());

        return load;
    }
}
