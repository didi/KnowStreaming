package com.xiaojukeji.know.streaming.km.core.service.reassign;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.reassign.ExecuteReassignParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignPlan;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;

public interface ReassignService {
    /**
     * 生成迁移计划
     * @param clusterPhyId 物理集群ID
     * @param topicName Topic名称
     * @param partitionIdList 分区ID
     * @param brokerIdList BrokerID
     * @param enableRackAwareness 是否rack感知
     * @return
     */
    Result<ReassignPlan> generateReassignmentJson(Long clusterPhyId,
                                                  String topicName,
                                                  List<Integer> partitionIdList,
                                                  List<Integer> brokerIdList,
                                                  Boolean enableRackAwareness);

    /**
     * 生成副本扩缩计划
     * @param clusterPhyId 物理集群ID
     * @param topicName Topic名称
     * @param newReplicaNum 新的副本数
     * @param brokerIdList BrokerID
     * @return
     */
    Result<ReassignPlan> generateReplicaChangeReassignmentJson(Long clusterPhyId,
                                                               String topicName,
                                                               Integer newReplicaNum,
                                                               List<Integer> brokerIdList);

    /**
     * 执行迁移任务
     * @param executeReassignParam 参数
     * @return
     */
    Result<Void> executePartitionReassignments(ExecuteReassignParam executeReassignParam);

    /**
     * 检查迁移信息
     * @param executeReassignParam 参数
     * @return
     */
    Result<ReassignResult> verifyPartitionReassignments(ExecuteReassignParam executeReassignParam);

    Result<Void> changReassignmentThrottles(ExecuteReassignParam executeReassignParam);

    Result<Void> parseExecuteAssignmentArgs(Long clusterPhyId, String reassignmentJson);
}
