package com.xiaojukeji.know.streaming.km.common.enums.connect;

/**
 * @author wyb
 * @date 2022/11/25
 */
public enum ConnectorTypeEnum {


    UNKNOWN(-1, "unknown"),
    SOURCE(1, "source"),
    SINK(2, "sink");

    private final int code;

    private final String value;

    ConnectorTypeEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public static ConnectorTypeEnum getByName(String name) {
        for (ConnectorTypeEnum typeEnum : ConnectorTypeEnum.values()) {
            if (typeEnum.name().equals(name)) {
                return typeEnum;
            }
        }
        return UNKNOWN;
    }

}
