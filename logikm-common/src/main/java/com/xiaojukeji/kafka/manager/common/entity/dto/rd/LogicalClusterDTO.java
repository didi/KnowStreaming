package com.xiaojukeji.kafka.manager.common.entity.dto.rd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.bizenum.ClusterModeEnum;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/29
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "LogicalClusterDTO")
public class LogicalClusterDTO {
    @ApiModelProperty(value = "ID, 更新时必须传")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "集群标识, 用于告警的上报")
    private String identification;

    @ApiModelProperty(value = "集群模式")
    private Integer mode;

    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "regionId列表")
    private List<Long> regionIdList;

    @ApiModelProperty(value = "所属应用ID")
    private String appId;

    @ApiModelProperty(value = "备注")
    private String description;

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

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<Long> getRegionIdList() {
        return regionIdList;
    }

    public void setRegionIdList(List<Long> regionIdList) {
        this.regionIdList = regionIdList;
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

    @Override
    public String toString() {
        return "LogicalClusterDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", identification='" + identification + '\'' +
                ", mode=" + mode +
                ", clusterId=" + clusterId +
                ", regionIdList=" + regionIdList +
                ", appId='" + appId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(clusterId)
                || ValidateUtils.isEmptyList(regionIdList)
                || ValidateUtils.isNull(mode)) {
            return false;
        }
        if (!ClusterModeEnum.SHARED_MODE.getCode().equals(mode) && ValidateUtils.isNull(appId)) {
            return false;
        }
        appId = ValidateUtils.isNull(appId)? "": appId;
        description = ValidateUtils.isNull(description)? "": description;
        identification = ValidateUtils.isNull(identification)? name: identification;
        return true;
    }
}