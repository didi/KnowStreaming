package com.xiaojukeji.kafka.manager.common.entity;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/2
 */
public class TopicOperationResult {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "状态码, 0:成功, 其他失败")
    private Integer code;

    @ApiModelProperty(value = "信息")
    private String message;

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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TopicOperationResult{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public static TopicOperationResult buildFrom(Long clusterId, String topicName, Result rs) {
        return buildFrom(clusterId, topicName, rs.getCode(), rs.getMessage());
    }

    public static TopicOperationResult buildFrom(Long clusterId, String topicName, ResultStatus rs) {
        return buildFrom(clusterId, topicName, rs.getCode(), rs.getMessage());
    }

    private static TopicOperationResult buildFrom(Long clusterId,
                                                  String topicName,
                                                  Integer code,
                                                  String message) {
        TopicOperationResult result = new TopicOperationResult();
        result.setClusterId(clusterId);
        result.setTopicName(topicName);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}