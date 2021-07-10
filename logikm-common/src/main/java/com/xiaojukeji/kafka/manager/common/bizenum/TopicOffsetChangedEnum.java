package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/8/24
 */
public enum TopicOffsetChangedEnum {
    UNKNOWN(-1, "unknown"),
    NO(0, "no"),
    YES(1, "yes"),
    ;

    private Integer code;

    private String message;

    TopicOffsetChangedEnum(Integer code, String message) {
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
        return "TopicOffsetChangedEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}