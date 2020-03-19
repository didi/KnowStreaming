package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;

import java.util.List;

/**
 * 从Jmx获取相关数据的服务接口
 *
 * @author tukun, zengqiao
 * @date 2015/11/11.
 */
public interface JmxService {
    /**
     * 通过JMX获取指定字段的Broker的监控信息
     */
    BrokerMetrics getSpecifiedBrokerMetricsFromJmx(Long clusterId, Integer brokerId, List<String> specifiedFieldList, Boolean simple);

    TopicMetrics getSpecifiedTopicMetricsFromJmx(Long clusterId, String topicName, List<String> specifiedFieldList, Boolean simple);

    TopicMetrics getSpecifiedBrokerTopicMetricsFromJmx(Long clusterId, Integer brokerId, String topicName, List<String> specifiedFieldList, Boolean simple);
}
