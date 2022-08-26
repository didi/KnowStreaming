package com.xiaojukeji.know.streaming.km.common.bean.entity.partition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.TopicPartitionInfo;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partition implements Serializable {
    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 分区ID
     */
    private Integer partitionId;

    /**
     * leaderBrokerId
     */
    private Integer leaderBrokerId;

    /**
     * isr
     */
    private List<Integer> inSyncReplicaList;

    /**
     * ar
     */
    private List<Integer> assignReplicaList;

    public static Partition buildFrom(Long clusterPhyId, String topicName, TopicPartitionInfo topicPartitionInfo) {
        Partition partition = new Partition();
        partition.setClusterPhyId(clusterPhyId);
        partition.setTopicName(topicName);
        partition.setPartitionId(topicPartitionInfo.partition());
        partition.setLeaderBrokerId(topicPartitionInfo.leader().id());
        partition.setInSyncReplicaList(topicPartitionInfo.isr().stream().map(elem -> elem.id()).collect(Collectors.toList()));
        partition.setAssignReplicaList(topicPartitionInfo.replicas().stream().map(elem -> elem.id()).collect(Collectors.toList()));
        return partition;
    }
}
