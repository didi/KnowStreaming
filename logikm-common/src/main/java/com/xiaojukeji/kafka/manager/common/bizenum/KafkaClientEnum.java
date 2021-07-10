package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/5/29
 */
public enum KafkaClientEnum {
    PRODUCE_CLIENT(0, "Produce"),

    FETCH_CLIENT(1, "Fetch"),

    ;

    private Integer code;

    private String name;

    KafkaClientEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "KafkaClientEnum{" +
                "code=" + code +
                ", name='" + name + '\'' +
                '}';
    }
}