package com.xiaojukeji.know.streaming.km.common.enums.group;

import lombok.Getter;

/**
 * @author wyb
 * @date 2022/10/11
 */
@Getter
public enum GroupTypeEnum {

    UNKNOWN(-1, "Unknown"),

    CONSUMER(0, "Consumer客户端的消费组"),

    CONNECTOR(1, "Connector的消费组");

    private final Integer code;

    private final String msg;

    GroupTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static GroupTypeEnum getTypeByCode(Integer code) {
        if (code == null) return UNKNOWN;
        for (GroupTypeEnum groupTypeEnum : GroupTypeEnum.values()) {
            if (groupTypeEnum.code.equals(code)) {
                return groupTypeEnum;
            }
        }
        return UNKNOWN;
    }
}
