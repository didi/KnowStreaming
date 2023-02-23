package com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EnterpriseLoadReBalance
public class ClusterBalanceInterval {
    /**
     * 均衡维度:cpu,disk,bytesIn,bytesOut
     */
    private String type;

    /**
     * 平衡区间百分比
     */
    private Double intervalPercent;

    /**
     * 优先级
     */
    private Integer priority;
}
