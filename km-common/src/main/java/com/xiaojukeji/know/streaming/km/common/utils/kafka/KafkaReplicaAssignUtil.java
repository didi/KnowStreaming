package com.xiaojukeji.know.streaming.km.common.utils.kafka;


import com.xiaojukeji.know.streaming.km.common.bean.entity.assign.TopicAssignedCount;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;

import java.util.*;

public class KafkaReplicaAssignUtil {
    /**
     * 新增分区的分配策略
     * @param brokerRackMap broker-rack信息
     * @param partitionMap 当前partition分布信息
     * @param incPartitionNum 新增分区数
     * @return
     */
    public static Map<Integer, List<Integer>> generateNewPartitionAssignment(Map<Integer, String> brokerRackMap,
                                                                             Map<Integer, List<Integer>> partitionMap,
                                                                             Integer incPartitionNum) {
        if (ValidateUtils.isEmptyMap(brokerRackMap) || ValidateUtils.isEmptyMap(partitionMap) || ValidateUtils.isNullOrLessThanZero(incPartitionNum)) {
            return null;
        }

        Integer replicaNum = new ArrayList<>(partitionMap.entrySet()).get(0).getValue().size();
        if (brokerRackMap.size() < replicaNum) {
            return null;
        }

        // 初始化当前副本分布的统计信息
        Map<Integer, TopicAssignedCount> assignedCountMap = new HashMap<>();
        brokerRackMap.forEach((brokerId, rack) -> assignedCountMap.put(brokerId, new TopicAssignedCount(brokerId, rack, replicaNum, true)));
        partitionMap.forEach((partitionId, replicaIdList) -> {
            for (int idx = 0; idx < replicaIdList.size(); ++idx) {
                if (!assignedCountMap.containsKey(replicaIdList.get(idx))) {
                    // 该Broker不参与新增副本的分配
                    assignedCountMap.put(replicaIdList.get(idx), new TopicAssignedCount(replicaIdList.get(idx), "", replicaNum, false));
                }

                assignedCountMap.get(replicaIdList.get(idx)).getAssignedPartitionSet().add(partitionId);
                assignedCountMap.get(replicaIdList.get(idx)).getIdxReplicaPartitionList().get(idx).add(partitionId);
            }
        });

        List<TopicAssignedCount> assignedCountList = new ArrayList<>(assignedCountMap.values());

        // 遍历分区的副本
        for (int replicaIdx = 0; replicaIdx < replicaNum; ++replicaIdx) {
            TopicAssignedCount.setPresentReplicaIdx(replicaIdx, replicaNum);

            // 遍历新增的分区
            for (int partitionId = partitionMap.size(); partitionId < incPartitionNum + partitionMap.size(); ++partitionId) {
                Collections.sort(assignedCountList);

                for (TopicAssignedCount assignedCount: assignedCountList) {
                    if (!assignedCount.isIncludeAssign() || assignedCount.getAssignedPartitionSet().contains(partitionId)) {
                        // 当前Broker不参与新增副本的分配，或者分区已经落在该Broker上
                        continue;
                    }

                    // 将分区分配到该Broker上，可以增加判断是否满足跨Rack分配的要求
                    assignedCount.getAssignedPartitionSet().add(partitionId);
                    assignedCount.getIdxReplicaPartitionList().get(replicaIdx).add(partitionId);
                    break;
                }
            }
        }

        return TopicAssignedCount.convert2PartitionMap(assignedCountList, replicaNum, new ArrayList<>(partitionMap.keySet()));
    }



    private KafkaReplicaAssignUtil() {
    }

    public static void main(String []args) {
        Map<Integer, String> brokerRackMap = new HashMap<>();
        brokerRackMap.put(1, "");
        brokerRackMap.put(2, "");
        brokerRackMap.put(3, "");
        brokerRackMap.put(4, "");
        brokerRackMap.put(5, "");

        Map<Integer, List<Integer>> oldPartitionMap = new HashMap<>();
        oldPartitionMap.put(0, Arrays.asList(1, 2, 3));
        oldPartitionMap.put(1, Arrays.asList(2, 3, 1));
        oldPartitionMap.put(2, Arrays.asList(3, 1, 2));

        Map<Integer, List<Integer>> newPartitionMap = generateNewPartitionAssignment(brokerRackMap, oldPartitionMap, 10);
        for (Map.Entry<Integer, List<Integer>> entry: newPartitionMap.entrySet()) {
            System.out.println(String.format("partitionId:%d assignReplica:%s", entry.getKey(), ConvertUtil.obj2Json(entry.getValue())));
        }
    }
}
