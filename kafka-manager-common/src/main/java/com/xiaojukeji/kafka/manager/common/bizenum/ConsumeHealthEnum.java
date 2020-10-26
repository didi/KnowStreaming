package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 消费健康
 * @author zengqiao
 * @date 20/5/22
 */
public enum ConsumeHealthEnum {
    UNKNOWN(-1, "unknown"),
    HEALTH(0, "health"),
    UNHEALTH(1, "unhealth"),
    ;

    private Integer code;

    private String message;

    ConsumeHealthEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ConsumeHealthEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
