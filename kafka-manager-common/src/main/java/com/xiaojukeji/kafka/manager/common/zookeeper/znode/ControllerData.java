package com.xiaojukeji.kafka.manager.common.zookeeper.znode;

/**
 * @author zengqiao
 * @date 19/4/22
 */
public class ControllerData {
    private Integer brokerid;

    private Integer version;

    private Long timestamp;

    public Integer getBrokerid() {
        return brokerid;
    }

    public void setBrokerid(Integer brokerid) {
        this.brokerid = brokerid;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ControllerData{" +
                "brokerid=" + brokerid +
                ", version=" + version +
                ", timestamp=" + timestamp +
                '}';
    }
}