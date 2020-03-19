package com.xiaojukeji.kafka.manager.common.entity.po;

public class OperationHistoryDO extends BaseEntryDO {
    private Long clusterId;

    private String topicName;

    private String operator;

    private String operation;

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
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", operator='" + operator + '\'' +
                ", operation='" + operation + '\'' +
                ", id=" + id +
                ", gmtCreate=" + gmtCreate +
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