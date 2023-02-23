/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.vo;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.ClusterBalanceInterval;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 集群均衡任务 实体类
 * 
 * @author fengqiongfeng
 * @date 2022-05-23
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalanceJobConfigVO {
    /**
     * 序列化版本号
     */
    private static final long serialVersionUID=1L;

    @ApiModelProperty("集群id")
    private Long clusterId;

    @ApiModelProperty("topic黑名单")
    private List<String> topicBlackList;

    @ApiModelProperty("任务周期")
    private String scheduleCron;

    @ApiModelProperty("均衡区间详情")
    private List<ClusterBalanceInterval> clusterBalanceIntervalList;

    @ApiModelProperty("指标计算周期，单位分钟")
    private Integer metricCalculationPeriod;

    @ApiModelProperty("任务并行数")
    private Integer parallelNum;

    @ApiModelProperty("执行策略， 1：优先最大副本，2：优先最小副本")
    private Integer executionStrategy;

    @ApiModelProperty("限流值")
    private Long throttleUnitB;

    @ApiModelProperty("任务状态 0：未开启，1：开启")
    private Integer status;

}

