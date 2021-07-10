package com.xiaojukeji.kafka.manager.common.entity.vo.rd.broker;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/12/29
 */
public class AnalysisBrokerVO {
    private Long clusterId;

    private Integer brokerId;

    private Long baseTime;

    private Double bytesIn;

    private Double bytesOut;

    private Double messagesIn;

    private Double totalFetchRequests;

    private Double totalProduceRequests;

    List<AnalysisTopicVO> topicAnalysisVOList;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public Long getBaseTime() {
        return baseTime;
    }

    public void setBaseTime(Long baseTime) {
        this.baseTime = baseTime;
    }

    public Double getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(Double bytesIn) {
        this.bytesIn = bytesIn;
    }

    public Double getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(Double bytesOut) {
        this.bytesOut = bytesOut;
    }

    public Double getMessagesIn() {
        return messagesIn;
    }

    public void setMessagesIn(Double messagesIn) {
        this.messagesIn = messagesIn;
    }

    public Double getTotalFetchRequests() {
        return totalFetchRequests;
    }

    public void setTotalFetchRequests(Double totalFetchRequests) {
        this.totalFetchRequests = totalFetchRequests;
    }

    public Double getTotalProduceRequests() {
        return totalProduceRequests;
    }

    public void setTotalProduceRequests(Double totalProduceRequests) {
        this.totalProduceRequests = totalProduceRequests;
    }

    public List<AnalysisTopicVO> getTopicAnalysisVOList() {
        return topicAnalysisVOList;
    }

    public void setTopicAnalysisVOList(List<AnalysisTopicVO> topicAnalysisVOList) {
        this.topicAnalysisVOList = topicAnalysisVOList;
    }

    @Override
    public String toString() {
        return "AnalysisBrokerVO{" +
                "clusterId=" + clusterId +
                ", brokerId=" + brokerId +
                ", baseTime=" + baseTime +
                ", bytesIn=" + bytesIn +
                ", bytesOut=" + bytesOut +
                ", messagesIn=" + messagesIn +
                ", totalFetchRequests=" + totalFetchRequests +
                ", totalProduceRequests=" + totalProduceRequests +
                ", topicAnalysisVOList=" + topicAnalysisVOList +
                '}';
    }
}