package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/3/29
 */
public class TopicStatisticsDO {
    private Long id;

    private Date gmtCreate;

    private Long clusterId;

    private String topicName;

    private Long offsetSum;

    private Double maxAvgBytesIn;

    private String gmtDay;

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

    public Long getOffsetSum() {
        return offsetSum;
    }

    public void setOffsetSum(Long offsetSum) {
        this.offsetSum = offsetSum;
    }

    public Double getMaxAvgBytesIn() {
        return maxAvgBytesIn;
    }

    public void setMaxAvgBytesIn(Double maxAvgBytesIn) {
        this.maxAvgBytesIn = maxAvgBytesIn;
    }

    public String getGmtDay() {
        return gmtDay;
    }

    public void setGmtDay(String gmtDay) {
        this.gmtDay = gmtDay;
    }

    @Override
    public String toString() {
        return "TopicStatisticsDO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", offsetSum=" + offsetSum +
                ", maxAvgBytesIn=" + maxAvgBytesIn +
                ", gmtDay='" + gmtDay + '\'' +
                '}';
    }
}