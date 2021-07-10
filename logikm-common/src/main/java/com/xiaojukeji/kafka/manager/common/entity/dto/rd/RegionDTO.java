package com.xiaojukeji.kafka.manager.common.entity.dto.rd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 创建Region
 * @author zengqiao
 * @date 19/4/3
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "RegionDTO")
public class RegionDTO {
    @ApiModelProperty(value = "ID, 更新时必须传")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "状态, 0:正常 1:已满")
    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
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

    @Override
    public String toString() {
        return "RegionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", clusterId=" + clusterId +
                ", brokerIdList=" + brokerIdList +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isNull(clusterId)
                || ValidateUtils.isNull(clusterId)
                || ValidateUtils.isEmptyList(brokerIdList)
                || ValidateUtils.isNull(status)) {
            return false;
        }
        description = ValidateUtils.isNull(description)? "": description;
        return true;
    }
}