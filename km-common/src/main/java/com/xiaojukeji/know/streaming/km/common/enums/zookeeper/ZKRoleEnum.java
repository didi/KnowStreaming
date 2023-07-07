package com.xiaojukeji.know.streaming.km.common.enums.zookeeper;

import lombok.Getter;

@Getter
public enum ZKRoleEnum {
    LEADER("leader"),

    FOLLOWER("follower"),

    OBSERVER("observer"),

    STANDALONE("standalone"),

    UNKNOWN("unknown"),

    ;

    private final String role;

    ZKRoleEnum(String role) {
        this.role = role;
    }
}
