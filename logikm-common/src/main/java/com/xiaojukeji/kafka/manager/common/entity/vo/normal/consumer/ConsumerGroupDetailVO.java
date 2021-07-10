package com.xiaojukeji.kafka.manager.common.entity.vo.normal.consumer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/4/3
 */
@ApiModel(value = "消费组的消费详情")
public class ConsumerGroupDetailVO {
    @ApiModelProperty(value = "topic名称")
    private String topicName;

    @ApiModelProperty(value = "消费组名称")
    private String consumerGroup;

    @ApiModelProperty(value = "location")
    private String location;

    @ApiModelProperty(value = "分区Id")
    private Integer partitionId;

    @ApiModelProperty(value = "clientId")
    private String clientId;

    @ApiModelProperty(value = "消费偏移量")
    private Long consumeOffset;

    @ApiModelProperty(value = "partitionOffset")
    private Long partitionOffset;

    @ApiModelProperty(value = "lag")
    private Long lag;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Long getConsumeOffset() {
        return consumeOffset;
    }

    public void setConsumeOffset(Long consumeOffset) {
        this.consumeOffset = consumeOffset;
    }

    public Long getPartitionOffset() {
        return partitionOffset;
    }

    public void setPartitionOffset(Long partitionOffset) {
        this.partitionOffset = partitionOffset;
    }

    public Long getLag() {
        return lag;
    }

    public void setLag(Long lag) {
        this.lag = lag;
    }

    @Override
    public String toString() {
        return "ConsumerGroupDetailVO{" +
                "topicName='" + topicName + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", partitionId=" + partitionId +
                ", clientId='" + clientId + '\'' +
                ", consumeOffset=" + consumeOffset +
                ", partitionOffset=" + partitionOffset +
                ", lag=" + lag +
                '}';
    }
}
