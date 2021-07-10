package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

/**
 * @author zhongyuankai
 * @date 20/5/19
 */
public class OrderDetailModifyClusterDTO extends AbstractOrderDetailData {
    private Long physicalClusterId;

    private String physicalClusterName;

    private Long logicalClusterId;

    private String logicalClusterName;

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

    @Override
    public String toString() {
        return "OrderDetailModifyClusterDTO{" +
                "physicalClusterId=" + physicalClusterId +
                ", physicalClusterName='" + physicalClusterName + '\'' +
                ", logicalClusterId=" + logicalClusterId +
                ", logicalClusterName='" + logicalClusterName + '\'' +
                '}';
    }
}