package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/4/26
 */
public enum KafkaFileEnum {
    PACKAGE(0, "Kafka压缩包", ".tgz"),

    SERVER_CONFIG(1, "KafkaServer配置", ".properties"),
            ;

    private Integer code;

    private String message;

    private String suffix;

    KafkaFileEnum(Integer code, String message, String suffix) {
        this.code = code;
        this.message = message;
        this.suffix = suffix;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getSuffix() {
        return suffix;
    }

    @Override
    public String toString() {
        return "KafkaFileEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", suffix=" + suffix +
                '}';
    }

    public static KafkaFileEnum getByCode(Integer code) {
        for (KafkaFileEnum elem: KafkaFileEnum.values()) {
            if (elem.getCode().equals(code)) {
                return elem;
            }
        }
        return null;
    }
}