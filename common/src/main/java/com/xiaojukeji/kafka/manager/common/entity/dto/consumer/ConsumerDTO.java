package com.xiaojukeji.kafka.manager.common.entity.dto.consumer;

import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;

import java.util.List;
import java.util.Map;

/**
 * Consumer实体类
 * @author tukun
 * @date 2015/11/12
 */
public class ConsumerDTO {
    /**
     * 消费group名
     */
    private String consumerGroup;

    /**
     * 消费类型，一般为static
     */
    private String location;

    /**
     * 订阅的每个topic的partition状态列表
     */
    private Map<String, List<PartitionState>> topicPartitionMap;

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Map<String, List<PartitionState>> getTopicPartitionMap() {
        return topicPartitionMap;
    }

    public void setTopicPartitionMap(Map<String, List<PartitionState>> topicPartitionMap) {
        this.topicPartitionMap = topicPartitionMap;
    }

    @Override
    public String toString() {
        return "Consumer{" +
                "consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", topicPartitionMap=" + topicPartitionMap +
                '}';
    }
}
