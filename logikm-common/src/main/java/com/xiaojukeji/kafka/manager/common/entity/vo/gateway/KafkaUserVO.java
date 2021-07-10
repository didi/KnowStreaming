package com.xiaojukeji.kafka.manager.common.entity.vo.gateway;

/**
 * @author zengqiao
 * @date 20/7/27
 */
public class KafkaUserVO {
    private String username;

    private String password;

    private Integer operation;

    private Long timestamp;

    private Integer userType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "KafkaUserVO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", operation=" + operation +
                ", timestamp=" + timestamp +
                ", userType=" + userType +
                '}';
    }
}