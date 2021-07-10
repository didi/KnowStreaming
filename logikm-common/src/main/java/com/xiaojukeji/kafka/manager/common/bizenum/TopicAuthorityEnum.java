package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * topic权限
 * @author zhongyuankai
 * @date 20/4/29
 */
public enum TopicAuthorityEnum {
    DENY(0, "无"),

    READ(1, "只读"),

    WRITE(2, "只写"),

    READ_WRITE(3, "可读可写"),

    OWNER(4, "可管理"),
    ;

    private Integer code;

    private String message;

    TopicAuthorityEnum(Integer code, String message) {
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
        return "TopicAuthorityEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
