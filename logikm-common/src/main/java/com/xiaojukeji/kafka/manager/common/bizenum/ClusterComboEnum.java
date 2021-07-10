package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/4/21
 */
public enum ClusterComboEnum {
    BYTES_IN_200(200*1024*1024, "200MB/s"),
    BYTES_IN_400(400*1024*1024, "400MB/s"),
    BYTES_IN_600(600*1024*1024, "600MB/s"),
    ;

    private Integer code;

    private String message;

    ClusterComboEnum(Integer code, String message) {
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
        return "ClusterComboEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}