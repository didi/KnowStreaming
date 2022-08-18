package com.xiaojukeji.know.streaming.km.common.enums.config;

/**
 * 操作记录模块枚举
 *
 * Created by d06679 on 2017/7/14.
 */
public enum ConfigStatusEnum {
                                   /**正常*/
                                   NORMAL(1, "正常"),

                                   DISABLE(2, "禁用"),

                                   DELETED(-1, "删除");

    ConfigStatusEnum(int code, String desc) {
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

    public static ConfigStatusEnum valueOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (ConfigStatusEnum state : ConfigStatusEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return null;
    }

}
