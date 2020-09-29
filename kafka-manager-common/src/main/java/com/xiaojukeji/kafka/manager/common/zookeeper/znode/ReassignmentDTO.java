package com.xiaojukeji.kafka.manager.common.zookeeper.znode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/1/15
 */
public class ReassignmentDTO {
    private Integer version;

    private List<Map<String, String>> topics;

    public ReassignmentDTO(Integer version, String topicName) {
        this.version = version;
        Map<String, String> topic = new HashMap<>();
        topic.put("topic", topicName);
        topics = new ArrayList<>();
        topics.add(topic);
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<Map<String, String>> getTopics() {
        return topics;
    }

    public void setTopics(List<Map<String, String>> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "ReassignmentDTO{" +
                "version=" + version +
                ", topics=" + topics +
                '}';
    }
}