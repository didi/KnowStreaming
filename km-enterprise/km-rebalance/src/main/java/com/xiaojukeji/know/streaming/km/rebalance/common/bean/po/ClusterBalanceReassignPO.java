/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.rebalance.common.bean.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xiaojukeji.know.streaming.km.common.bean.po.BasePO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 集群平衡迁移详情 实体类
 * 
 * @author fengqiongfeng
 * @date 2022-05-23
 */
@Data
@NoArgsConstructor
@TableName(Constant.MYSQL_TABLE_NAME_PREFIX + "cluster_balance_reassign")
public class ClusterBalanceReassignPO extends BasePO {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID=1L;
    /**
     * jobID
     */
    private Long jobId;

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 分区ID
     */
    private Integer partitionId;

    /**
     * 源BrokerId列表
     */
    private String originalBrokerIds;

    /**
     * 目标BrokerId列表
     */
    private String reassignBrokerIds;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务完成时间
     */
    private Date finishedTime;

    /**
     * 扩展数据
     */
    private String extendData;

    /**
     * 任务状态
     */
    private Integer status;

}

