package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/6/29
 */
public class LogicalClusterDO {
    private Long id;

    private String name;

    private String identification;

    private Integer mode;

    private String appId;

    private Long clusterId;

    private String regionList;

    private String description;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getRegionList() {
        return regionList;
    }

    public void setRegionList(String regionList) {
        this.regionList = regionList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "LogicalClusterDO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", identification='" + identification + '\'' +
                ", mode=" + mode +
                ", appId='" + appId + '\'' +
                ", clusterId=" + clusterId +
                ", regionList='" + regionList + '\'' +
                ", description='" + description + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}