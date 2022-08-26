package com.xiaojukeji.know.streaming.km.common.enums.cluster;

import lombok.Getter;

/**
 * 集群运行状态
 * @author zengqiao
 * @date 22/03/08
 */
@Getter
public enum ClusterRunStateEnum {
    DEAD(0, "未监控"),

    RUN_ZK(1, "ZK模式"),

    RUN_ZK_BUT_IGNORE_ZK(11, "ZK模式，但忽略ZK"),

    RUN_RAFT(2, "Raft模式")
    ;

    private final int runState;

    private final String message;

    ClusterRunStateEnum(int runState, String message) {
        this.runState = runState;
        this.message = message;
    }
}