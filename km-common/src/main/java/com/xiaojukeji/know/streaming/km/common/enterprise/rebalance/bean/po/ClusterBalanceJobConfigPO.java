/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 集群均衡任务 实体类
 * 
 * @author fengqiongfeng
 * @date 2022-05-23
 */
@Data
@EnterpriseLoadReBalance
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "cluster_balance_job_config")
public class ClusterBalanceJobConfigPO extends BasePO {

/**
 * 序列化版本号
 */
private static final long serialVersionUID=1L;

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * topic黑名单
     */
    private String topicBlackList;

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
    private Long throttleUnitB;

    /**
     * 操作人
    */
    private String creator;

    /**
     * 任务状态 0：未开启，1：开启
     */
    private Integer status;
}

