package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/2/28
 */
public class ControllerDO  {
    private Long id;

    private Date gmtCreate;

    private Long clusterId;

    private Integer brokerId;

    private String host;

    private Long timestamp;

    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "ControllerDO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", timestamp=" + timestamp +
                ", version=" + version +
                '}';
    }

    public static ControllerDO newInstance(Long clusterId,
                                           Integer brokerId,
                                           String host,
                                           Long timestamp,
                                           Integer version) {
        ControllerDO controllerDO = new ControllerDO();
        controllerDO.setClusterId(clusterId);
        controllerDO.setBrokerId(brokerId);
        controllerDO.setHost(host == null? "": host);
        controllerDO.setTimestamp(timestamp);
        controllerDO.setVersion(version);
        return controllerDO;
    }
}