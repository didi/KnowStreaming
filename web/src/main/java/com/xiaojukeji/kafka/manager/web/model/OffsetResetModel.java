package com.xiaojukeji.kafka.manager.web.model;

import com.xiaojukeji.kafka.manager.common.constant.OffsetStoreLocation;
import com.xiaojukeji.kafka.manager.common.entity.dto.PartitionOffsetDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 重置offset
 * @author zengqiao
 * @date 19/4/8
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "OffsetResetModel", description = "重置消费偏移")
public class OffsetResetModel {
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

    public boolean legal() {
        if (clusterId == null
                || StringUtils.isEmpty(topicName)
                || StringUtils.isEmpty(consumerGroup)
                || OffsetStoreLocation.getOffsetStoreLocation(location) == null) {
            return false;
        }
        if (timestamp == null && offsetList == null) {
            return false;
        }
        return true;
    }
}