package com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubJobPartitionDetail {
    /**
     * partitionId
     */
    private Integer partitionId;

    /**
     * 源BrokerID
     */
    private Integer sourceBrokerId;

    /**
     * 目标BrokerID
     */
    private List<Integer> desBrokerIds;

    /**
     * 需迁移MessageSize
     */
    private Double totalSize;

    /**
     * 已完成MessageSize
     */
    private Double movedSize;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 预计剩余时长
     */
    private Long  remainTime;

    /**
     * BytesIn
     */
    private Long  byteIn;

    /**
     * 同步速率
     */
    private Long  byteMove;
}
