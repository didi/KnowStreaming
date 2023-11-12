package com.xiaojukeji.know.streaming.km.rebalance.common.enums;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import lombok.Getter;

/**
 * 集群平衡任务类型
 * @author zengqiao
 * @date 22/03/08
 */
@Getter
@EnterpriseLoadReBalance
public enum ClusterBalanceTypeEnum {

    IMMEDIATELY(1, "立即"),

    CYCLE(2, "周期"),
    ;

    private final int type;

    private final String message;

    ClusterBalanceTypeEnum(int type, String message) {
        this.type = type;
        this.message = message;
    }
}