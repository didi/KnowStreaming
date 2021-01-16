package com.xiaojukeji.kafka.manager.common.bizenum;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author zhongyuankai_i
 * @date 20/09/03
 */
public enum  ModuleEnum {
    TOPIC(0, "Topic"),

    APP(1, "应用"),

    QUOTA(2, "配额"),

    AUTHORITY(3, "权限"),

    CLUSTER(4, "集群"),

    PARTITION(5, "分区"),

    GATEWAY_CONFIG(6, "Gateway配置"),

    UNKNOWN(-1, "未知")
            ;
    ModuleEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;

    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", code);
        map.put("message", message);
        return map;
    }

    public static ModuleEnum valueOf(Integer code) {
        if (code == null) {
            return ModuleEnum.UNKNOWN;
        }
        for (ModuleEnum state : ModuleEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return ModuleEnum.UNKNOWN;
    }

    public static boolean validate(Integer code) {
        if (code == null) {
            return false;
        }
        for (ModuleEnum state : ModuleEnum.values()) {
            if (state.getCode() == code) {
                return true;
            }
        }

        return false;
    }

}
