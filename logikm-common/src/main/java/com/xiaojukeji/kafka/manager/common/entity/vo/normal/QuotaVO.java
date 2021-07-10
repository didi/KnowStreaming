package com.xiaojukeji.kafka.manager.common.entity.vo.normal;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class QuotaVO {
    private Long clusterId;

    private String topicName;

    private String appId;

    private Long produceQuota;

    private Long consumeQuota;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getProduceQuota() {
        return produceQuota;
    }

    public void setProduceQuota(Long produceQuota) {
        this.produceQuota = produceQuota;
    }

    public Long getConsumeQuota() {
        return consumeQuota;
    }

    public void setConsumeQuota(Long consumeQuota) {
        this.consumeQuota = consumeQuota;
    }

    @Override
    public String toString() {
        return "QuotaVO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", produceQuota=" + produceQuota +
                ", consumeQuota=" + consumeQuota +
                '}';
    }
}