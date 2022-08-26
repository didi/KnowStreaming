package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 迁移计划
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReassignExecutionStrategy {
    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * 任务并行数 0代表不限
     */
    private Integer parallelNum;

    /**
     * 执行策略， 1：优先最大副本，2：优先最小副本
     */
    private Integer executionStrategy;

    /**
     * 迁移执行副本信息
     */
    private List<ReplaceReassignSub> replaceReassignSubs;

}
