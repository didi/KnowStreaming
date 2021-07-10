package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 优先副本选举维度
 * @author zengqiao
 * @date 20/4/23
 */
public enum RebalanceDimensionEnum {
    CLUSTER(0, "Cluster维度"),
    REGION(1, "Region维度"),
    BROKER(2, "Broker维度"),
    TOPIC(3, "Topic维度"),
    PARTITION(4, "Partition维度"),
    ;

    private Integer code;

    private String message;

    RebalanceDimensionEnum(Integer code, String message) {
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
        return "RebalanceDimensionEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}