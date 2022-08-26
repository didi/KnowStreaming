package com.xiaojukeji.know.streaming.km.common.enums.config;

/**
 * 操作记录模块枚举
 *
 * Created by d06679 on 2017/7/14.
 */
public enum ConfigGroupEnum {
                                      /**集群*/
                                      CLUSTER(1, "集群"),

                                      TEMPLATE(2, "模板"),

                                      RESOURCE(3, "资源"),

                                      BROKER_SPEC(4, "集群规格"),

                                      CLUSTER_BALANCE_INTERVAL(5, "集群平衡区间"),

                                      HEALTH(10, "健康检查及健康分"),

                                      UNKNOWN(-1, "未知");

    ConfigGroupEnum(int code, String desc) {
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

    public static ConfigGroupEnum valueOf(Integer code) {
        if (code == null) {
            return ConfigGroupEnum.UNKNOWN;
        }
        for (ConfigGroupEnum state : ConfigGroupEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return ConfigGroupEnum.UNKNOWN;
    }

}
