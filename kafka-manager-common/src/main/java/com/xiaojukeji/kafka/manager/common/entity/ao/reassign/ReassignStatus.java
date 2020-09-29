package com.xiaojukeji.kafka.manager.common.entity.ao.reassign;

import com.xiaojukeji.kafka.manager.common.bizenum.TaskStatusReassignEnum;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.ReassignmentElemData;
import kafka.common.TopicAndPartition;

import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/5/14
 */
public class ReassignStatus {
    private Long subTaskId;

    private Long clusterId;

    private String clusterName;

    private String topicName;

    private Integer status;

    private Long realThrottle;

    private Long maxThrottle;

    private Long minThrottle;

    private List<ReassignmentElemData> reassignList;

    private Map<TopicAndPartition, TaskStatusReassignEnum> reassignStatusMap;

    public Long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(Long subTaskId) {
        this.subTaskId = subTaskId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRealThrottle() {
        return realThrottle;
    }

    public void setRealThrottle(Long realThrottle) {
        this.realThrottle = realThrottle;
    }

    public Long getMaxThrottle() {
        return maxThrottle;
    }

    public void setMaxThrottle(Long maxThrottle) {
        this.maxThrottle = maxThrottle;
    }

    public Long getMinThrottle() {
        return minThrottle;
    }

    public void setMinThrottle(Long minThrottle) {
        this.minThrottle = minThrottle;
    }

    public List<ReassignmentElemData> getReassignList() {
        return reassignList;
    }

    public void setReassignList(List<ReassignmentElemData> reassignList) {
        this.reassignList = reassignList;
    }

    public Map<TopicAndPartition, TaskStatusReassignEnum> getReassignStatusMap() {
        return reassignStatusMap;
    }

    public void setReassignStatusMap(Map<TopicAndPartition, TaskStatusReassignEnum> reassignStatusMap) {
        this.reassignStatusMap = reassignStatusMap;
    }

    @Override
    public String toString() {
        return "ReassignStatus{" +
                "subTaskId=" + subTaskId +
                ", clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", status=" + status +
                ", realThrottle=" + realThrottle +
                ", maxThrottle=" + maxThrottle +
                ", minThrottle=" + minThrottle +
                ", reassignList=" + reassignList +
                ", reassignStatusMap=" + reassignStatusMap +
                '}';
    }
}