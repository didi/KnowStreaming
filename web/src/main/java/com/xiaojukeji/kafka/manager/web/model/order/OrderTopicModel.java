package com.xiaojukeji.kafka.manager.web.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/6/16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "OrderTopicApplyModel", description = "Topic申请工单")
public class OrderTopicModel {
    @ApiModelProperty(value = "工单Id, 创建工单时忽略, 更新工单时必须传")
    private Long orderId;

    @ApiModelProperty(value = "集群Id")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "负责人列表")
    private List<String> principalList;

    @ApiModelProperty(value = "流量上限(MB/s)")
    private Double peakBytesIn;

    @ApiModelProperty(value = "保存时间(H)")
    private Long retentionTime;

    @ApiModelProperty(value = "备注说明")
    private String description;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public List<String> getPrincipalList() {
        return principalList;
    }

    public void setPrincipalList(List<String> principalList) {
        this.principalList = principalList;
    }

    public Double getPeakBytesIn() {
        return peakBytesIn;
    }

    public void setPeakBytesIn(Double peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "OrderTopicModel{" +
                "orderId=" + orderId +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", principalList='" + principalList + '\'' +
                ", peakBytesIn=" + peakBytesIn +
                ", retentionTime=" + retentionTime +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean modifyLegal() {
        if (orderId == null || !createLegal()) {
            return false;
        }
        return true;
    }

    public boolean createLegal() {
        if (clusterId == null
                || StringUtils.isEmpty(topicName)
                || principalList == null || principalList.isEmpty()
                || peakBytesIn == null || peakBytesIn < 0
                || retentionTime == null || retentionTime < 0
                || StringUtils.isEmpty(description)) {
            return false;
        }
        return true;
    }
}