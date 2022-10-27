package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign;

import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
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

    public boolean checkPreferredReplicaElectionUnNeed(String reassignBrokerIds, String originalBrokerIds) {
        Integer targetLeader = CommonUtils.string2IntList(reassignBrokerIds).get(0);
        Integer originalLeader = CommonUtils.string2IntList(originalBrokerIds).get(0);
        return originalLeader.equals(targetLeader);
    }
}
