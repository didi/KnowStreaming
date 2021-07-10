package com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/29
 */
@ApiModel(description = "逻辑集群")
public class LogicalClusterVO {
    @ApiModelProperty(value = "逻辑集群ID")
    protected Long logicalClusterId;

    @ApiModelProperty(value = "逻辑集群名称")
    private String logicalClusterName;

    @ApiModelProperty(value = "逻辑集群标识")
    private String logicalClusterIdentification;

    @ApiModelProperty(value = "物理集群ID")
    private Long physicalClusterId;

    @ApiModelProperty(value = "brokerId列表")
    private List<Long> regionIdList;

    @ApiModelProperty(value = "逻辑集群类型")
    private Integer mode;

    @ApiModelProperty(value = "所属应用")
    private String appId;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Date gmtModify;

    public Long getLogicalClusterId() {
        return logicalClusterId;
    }

    public void setLogicalClusterId(Long logicalClusterId) {
        this.logicalClusterId = logicalClusterId;
    }

    public String getLogicalClusterName() {
        return logicalClusterName;
    }

    public void setLogicalClusterName(String logicalClusterName) {
        this.logicalClusterName = logicalClusterName;
    }

    public String getLogicalClusterIdentification() {
        return logicalClusterIdentification;
    }

    public void setLogicalClusterIdentification(String logicalClusterIdentification) {
        this.logicalClusterIdentification = logicalClusterIdentification;
    }

    public Long getPhysicalClusterId() {
        return physicalClusterId;
    }

    public void setPhysicalClusterId(Long physicalClusterId) {
        this.physicalClusterId = physicalClusterId;
    }

    public List<Long> getRegionIdList() {
        return regionIdList;
    }

    public void setRegionIdList(List<Long> regionIdList) {
        this.regionIdList = regionIdList;
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
        return "LogicalClusterVO{" +
                "logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", logicalClusterIdentification='" + logicalClusterIdentification + '\'' +
                ", physicalClusterId=" + physicalClusterId +
                ", regionIdList=" + regionIdList +
                ", mode=" + mode +
                ", appId='" + appId + '\'' +
                ", description='" + description + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}