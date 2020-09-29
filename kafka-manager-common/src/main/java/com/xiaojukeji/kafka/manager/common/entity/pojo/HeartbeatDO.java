package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * 心跳
 * @author zengqiao
 * @date 20/8/10
 */
public class HeartbeatDO implements Comparable<HeartbeatDO> {
    private Long id;

    private String ip;

    private String hostname;

    private Date createTime;

    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "HeartbeatDO{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", hostname='" + hostname + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                '}';
    }

    @Override
    public int compareTo(HeartbeatDO heartbeatDO) {
        return this.id.compareTo(heartbeatDO.id);
    }
}