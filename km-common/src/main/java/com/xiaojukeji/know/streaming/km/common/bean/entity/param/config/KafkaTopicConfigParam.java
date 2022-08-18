package com.xiaojukeji.know.streaming.km.common.bean.entity.param.config;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaTopicConfigParam extends TopicParam {
    private Map<String, String> changedProps;

    public KafkaTopicConfigParam(Long clusterPhyId, String topicName, Map<String, String> changedProps) {
        super(clusterPhyId, topicName);
        this.changedProps = changedProps;
    }
}
