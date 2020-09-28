package com.xiaojukeji.kafka.manager.common.entity.ao.analysis;

/**
 * @author zengqiao
 * @date 19/12/29
 */
public class AnalysisTopicDTO {
    private String topicName;

    private Double bytesIn;

    private Double bytesInRate;

    private Double bytesOut;

    private Double bytesOutRate;

    private Double messagesIn;

    private Double messagesInRate;

    private Double totalFetchRequests;

    private Double totalFetchRequestsRate;

    private Double totalProduceRequests;

    private Double totalProduceRequestsRate;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Double getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(Double bytesIn) {
        this.bytesIn = bytesIn;
    }

    public Double getBytesInRate() {
        return bytesInRate;
    }

    public void setBytesInRate(Double bytesInRate) {
        this.bytesInRate = bytesInRate;
    }

    public Double getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(Double bytesOut) {
        this.bytesOut = bytesOut;
    }

    public Double getBytesOutRate() {
        return bytesOutRate;
    }

    public void setBytesOutRate(Double bytesOutRate) {
        this.bytesOutRate = bytesOutRate;
    }

    public Double getMessagesIn() {
        return messagesIn;
    }

    public void setMessagesIn(Double messagesIn) {
        this.messagesIn = messagesIn;
    }

    public Double getMessagesInRate() {
        return messagesInRate;
    }

    public void setMessagesInRate(Double messagesInRate) {
        this.messagesInRate = messagesInRate;
    }

    public Double getTotalFetchRequests() {
        return totalFetchRequests;
    }

    public void setTotalFetchRequests(Double totalFetchRequests) {
        this.totalFetchRequests = totalFetchRequests;
    }

    public Double getTotalFetchRequestsRate() {
        return totalFetchRequestsRate;
    }

    public void setTotalFetchRequestsRate(Double totalFetchRequestsRate) {
        this.totalFetchRequestsRate = totalFetchRequestsRate;
    }

    public Double getTotalProduceRequests() {
        return totalProduceRequests;
    }

    public void setTotalProduceRequests(Double totalProduceRequests) {
        this.totalProduceRequests = totalProduceRequests;
    }

    public Double getTotalProduceRequestsRate() {
        return totalProduceRequestsRate;
    }

    public void setTotalProduceRequestsRate(Double totalProduceRequestsRate) {
        this.totalProduceRequestsRate = totalProduceRequestsRate;
    }

    @Override
    public String toString() {
        return "AnalysisTopicDTO{" +
                "topicName='" + topicName + '\'' +
                ", bytesIn=" + bytesIn +
                ", bytesInRate=" + bytesInRate +
                ", bytesOut=" + bytesOut +
                ", bytesOutRate=" + bytesOutRate +
                ", messagesIn=" + messagesIn +
                ", messagesInRate=" + messagesInRate +
                ", totalFetchRequests=" + totalFetchRequests +
                ", totalFetchRequestsRate=" + totalFetchRequestsRate +
                ", totalProduceRequests=" + totalProduceRequests +
                ", totalProduceRequestsRate=" + totalProduceRequestsRate +
                '}';
    }
}