package com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.po.ClusterBalanceJobPO;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.po.ClusterBalanceReassignPO;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.vo.ClusterBalanceHistoryVO;

import java.util.List;
import java.util.Map;

@EnterpriseLoadReBalance
public interface ClusterBalanceJobService {
    /**
     *
     * @param jobId jobId
     * @return
     */
    Result<Void> deleteByJobId(Long jobId, String operator);

    /**
     *
     * @param clusterBalanceJobPO
     * @return
     */
    Result<Void> createClusterBalanceJob(ClusterBalanceJobPO clusterBalanceJobPO, String operator);

    /**
     *
     * @param clusterBalanceJobPO
     * @return
     */
    Result<Void> modifyClusterBalanceJob(ClusterBalanceJobPO clusterBalanceJobPO, String operator);

    /**
     *
     * @param id id
     * @return
     */
    Result<ClusterBalanceJobPO> getClusterBalanceJobById(Long id);

    /**
     *
     * @param clusterPhyId
     * @return
     */
    ClusterBalanceJobPO getLastOneByClusterId(Long clusterPhyId);

    /**
     *
     * @param clusterPhyId
     * @return
     */
    Map<String, Double> getBalanceInterval(Long clusterPhyId);

    /**
     *
     * @param clusterPhyId
     * @return
     */
    PaginationResult<ClusterBalanceHistoryVO> page(Long clusterPhyId, PaginationBaseDTO dto);

    /**
     * 依据任务状态或者其中一个任务ID
     */
    Long getOneRunningJob(Long clusterPhyId);

    /**
     * 检查平衡任务
     */
    Result<Void> verifyClusterBalanceAndUpdateStatue(Long jobId);

    /**
     * 根据jobId生成迁移json
     * @param parallelNum 并行数
     * @param clusterId 集群id
     * @param executionStrategy 执行策略
     * @param reassignPOList 迁移任务详情
     * @return
     */
    Result<String> generateReassignmentJson(Long clusterId, Integer parallelNum, Integer jsonVersion, Integer executionStrategy, List<ClusterBalanceReassignPO> reassignPOList);

    /**
     * 根据迁移策略更新迁移任务
     * @param jobId jobId
     * @param clusterPhyId 集群id
     * @return
     */
    Result<Void> generateReassignmentForStrategy(Long clusterPhyId, Long jobId);

}
