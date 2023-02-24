package com.xiaojukeji.know.streaming.km.rebalance.common.enums;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import lombok.Getter;

/**
 * 集群平衡状态
 * @author zengqiao
 * @date 22/03/08
 */
@Getter
@EnterpriseLoadReBalance
public enum ClusterBalanceStateEnum {
    BELOW_BALANCE(-1, "低于均衡范围"),

    BALANCE(0, "均衡范围内"),

    ABOVE_BALANCE(1, "高于均衡范围"),

    UNBALANCED(2, "不均衡"),
    ;

    private final Integer state;

    private final String message;

    ClusterBalanceStateEnum(int state, String message) {
        this.state = state;
        this.message = message;
    }
}