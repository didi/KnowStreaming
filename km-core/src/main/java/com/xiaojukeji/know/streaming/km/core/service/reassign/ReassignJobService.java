package com.xiaojukeji.know.streaming.km.core.service.reassign;


import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReassignJobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReplaceReassignJob;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.reassign.ReassignSubJobPO;

import java.util.List;

public interface ReassignJobService {
    /**
     * 创建迁移任务
     */
    Result<Void> create(Long jobId, ReplaceReassignJob dto, String operator);

    /**
     * 删除迁移任务
     */
    Result<Void> delete(Long jobId, String operator);

    /**
     * 修改迁移任务
     */
    Result<Void> modify(Long jobId, ReplaceReassignJob replaceReassignJob, String operator);

    Result<Void> modifyThrottle(Long jobId, Long throttleUnitB, String operator);

    /**
     * 执行迁移任务
     */
    Result<Void> execute(Long jobId, String operator);

    /**
     * 取消迁移任务
     */
    Result<Void> cancel(Long jobId, String operator);

    /**
     * 检查迁移任务
     */
    Result<Void> verifyAndUpdateStatue(Long jobId);

    /**
     * 更新子任务中扩展字段的数据
     */
    Result<Void> getAndUpdateSubJobExtendData(Long jobId);

    /**
     * 获取迁移任务信息
     */
    List<ReassignSubJobPO> getSubJobsByJobId(Long jobId);

    /**
     * 获取按照Topic维度聚合的详情
     */
    Result<ReassignJobDetail> getJobDetailsGroupByTopic(Long jobId);

    /**
     * 依据任务状态或者其中一个任务ID
     */
    Long getOneRunningJobId(Long clusterPhyId);


    Result<Void> preferredReplicaElection(Long jobId);
}
