package com.xiaojukeji.kafka.manager.common.entity.ao.cluster;

/**
 * @author zengqiao
 * @date 20/6/29
 */
public class LogicalClusterMetrics {

    private Double totalProduceRequestsPerSec = 0.0;

    private Double bytesInPerSec = 0.0;

    private Double bytesOutPerSec = 0.0;

    private Double bytesRejectedPerSec = 0.0;

    private Double messagesInPerSec = 0.0;

    private Long gmtCreate;

    public Double getBytesInPerSec() {
        return bytesInPerSec;
    }

    public void setBytesInPerSec(Double bytesInPerSec) {
        this.bytesInPerSec = bytesInPerSec;
    }

    public Double getBytesOutPerSec() {
        return bytesOutPerSec;
    }

    public void setBytesOutPerSec(Double bytesOutPerSec) {
        this.bytesOutPerSec = bytesOutPerSec;
    }

    public Double getBytesRejectedPerSec() {
        return bytesRejectedPerSec;
    }

    public void setBytesRejectedPerSec(Double bytesRejectedPerSec) {
        this.bytesRejectedPerSec = bytesRejectedPerSec;
    }

    public Double getMessagesInPerSec() {
        return messagesInPerSec;
    }

    public void setMessagesInPerSec(Double messagesInPerSec) {
        this.messagesInPerSec = messagesInPerSec;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Double getTotalProduceRequestsPerSec() {
        return totalProduceRequestsPerSec;
    }

    public void setTotalProduceRequestsPerSec(Double totalProduceRequestsPerSec) {
        this.totalProduceRequestsPerSec = totalProduceRequestsPerSec;
    }

    @Override
    public String toString() {
        return "LogicalClusterMetrics{" +
                "totalProduceRequestsPerSec=" + totalProduceRequestsPerSec +
                ", bytesInPerSec=" + bytesInPerSec +
                ", bytesOutPerSec=" + bytesOutPerSec +
                ", bytesRejectedPerSec=" + bytesRejectedPerSec +
                ", messagesInPerSec=" + messagesInPerSec +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}