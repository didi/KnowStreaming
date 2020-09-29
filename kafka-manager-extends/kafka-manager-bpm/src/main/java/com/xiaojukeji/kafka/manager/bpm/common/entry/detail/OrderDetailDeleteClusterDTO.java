package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 20/5/19
 */
public class OrderDetailDeleteClusterDTO extends AbstractOrderDetailData {
    private Long physicalClusterId;

    private String physicalClusterName;

    private Long logicalClusterId;

    private String logicalClusterName;

    private List<String> topicNameList;

    public Long getPhysicalClusterId() {
        return physicalClusterId;
    }

    public void setPhysicalClusterId(Long physicalClusterId) {
        this.physicalClusterId = physicalClusterId;
    }

    public String getPhysicalClusterName() {
        return physicalClusterName;
    }

    public void setPhysicalClusterName(String physicalClusterName) {
        this.physicalClusterName = physicalClusterName;
    }

    public Long getLogicalClusterId() {
        return logicalClusterId;
    }

    public void setLogicalClusterId(Long logicalClusterId) {
        this.logicalClusterId = logicalClusterId;
    }

    public String getLogicalClusterName() {
        return logicalClusterName;
    }

    public void setLogicalClusterName(String logicalClusterName) {
        this.logicalClusterName = logicalClusterName;
    }

    public List<String> getTopicNameList() {
        return topicNameList;
    }

    public void setTopicNameList(List<String> topicNameList) {
        this.topicNameList = topicNameList;
    }

    @Override
    public String toString() {
        return "OrderDetailDeleteClusterDTO{" +
                "physicalClusterId=" + physicalClusterId +
                ", physicalClusterName='" + physicalClusterName + '\'' +
                ", logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                ", topicNameList=" + topicNameList +
                '}';
    }
}