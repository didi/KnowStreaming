package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign;

import lombok.Data;
import org.apache.kafka.common.TopicPartition;

import java.util.Map;

@Data
public class ReassignResult {
    private Map<TopicPartition, ReassignState> reassignStateMap;

    private boolean partsOngoing;

    public boolean checkPartitionFinished(String topicName, Integer partitionId) {
        ReassignState state = reassignStateMap.get(new TopicPartition(topicName, partitionId));
        if (state == null) {
            return true;
        }

        return state.isDone();
    }
}
