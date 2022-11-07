package com.xiaojukeji.know.streaming.km.core.service.reassign.impl;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReassignExecutionStrategy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReassignTask;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReplaceReassignSub;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignStrategyService;
import com.xiaojukeji.know.streaming.km.core.service.replica.ReplicaMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ReplicaMetricVersionItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReassignStrategyServiceImpl implements ReassignStrategyService {

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ReplicaMetricService replicationMetricService;

    @Override
    public Result<List<ReassignTask>> generateReassignmentTask(ReassignExecutionStrategy executionStrategy) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(executionStrategy.getClusterPhyId());
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        if(executionStrategy.getReplaceReassignSubs().isEmpty()){
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        //根据副本大小进行排序
        List<ReplaceReassignSub> sortSubs = sortByReplaceLogSize(executionStrategy);

        //根据策略生成新的迁移任务
        List<ReassignTask> reassignTasks = generateReassignmentTask(
                executionStrategy.getParallelNum(),
                sortSubs);
        return Result.buildSuc(reassignTasks);
    }

    private List<ReassignTask> generateReassignmentTask(Integer parallelNum, List<ReplaceReassignSub> sortSubs){
        List<ReassignTask> reassignTasks = new ArrayList<>();

        //统计当前任务broker的并行度
        Map<Integer,Integer> bParallelNum = new HashMap<>();
        sortSubs.forEach(sub->{
            Integer leader = sub.getOriginalBrokerIdList().get(0);
            // 获取需要移入的副本
            Set<Integer> newReplicas = new HashSet<>(sub.getReassignBrokerIdList());
            newReplicas.removeAll(sub.getOriginalBrokerIdList());
            if (newReplicas == null || newReplicas.size() == 0){
                //只切换leader
                reassignTasks.add(new ReassignTask(sub.getTopicName(), sub.getPartitionId(), sub.getReassignBrokerIdList()));
                return;
            }

            //若分区的所有副本都未计入并行度中，则遵循一个分区的任务不拆封的原则，不管是否超过并行数都加入到任务
            ReassignTask task = getFirstTaskByReplace(bParallelNum, parallelNum, newReplicas, sub);
            if (task != null){
                //更新当前任务并行度
                modifyParallelNum(bParallelNum, newReplicas, leader);
                reassignTasks.add(task);
                return;
            }

            //是否满足并行度要求
            if(!checkParallelNum(bParallelNum, newReplicas, parallelNum, leader)){
                return;
            }
            //更新当前任务并行度
            modifyParallelNum(bParallelNum, newReplicas, leader);
            reassignTasks.add(new ReassignTask(sub.getTopicName(), sub.getPartitionId(), sub.getReassignBrokerIdList()));
        });

        return reassignTasks;
    }

    private Boolean checkParallelNum(Map<Integer,Integer> bParallelNum,
                                     Set<Integer> newReplicas,
                                     Integer parallelNum,
                                     Integer leader){
        if(bParallelNum.getOrDefault(leader, 0) + newReplicas.size() > parallelNum){
            return Boolean.FALSE;
        }
        for (Integer brokerId : newReplicas){
            Integer brokerParallelNum = bParallelNum.getOrDefault(brokerId, 0);
            if (brokerParallelNum + 1 > parallelNum){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private ReassignTask getFirstTaskByReplace(Map<Integer,Integer> bParallelNum,
                                               Integer parallelNum,
                                               Set<Integer> newReplicas,
                                               ReplaceReassignSub sub){
        Integer leader = sub.getReassignBrokerIdList().get(0);

        if (bParallelNum.get(leader) != null){
            return null;
        }
        //若分区的所有副本都未计入并行度中，则遵循一个分区的任务不拆封的原则，不管是否超过并行数都加入到任务
        for(Integer brokerId : newReplicas) {
            Integer brokerParallelNum = bParallelNum.get(brokerId);
            if (brokerParallelNum != null){
                return null;
            }
        }
        return new ReassignTask(sub.getTopicName(), sub.getPartitionId(), sub.getReassignBrokerIdList());

    }

    private Result<String> modifyParallelNum(Map<Integer,Integer> bParallelNum, Set<Integer> newReplicas, Integer leader){
        bParallelNum.put(leader, bParallelNum.getOrDefault(leader, 0) + newReplicas.size());
        newReplicas.forEach(brokerId -> {
            bParallelNum.put(brokerId, bParallelNum.getOrDefault(brokerId, 0) + 1);
        });
        return Result.buildSuc();
    }

    private List<ReplaceReassignSub> sortByReplaceLogSize(ReassignExecutionStrategy executionStrategy){
        List<ReplaceReassignSub> reassignSubs = executionStrategy.getReplaceReassignSubs();
        reassignSubs.forEach(sub -> {
            sub.setReplaceLogSize(
                    getReplicaLogSize(executionStrategy.getClusterPhyId(),
                            sub.getOriginalBrokerIdList().get(0),
                            sub.getTopicName(),
                            sub.getPartitionId())
            );
        });

        //优先最大副本
        if (executionStrategy.equals(Constant.NUM_ONE)){
            return reassignSubs.stream().sorted(Comparator.comparing(ReplaceReassignSub::getReplaceLogSize).reversed()).collect(Collectors.toList());
        }
        //优先最小副本
        return reassignSubs.stream().sorted(Comparator.comparing(ReplaceReassignSub::getReplaceLogSize)).collect(Collectors.toList());
    }

    private Float getReplicaLogSize(Long clusterPhyId, Integer brokerId, String topicName, Integer partitionId) {
        Result<ReplicationMetrics> replicaMetricsResult = replicationMetricService.collectReplicaMetricsFromKafka(
                clusterPhyId,
                topicName,
                partitionId,
                brokerId,
                ReplicaMetricVersionItems.REPLICATION_METRIC_LOG_SIZE
        );

        if (!replicaMetricsResult.hasData()) {
            return 0F;
        }

        return replicaMetricsResult.getData().getMetric(ReplicaMetricVersionItems.REPLICATION_METRIC_LOG_SIZE);
    }
}
