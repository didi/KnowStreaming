package com.xiaojukeji.kafka.manager.common.entity.dto.normal;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 重置offset
 * @author zengqiao
 * @date 19/4/8
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TopicOffsetResetDTO {
    @ApiModelProperty(value = "集群Id")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "消费组")
    private String consumerGroup;

    @ApiModelProperty(value = "存储位置")
    private String location;

    @ApiModelProperty(value = "重置到指定offset")
    private List<PartitionOffsetDTO> offsetList;

    @ApiModelProperty(value = "重置到指定时间")
    private Long timestamp;

    @ApiModelProperty(value = "是否是物理集群ID")
    private Boolean isPhysicalClusterId;

    @ApiModelProperty(value = "指定offset的位置（0 不指定，1 最旧的offset，2 最新的offset）")
    private Integer offsetPos;

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

    public List<PartitionOffsetDTO> getOffsetList() {
        return offsetList;
    }

    public void setOffsetList(List<PartitionOffsetDTO> offsetList) {
        this.offsetList = offsetList;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getIsPhysicalClusterId() {
        return isPhysicalClusterId;
    }

    public void setIsPhysicalClusterId(Boolean isPhysicalClusterId) {
        this.isPhysicalClusterId = isPhysicalClusterId;
    }

    public Integer getOffsetPos() {
        return offsetPos;
    }

    public void setOffsetPos(Integer offsetPos) {
        this.offsetPos = offsetPos;
    }

    @Override
    public String toString() {
        return "TopicOffsetResetDTO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", location='" + location + '\'' +
                ", offsetList=" + offsetList +
                ", timestamp=" + timestamp +
                ", isPhysicalClusterId=" + isPhysicalClusterId +
                ", offsetPos=" + offsetPos +
                '}';
    }

    public boolean legal() {
        if (clusterId == null
                || ValidateUtils.isExistBlank(topicName)
                || ValidateUtils.isExistBlank(consumerGroup)
                || OffsetLocationEnum.getOffsetStoreLocation(location) == null) {
            return false;
        }

        if (isPhysicalClusterId == null) {
            isPhysicalClusterId = false;
        }
        if (timestamp == null && offsetList == null && ValidateUtils.isNullOrLessThanZero(offsetPos)) {
            return false;
        }
        if (ValidateUtils.isNull(offsetPos)) {
            offsetPos = 0;
        }
        return true;
    }
}