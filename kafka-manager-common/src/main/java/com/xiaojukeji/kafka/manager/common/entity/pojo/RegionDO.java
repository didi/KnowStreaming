package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

public class RegionDO implements Comparable<RegionDO> {
    private Long id;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String name;

    private Long clusterId;

    private String brokerList;

    private Long capacity;

    private Long realUsed;

    private Long estimateUsed;

    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getBrokerList() {
        return brokerList;
    }

    public void setBrokerList(String brokerList) {
        this.brokerList = brokerList;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public Long getRealUsed() {
        return realUsed;
    }

    public void setRealUsed(Long realUsed) {
        this.realUsed = realUsed;
    }

    public Long getEstimateUsed() {
        return estimateUsed;
    }

    public void setEstimateUsed(Long estimateUsed) {
        this.estimateUsed = estimateUsed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RegionDO{" +
                "id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", name='" + name + '\'' +
                ", clusterId=" + clusterId +
                ", brokerList='" + brokerList + '\'' +
                ", capacity=" + capacity +
                ", realUsed=" + realUsed +
                ", estimateUsed=" + estimateUsed +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int compareTo(RegionDO regionDO) {
        return this.id.compareTo(regionDO.id);
    }
}