package com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker;

/**
 * @author zengqiao
 * @date 20/3/11
 */
public class AnalysisTopicVO {
    private String topicName;

    private String bytesIn;

    private String bytesInRate;

    private String bytesOut;

    private String bytesOutRate;

    private String messagesIn;

    private String messagesInRate;

    private String totalFetchRequests;

    private String totalFetchRequestsRate;

    private String totalProduceRequests;

    private String totalProduceRequestsRate;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(String bytesIn) {
        this.bytesIn = bytesIn;
    }

    public String getBytesInRate() {
        return bytesInRate;
    }

    public void setBytesInRate(String bytesInRate) {
        this.bytesInRate = bytesInRate;
    }

    public String getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(String bytesOut) {
        this.bytesOut = bytesOut;
    }

    public String getBytesOutRate() {
        return bytesOutRate;
    }

    public void setBytesOutRate(String bytesOutRate) {
        this.bytesOutRate = bytesOutRate;
    }

    public String getMessagesIn() {
        return messagesIn;
    }

    public void setMessagesIn(String messagesIn) {
        this.messagesIn = messagesIn;
    }

    public String getMessagesInRate() {
        return messagesInRate;
    }

    public void setMessagesInRate(String messagesInRate) {
        this.messagesInRate = messagesInRate;
    }

    public String getTotalFetchRequests() {
        return totalFetchRequests;
    }

    public void setTotalFetchRequests(String totalFetchRequests) {
        this.totalFetchRequests = totalFetchRequests;
    }

    public String getTotalFetchRequestsRate() {
        return totalFetchRequestsRate;
    }

    public void setTotalFetchRequestsRate(String totalFetchRequestsRate) {
        this.totalFetchRequestsRate = totalFetchRequestsRate;
    }

    public String getTotalProduceRequests() {
        return totalProduceRequests;
    }

    public void setTotalProduceRequests(String totalProduceRequests) {
        this.totalProduceRequests = totalProduceRequests;
    }

    public String getTotalProduceRequestsRate() {
        return totalProduceRequestsRate;
    }

    public void setTotalProduceRequestsRate(String totalProduceRequestsRate) {
        this.totalProduceRequestsRate = totalProduceRequestsRate;
    }

    @Override
    public String toString() {
        return "AnalysisTopicVO{" +
                "topicName='" + topicName + '\'' +
                ", bytesIn='" + bytesIn + '\'' +
                ", bytesInRate='" + bytesInRate + '\'' +
                ", bytesOut='" + bytesOut + '\'' +
                ", bytesOutRate='" + bytesOutRate + '\'' +
                ", messagesIn='" + messagesIn + '\'' +
                ", messagesInRate='" + messagesInRate + '\'' +
                ", totalFetchRequests='" + totalFetchRequests + '\'' +
                ", totalFetchRequestsRate='" + totalFetchRequestsRate + '\'' +
                ", totalProduceRequests='" + totalProduceRequests + '\'' +
                ", totalProduceRequestsRate='" + totalProduceRequestsRate + '\'' +
                '}';
    }
}