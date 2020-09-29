package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * Region信息
 * @author zengqiao
 * @date 19/4/1
 */
@ApiModel(description = "Region信息")
public class RegionVO {
    @ApiModelProperty(value = "RegionID")
    protected Long id;

    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Region名称")
    private String name;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "状态, 0:正常 1:容量已满")
    private Integer status;

    @ApiModelProperty(value = "容量(B/s)")
    private Long capacity;

    @ApiModelProperty(value = "实际流量(B/s)")
    private Long realUsed;

    @ApiModelProperty(value = "预估流量(B/s)")
    private Long estimateUsed;

    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
        return "RegionVO{" +
                "id=" + id +
                ", clusterId=" + clusterId +
                ", name='" + name + '\'' +
                ", brokerIdList=" + brokerIdList +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", capacity=" + capacity +
                ", realUsed=" + realUsed +
                ", estimateUsed=" + estimateUsed +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}
