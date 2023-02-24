package com.xiaojukeji.know.streaming.km.rebalance.core.service;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.ClusterBalanceReassignDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceReassignPO;

import java.util.List;

@EnterpriseLoadReBalance
public interface ClusterBalanceReassignService {

    /**
     *新增迁移任务
     * @param clusterBalanceReassignPO
     * @return
     */
    Result<Void> addBalanceReassign(ClusterBalanceReassignPO clusterBalanceReassignPO);

    /**
     *批量新增迁移任务
     * @param reassignPOList
     * @return
     */
    Result<Void> addBatchBalanceReassign(List<ClusterBalanceReassignPO> reassignPOList);
    /**
     * 删除迁移任务
     */
    Result<Void> delete(Long jobId, String operator);

    /**
     * 执行迁移任务
     */
    Result<Void> execute(Long jobId);

    /**
     * 取消迁移任务
     */
    Result<Void> cancel(Long jobId);

    /**
     * 检查迁移任务
     */
    Result<Boolean> verifyAndUpdateStatue(ClusterBalanceJobPO clusterBalanceJobPO);

    /**
     * 修改限流值
     */
    Result<Void> modifyThrottle(Long jobId, Long throttleUnitB, String operator);

    /**
     * 更新子任务中扩展字段的数据
     */
    Result<Void> getAndUpdateSubJobExtendData(Long jobId);

    /**
     * 获取迁移任务信息
     */
    List<ClusterBalanceReassignPO> getBalanceReassignsByJobId(Long jobId);

    /**
     * 获取按照Topic维度聚合的详情
     */
    Result<ClusterBalanceReassignDetail> getJobDetailsGroupByTopic(Long jobId);

    /**
     * leader重新选举
     */
    Result<Void> preferredReplicaElection(Long jobId);

}
