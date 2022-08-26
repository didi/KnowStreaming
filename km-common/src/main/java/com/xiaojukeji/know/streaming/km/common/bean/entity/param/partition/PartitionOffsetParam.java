package com.xiaojukeji.know.streaming.km.common.bean.entity.param.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

@Data
@NoArgsConstructor
public class PartitionOffsetParam extends ClusterPhyParam {
    private Map<TopicPartition, OffsetSpec> topicPartitionOffsets;

    private Long timestamp;

    public PartitionOffsetParam(Long clusterPhyId, Map<TopicPartition, OffsetSpec> topicPartitionOffsets, Long timestamp) {
        super(clusterPhyId);
        this.topicPartitionOffsets = topicPartitionOffsets;
        this.timestamp = timestamp;
    }
}
