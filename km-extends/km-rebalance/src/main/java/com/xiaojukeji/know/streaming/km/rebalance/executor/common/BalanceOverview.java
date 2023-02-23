package com.xiaojukeji.know.streaming.km.rebalance.executor.common;

import com.xiaojukeji.know.streaming.km.rebalance.model.Resource;

import java.util.Map;

public class BalanceOverview {
    //任务类型
    private String taskType;
    //节点范围
    private String nodeRange;
    //总的迁移大小
    private double totalMoveSize;
    //topic黑名单
    private String topicBlacklist;
    //迁移副本数
    private int moveReplicas;
    //迁移Topic
    private String moveTopics;
    //均衡阈值
    private Map<Resource, Double> balanceThreshold;
    //移除节点
    private String removeNode;

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getNodeRange() {
        return nodeRange;
    }

    public void setNodeRange(String nodeRange) {
        this.nodeRange = nodeRange;
    }

    public double getTotalMoveSize() {
        return totalMoveSize;
    }

    public void setTotalMoveSize(double totalMoveSize) {
        this.totalMoveSize = totalMoveSize;
    }

    public String getTopicBlacklist() {
        return topicBlacklist;
    }

    public void setTopicBlacklist(String topicBlacklist) {
        this.topicBlacklist = topicBlacklist;
    }

    public int getMoveReplicas() {
        return moveReplicas;
    }

    public void setMoveReplicas(int moveReplicas) {
        this.moveReplicas = moveReplicas;
    }

    public String getMoveTopics() {
        return moveTopics;
    }

    public void setMoveTopics(String moveTopics) {
        this.moveTopics = moveTopics;
    }

    public Map<Resource, Double> getBalanceThreshold() {
        return balanceThreshold;
    }

    public void setBalanceThreshold(Map<Resource, Double> balanceThreshold) {
        this.balanceThreshold = balanceThreshold;
    }

    public String getRemoveNode() {
        return removeNode;
    }

    public void setRemoveNode(String removeNode) {
        this.removeNode = removeNode;
    }

    @Override
    public String toString() {
        return "BalanceOverview{" +
                "taskType='" + taskType + '\'' +
                ", nodeRange='" + nodeRange + '\'' +
                ", totalMoveSize=" + totalMoveSize +
                ", topicBlacklist='" + topicBlacklist + '\'' +
                ", moveReplicas=" + moveReplicas +
                ", moveTopics='" + moveTopics + '\'' +
                ", balanceThreshold=" + balanceThreshold +
                ", removeNode='" + removeNode + '\'' +
                '}';
    }
}
