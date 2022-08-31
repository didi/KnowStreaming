package com.xiaojukeji.know.streaming.km.core.service.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import org.apache.kafka.common.TopicPartition;

import java.util.List;

public interface OpPartitionService {

    /**
     * 优先副本选举
     */
    Result<Void> preferredReplicaElection(Long clusterPhyId, List<TopicPartition> tpList);
}
