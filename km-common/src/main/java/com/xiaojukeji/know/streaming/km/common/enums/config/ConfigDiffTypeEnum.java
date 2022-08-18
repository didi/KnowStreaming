package com.xiaojukeji.know.streaming.km.common.enums.config;

/**
 * 配置差异类型
 */
public enum ConfigDiffTypeEnum {
    UNKNOWN(-1, "未知"),

    EQUAL(0, "相同"),

    UN_EQUAL(1, "不同"),

    ALONE_POSSESS(2, "独有"),

    ;

    ConfigDiffTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int    code;

    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ConfigDiffTypeEnum valueOf(Integer code) {
        if (code == null) {
            return ConfigDiffTypeEnum.UNKNOWN;
        }
        for (ConfigDiffTypeEnum state : ConfigDiffTypeEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return ConfigDiffTypeEnum.UNKNOWN;
    }

}
