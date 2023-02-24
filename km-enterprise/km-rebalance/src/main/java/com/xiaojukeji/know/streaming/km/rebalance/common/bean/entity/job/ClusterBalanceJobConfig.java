/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.entity.BaseEntity;
import lombok.Data;

/**
 * 集群均衡任务 实体类
 * 
 * @author fengqiongfeng
 * @date 2022-05-23
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalanceJobConfig extends BaseEntity {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID=1L;

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * 均衡节点
     */
    private String brokers;

    /**
     * topic黑名单
     */
    private String topicBlackList;

    /**
     * 1:立即均衡，2：周期均衡
     */
    private Integer type;

    /**
     * 任务周期
     */
    private String taskCron;

    /**
     * 均衡区间详情
     */
    private String balanceIntervalJson;

    /**
     * 指标计算周期，单位分钟
     */
    private Integer metricCalculationPeriod;

    /**
     * 迁移脚本
     */
    private String reassignmentJson;

    /**
     * 任务并行数
     */
    private Integer parallelNum;

    /**
     * 执行策略， 1：优先最大副本，2：优先最小副本
     */
    private Integer executionStrategy;

    /**
     * 限流值
     */
    private Long throttleUnitByte;

    /**
     * 操作人
     */
    private String creator;

    /**
     * 任务状态 0：未开启，1：开启
     */
    private Integer status;

}

