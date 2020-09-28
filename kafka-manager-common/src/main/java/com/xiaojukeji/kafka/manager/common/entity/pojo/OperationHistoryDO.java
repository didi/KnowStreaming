package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/4/29
 */
public class OperationHistoryDO {
    private Long id;

    private Date gmtCreate;

    private Long clusterId;

    private String topicName;

    private String operator;

    private String operation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "OperationHistoryDO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", operator='" + operator + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }

    public static OperationHistoryDO newInstance(Long clusterId, String topicName, String operator, String operation) {
        OperationHistoryDO operationHistoryDO = new OperationHistoryDO();
        operationHistoryDO.setClusterId(clusterId);
        operationHistoryDO.setTopicName(topicName);
        operationHistoryDO.setOperator(operator);
        operationHistoryDO.setOperation(operation);
        return operationHistoryDO;
    }
}