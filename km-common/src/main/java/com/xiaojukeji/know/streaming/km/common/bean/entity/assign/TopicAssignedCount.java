package com.xiaojukeji.know.streaming.km.common.bean.entity.assign;

import lombok.Data;

import java.util.*;

/**
 * Topic在Broker上的分布统计
 */
@Data
public class TopicAssignedCount implements Comparable<TopicAssignedCount> {
    private static Integer presentReplicaIdx = 0;

    private Integer brokerId;

    private String rack;

    /**
     * 每个位置的副本，都分配了哪些分区到该Broker上
     */
    private List<List<Integer>> idxReplicaPartitionList;

    /**
     * 分配到该Broker上的分区
     */
    private Set<Integer> assignedPartitionSet;

    /**
     * 是否参与分区分配
     */
    private boolean includeAssign;

    public TopicAssignedCount(Integer brokerId, String rack, Integer replicaNum, boolean includeAssign) {
        this.brokerId = brokerId;
        this.rack = rack;
        this.idxReplicaPartitionList = new ArrayList<>();
        for (int idx = 0; idx < replicaNum; ++idx) {
            this.idxReplicaPartitionList.add(new ArrayList<>());
        }
        this.assignedPartitionSet = new HashSet<>();
        this.includeAssign = includeAssign;
    }

    @Override
    public int compareTo(TopicAssignedCount o) {
        // 先按照该Broker在某个位置的副本数量进行排序
        if (this.idxReplicaPartitionList.get(presentReplicaIdx).size() < o.idxReplicaPartitionList.get(o.presentReplicaIdx).size()) {
            return -1;
        } else if (this.idxReplicaPartitionList.get(presentReplicaIdx).size() > o.idxReplicaPartitionList.get(o.presentReplicaIdx).size()) {
            return 1;
        }

        // 再按照Broker上的副本总数进行排序
        if (this.assignedPartitionSet.size() < o.assignedPartitionSet.size()) {
            return -1;
        } else if (this.assignedPartitionSet.size() > o.assignedPartitionSet.size()) {
            return 1;
        }

        // 最后按照brokerId进行排序
        return this.brokerId.compareTo(o.brokerId);
    }

    public static void setPresentReplicaIdx(Integer replicaIdx, Integer replicaNum) {
        presentReplicaIdx = replicaIdx % replicaNum;
    }

    public static Map<Integer, List<Integer>> convert2PartitionMap(List<TopicAssignedCount> assignedCountList, Integer replicaNum, List<Integer> filterPartitionIdList) {
        Map<Integer, List<Integer>> partitionMap = new HashMap<>();

        // 遍历每个位置的副本
        for (int idx = 0; idx < replicaNum; ++idx) {
            // 遍历Broker
            for (TopicAssignedCount assignedCount: assignedCountList) {
                // 遍历Broker上指定位置的副本被分配到的分区
                for (Integer partitionId: assignedCount.getIdxReplicaPartitionList().get(idx)) {
                    if (filterPartitionIdList != null && filterPartitionIdList.contains(partitionId)) {
                        continue;
                    }
                    partitionMap.putIfAbsent(partitionId, new ArrayList<>());
                    partitionMap.get(partitionId).add(assignedCount.getBrokerId());
                }
            }
        }

        return partitionMap;
    }
}
