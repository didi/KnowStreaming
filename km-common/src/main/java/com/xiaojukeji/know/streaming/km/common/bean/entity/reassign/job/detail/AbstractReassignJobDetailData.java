package com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.detail;

import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
public abstract class AbstractReassignJobDetailData {
    /**
     * 物流集群ID
     */
    private Long clusterPhyId;

    /**
     * Topic名称
     */
    private String topicName;

    /**
     * 源Broker列表
     */
    private List<Integer> originalBrokerIdList;

    /**
     * 目标Broker列表
     */
    private List<Integer> reassignBrokerIdList;

    /**
     * 需迁移LogSize
     */
    private Long needReassignLogSizeUnitB;

    /**
     * 已完成迁移LogSize
     */
    private Long finishedReassignLogSizeUnitB;

    /**
     * 预计剩余时长
     */
    private Long remainTimeUnitMs;

    /**
     * 当前副本数
     */
    private Integer presentReplicaNum;

    /**
     * 新的副本数
     */
    private Integer oldReplicaNum;

    /**
     * 新的副本数
     */
    private Integer newReplicaNum;

    /**
     * 原本保存时间
     */
    private Long originalRetentionTimeUnitMs;

    /**
     * 迁移时保存时间
     */
    private Long reassignRetentionTimeUnitMs;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 子任务成功数
     */
    private Integer total;

    /**
     * 子任务成功数
     */
    private Integer success;

    /**
     * 子任务失败数
     */
    private Integer fail;

    /**
     * 子任务进行数
     */
    private Integer doing;
}
