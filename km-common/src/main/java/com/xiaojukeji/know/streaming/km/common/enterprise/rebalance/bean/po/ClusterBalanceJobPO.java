/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 集群均衡任务 实体类
 * 
 * @author fengqiongfeng
 * @date 2022-05-23
 */
@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "cluster_balance_job")
public class ClusterBalanceJobPO extends BasePO {

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
     * 总迁移大小
     */
    private Double totalReassignSize;

    /**
     * 总迁移副本数
     */
    private Integer totalReassignReplicaNum;

    /**
     * 移入topic
     */
    private String moveInTopicList;

    /**
     * 节点均衡详情
     */
    private String brokerBalanceDetail;

    /**
     * 任务状态 1：进行中，2：准备，3，成功，4：失败，5：取消
     */
    private Integer status;

    /**
     * 操作人
     */
    private String creator;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务完成时间
     */
    private Date finishedTime;

    /**
     * 备注说明
     */
    private String description;
}

