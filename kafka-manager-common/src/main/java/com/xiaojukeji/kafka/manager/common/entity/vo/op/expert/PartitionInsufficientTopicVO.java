package com.xiaojukeji.kafka.manager.common.entity.vo.op.expert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/30
 */
@ApiModel(description = "分区不足Topic")
public class PartitionInsufficientTopicVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "Region名称")
    private String regionName;

    @ApiModelProperty(value = "Topic所属BrokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "当前分区数")
    private Integer presentPartitionNum;

    @ApiModelProperty(value = "建议分区数")
    private Integer suggestedPartitionNum;

    @ApiModelProperty(value = "单分区流量(B/s)")
    private Double bytesInPerPartition;

    @ApiModelProperty(value = "今天，昨天，前天的峰值均值流入流量(B/s)")
    private List<Double> maxAvgBytesInList;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public Integer getPresentPartitionNum() {
        return presentPartitionNum;
    }

    public void setPresentPartitionNum(Integer presentPartitionNum) {
        this.presentPartitionNum = presentPartitionNum;
    }

    public Integer getSuggestedPartitionNum() {
        return suggestedPartitionNum;
    }

    public void setSuggestedPartitionNum(Integer suggestedPartitionNum) {
        this.suggestedPartitionNum = suggestedPartitionNum;
    }

    public Double getBytesInPerPartition() {
        return bytesInPerPartition;
    }

    public void setBytesInPerPartition(Double bytesInPerPartition) {
        this.bytesInPerPartition = bytesInPerPartition;
    }

    public List<Double> getMaxAvgBytesInList() {
        return maxAvgBytesInList;
    }

    public void setMaxAvgBytesInList(List<Double> maxAvgBytesInList) {
        this.maxAvgBytesInList = maxAvgBytesInList;
    }

    @Override
    public String toString() {
        return "PartitionInsufficientTopicVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", regionName='" + regionName + '\'' +
                ", brokerIdList=" + brokerIdList +
                ", presentPartitionNum=" + presentPartitionNum +
                ", suggestedPartitionNum=" + suggestedPartitionNum +
                ", bytesInPerPartition=" + bytesInPerPartition +
                ", maxAvgBytesInList=" + maxAvgBytesInList +
                '}';
    }
}