package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 集群模式
 * @author zengqiao
 * @date 20/4/1
 */
public enum ClusterModeEnum {
    /**
     * 共享模式
     */
    SHARED_MODE(0, "共享集群"),

    /**
     * 独享模式
     */
    EXCLUSIVE_MODE(1, "独享集群"),

    /**
     * 独立模式
     */
    INDEPENDENT_MODE(2, "独立集群");

    private Integer code;

    private String message;

    ClusterModeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ClusterModeEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}