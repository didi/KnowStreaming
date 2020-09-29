package com.xiaojukeji.kafka.manager.common.entity;

import kafka.admin.AdminClient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengqiao
 * @date 19/5/14
 */
public class ConsumerMetadata {
    private Set<String> consumerGroupSet = new HashSet<>();

    private Map<String, Set<String>> topicNameConsumerGroupMap = new HashMap<>();

    private Map<String, AdminClient.ConsumerGroupSummary> consumerGroupSummaryMap = new HashMap<>();

    private Map<String, List<String>> consumerGroupAppMap = new ConcurrentHashMap<>();


    public ConsumerMetadata(Set<String> consumerGroupSet,
                            Map<String, Set<String>> topicNameConsumerGroupMap,
                            Map<String, AdminClient.ConsumerGroupSummary> consumerGroupSummaryMap,
                            Map<String, List<String>> consumerGroupAppMap) {
        this.consumerGroupSet = consumerGroupSet;
        this.topicNameConsumerGroupMap = topicNameConsumerGroupMap;
        this.consumerGroupSummaryMap = consumerGroupSummaryMap;
        this.consumerGroupAppMap = consumerGroupAppMap;
    }

    public Set<String> getConsumerGroupSet() {
        return consumerGroupSet;
    }

    public Map<String, Set<String>> getTopicNameConsumerGroupMap() {
        return topicNameConsumerGroupMap;
    }

    public Map<String, AdminClient.ConsumerGroupSummary> getConsumerGroupSummaryMap() {
        return consumerGroupSummaryMap;
    }

    public Map<String, List<String>> getConsumerGroupAppMap() {
        return consumerGroupAppMap;
    }
}