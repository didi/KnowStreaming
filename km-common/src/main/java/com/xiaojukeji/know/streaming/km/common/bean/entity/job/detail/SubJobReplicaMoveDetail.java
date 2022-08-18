package com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail;

import lombok.Data;

import java.util.List;

@Data
public class SubJobReplicaMoveDetail extends SubBrokerJobDetail {
    /**
     * topic 名称
     */
    private String topicName;

    /**
     * 分区列表
     */
    private List<Integer> partitions;

    /**
     * 当前数据保存时间
     */
    private Long currentTimeSpent;

    /**
     * 迁移数据时间范围
     */
    private Long moveTimeSpent;

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
    private Long remainTime;

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
