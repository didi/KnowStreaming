package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author zengqiao
 * @date 19/7/8
 */
@ApiModel(value = "Topic删除结果")
public class TopicDeleteVO implements Serializable {
    @ApiModelProperty(value = "集群Id")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "删除信息")
    private String message;

    @ApiModelProperty(value = "删除code")
    private Integer code;

    public TopicDeleteVO(Long clusterId, String topicName, String message, Integer code) {
        this.clusterId = clusterId;
        this.topicName = topicName;
        this.message = message;
        this.code = code;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "TopicDeleteInfoVO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
