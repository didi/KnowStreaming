package com.xiaojukeji.kafka.manager.web.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Topic信息
 * @author zengqiao
 * @date 19/4/1
 */
@ApiModel(value = "TopicOverviewVO", description = "Topic信息")
public class TopicOverviewVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "每秒流入流量(B)")
    private Double byteIn;

    @ApiModelProperty(value = "发送请求数(个/秒)")
    private Double produceRequest;

    @ApiModelProperty(value = "Topic更新时间")
    private Long updateTime;

    @ApiModelProperty(value = "负责人")
    private String principals;

    @ApiModelProperty(value = "TRUE:已收藏的, FALSE:未收藏")
    private Boolean favorite;

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

    public Integer getReplicaNum() {
        return replicaNum;
    }

    public void setReplicaNum(Integer replicaNum) {
        this.replicaNum = replicaNum;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public Double getByteIn() {
        return byteIn;
    }

    public void setByteIn(Double byteIn) {
        this.byteIn = byteIn;
    }

    public Double getProduceRequest() {
        return produceRequest;
    }

    public void setProduceRequest(Double produceRequest) {
        this.produceRequest = produceRequest;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public String toString() {
        return "TopicOverviewVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", byteIn=" + byteIn +
                ", produceRequest=" + produceRequest +
                ", updateTime=" + updateTime +
                ", principals='" + principals + '\'' +
                ", favorite=" + favorite +
                '}';
    }
}
