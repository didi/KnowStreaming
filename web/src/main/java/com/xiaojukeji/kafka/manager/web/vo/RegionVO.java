package com.xiaojukeji.kafka.manager.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Region信息
 * @author zengqiao
 * @date 19/4/1
 */
@ApiModel(value = "RegionVO", description = "Region信息")
public class RegionVO {
    @ApiModelProperty(value = "RegionID")
    protected Long regionId;

    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "region名称")
    private String regionName;

    @ApiModelProperty(value = "重要级别, 0:普通, 1:重要，2:重要")
    private Integer level;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "状态, -1:废弃 0:正常 1:容量已满")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Long gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Long gmtModify;

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Long gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "RegionVO{" +
                "regionId=" + regionId +
                ", clusterId=" + clusterId +
                ", regionName='" + regionName + '\'' +
                ", level=" + level +
                ", brokerIdList=" + brokerIdList +
                ", description='" + description + '\'' +
                ", operator='" + operator + '\'' +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}
