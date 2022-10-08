package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.brokers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * PartitionState信息，对应ZK：/brokers/topics/__consumer_offsets/partitions/0/state 节点
 * 该节点的数据结构：
 * "{\"controller_epoch\":7,\"leader\":2,\"version\":1,\"leader_epoch\":7,\"isr\":[2,0]}"
 * @author tukun
 * @date 2015/11/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PartitionState implements Serializable {
    /**
     * partition id
     */
    private Integer partitionId;

    /**
     * kafka集群中的中央控制器选举次数
    */
    @JsonProperty("controller_epoch")
    private Integer controllerEpoch;

    /**
     * Partition所属的leader broker编号
     */
    private Integer leader;

    /**
     * partition的版本号
     */
    private Integer version;

    /**
     * 该partition leader选举次数
     */
    @JsonProperty("leader_epoch")
    private Integer leaderEpoch;

    /**
     * 同步副本组brokerId列表
     */
    private List<Integer> isr;
}
