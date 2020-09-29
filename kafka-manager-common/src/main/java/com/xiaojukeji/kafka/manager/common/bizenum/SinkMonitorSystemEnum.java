package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 是否上报监控系统
 * @author zengqiao
 * @date 20/9/25
 */
public enum SinkMonitorSystemEnum {
    SINK_MONITOR_SYSTEM(0, "上报监控系统"),
    NOT_SINK_MONITOR_SYSTEM(1, "不上报监控系统"),
    ;

    private Integer code;

    private String message;

    SinkMonitorSystemEnum(Integer code, String message) {
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
        return "SinkMonitorSystemEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
