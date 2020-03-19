package com.xiaojukeji.kafka.manager.web.model.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

/**
 * @author zengqiao
 * @date 19/6/16
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "OrderPartitionModel", description = "Partition申请工单")
public class OrderPartitionModel {
    @ApiModelProperty(value = "orderId, 创建工单时忽略, 更新工单时必须传")
    private Long orderId;

    @ApiModelProperty(value = "集群Id")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "预计峰值流量(MB/s)")
    private Double predictBytesIn;

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

    public Double getPredictBytesIn() {
        return predictBytesIn;
    }

    public void setPredictBytesIn(Double predictBytesIn) {
        this.predictBytesIn = predictBytesIn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "OrderTopicApplyModel{" +
                "orderId=" + orderId +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", predictBytesIn=" + predictBytesIn +
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
        if (clusterId == null || clusterId < 0
                || StringUtils.isEmpty(topicName)
                || predictBytesIn == null || predictBytesIn < 0
                || StringUtils.isEmpty(description)) {
            return false;
        }
        return true;
    }
}