package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

/**
 * author: mrazkonglingxu
 * Date: 2020/12/7
 * Time: 7:40 下午
 */
public class TopicBrokerRequestTimeVO {

    private Long clusterId;

    private Integer brokerId;

    private TopicRequestTimeDetailVO brokerRequestTime;

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

    public TopicRequestTimeDetailVO getBrokerRequestTime() {
        return brokerRequestTime;
    }

    public void setBrokerRequestTime(TopicRequestTimeDetailVO brokerRequestTime) {
        this.brokerRequestTime = brokerRequestTime;
    }
}
