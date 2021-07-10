package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-03-26
 */
@ApiModel(value = "TopicOffset信息")
public class TopicOffsetVO {
    @ApiModelProperty(value = "集群id")
    private Long clusterId;

    @ApiModelProperty(value = "topic名字")
    private String topicName;

    @ApiModelProperty(value = "分区编号")
    private Integer partitionId;

    @ApiModelProperty(value = "分区offset")
    private Long offset;

    @ApiModelProperty(value = "该offset对应的时间")
    private Long timestamp;

    public TopicOffsetVO(Long clusterId, String topicName, Integer partitionId, Long offset, Long timestamp) {
        this.clusterId = clusterId;
        this.topicName = topicName;
        this.partitionId = partitionId;
        this.offset = offset;
        this.timestamp = timestamp;
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

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TopicOffsetVO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", partitionId=" + partitionId +
                ", offset=" + offset +
                ", timestamp=" + timestamp +
                '}';
    }
}
