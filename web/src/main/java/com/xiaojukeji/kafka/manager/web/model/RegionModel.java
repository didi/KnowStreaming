package com.xiaojukeji.kafka.manager.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 创建Region
 * @author zengqiao
 * @date 19/4/3
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "RegionModel", description = "创建Region")
public class RegionModel {
    @ApiModelProperty(value = "RegionId, 更新时必须传")
    private Long regionId;

    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "regionName名称")
    private String regionName;

    @ApiModelProperty(value = "重要级别, 0:普通, 1:重要，2:重要")
    private Integer level;

    @ApiModelProperty(value = "状态, -1:废弃 0:正常 1:容量已满")
    private Integer status;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "备注")
    private String description;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public boolean legal() {
        if (clusterId == null
                || StringUtils.isEmpty(regionName)
                || level == null
                || brokerIdList == null || brokerIdList.isEmpty()) {
            return false;
        }
        if (description == null) {
            description = "";
        }
        return true;
    }

    @Override
    public String toString() {
        return "RegionModel{" +
                "regionId=" + regionId +
                ", clusterId=" + clusterId +
                ", regionName='" + regionName + '\'' +
                ", level=" + level +
                ", status=" + status +
                ", brokerIdList=" + brokerIdList +
                ", description='" + description + '\'' +
                '}';
    }
}