package com.xiaojukeji.kafka.manager.common.entity.vo.gateway;

/**
 * @author zengqiao
 * @date 20/7/27
 */
public class KafkaAclVO {
    private String topicName;

    private Long timestamp;

    private Integer access;

    private Integer operation;

    private String username;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "KafkaAclVO{" +
                "topicName='" + topicName + '\'' +
                ", timestamp=" + timestamp +
                ", access=" + access +
                ", operation=" + operation +
                ", username='" + username + '\'' +
                '}';
    }
}