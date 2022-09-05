package com.xiaojukeji.know.streaming.km.common.bean.entity.param.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

@Data
@NoArgsConstructor
public class PartitionOffsetParam extends TopicParam {
    private Map<TopicPartition, OffsetSpec> topicPartitionOffsets;

    private Long timestamp;

    public PartitionOffsetParam(Long clusterPhyId, String topicName, Map<TopicPartition, OffsetSpec> topicPartitionOffsets, Long timestamp) {
        super(clusterPhyId, topicName);
        this.topicPartitionOffsets = topicPartitionOffsets;
        this.timestamp = timestamp;
    }
}
