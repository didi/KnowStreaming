package com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/05/06
 */
@Data
@EnterpriseLoadReBalance
public class ClusterBalanceReassignExtendData {
    /**
     * 原本保存时间
     */
    private Long originalRetentionTimeUnitMs;

    /**
     * 迁移时保存时间
     */
    private Long reassignRetentionTimeUnitMs;

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
    private Integer originReplicaNum;

    /**
     * 新的副本数
     */
    private Integer reassignReplicaNum;
}
