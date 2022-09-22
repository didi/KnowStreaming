package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.brokers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 根据/brokers/topics/topic的节点内容定义
 * @author tukun
 * @date 2015/11/10.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PartitionMap implements Serializable {

    /**
     * 版本号
     */
    private int                         version;

    /**
     * Map<PartitionId，副本所在的brokerId列表>
     */
    private Map<Integer, List<Integer>> partitions;

    public List<Integer> getPartitionAssignReplicas(Integer partitionId) {
        if (partitions == null) {
            return new ArrayList<>();
        }

        return partitions.getOrDefault(partitionId, new ArrayList<>());
    }
}
