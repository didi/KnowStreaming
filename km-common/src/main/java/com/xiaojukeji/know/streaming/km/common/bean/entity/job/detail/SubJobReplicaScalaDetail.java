package com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail;

import lombok.Data;

@Data
public class SubJobReplicaScalaDetail extends SubBrokerJobDetail {
    /**
     * topic 名称
     */
    private String topicName;

    /**
     * 原副本数
     */
    private Integer oldReplicaNu;

    /**
     * 新副本数
     */
    private Integer newReplicaNu;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 需迁移MessageSize
     */
    private Double totalSize;

    /**
     * 已完成MessageSize
     */
    private Double movedSize;

    /**
     * 预计剩余时长
     */
    private Long  remainTime;

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
