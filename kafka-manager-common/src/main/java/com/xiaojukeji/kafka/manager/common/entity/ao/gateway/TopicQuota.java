package com.xiaojukeji.kafka.manager.common.entity.ao.gateway;

import com.xiaojukeji.kafka.manager.common.entity.dto.gateway.TopicQuotaDTO;

/**
 * @author zhongyuankai
 * @date 2020/4/27
 */
public class TopicQuota {
    private String appId;

    private Long clusterId;

    private String topicName;

    private Long produceQuota;

    private Long consumeQuota;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
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
        return "TopicQuota{" +
                "appId='" + appId + '\'' +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", produceQuota=" + produceQuota +
                ", consumeQuota=" + consumeQuota +
                '}';
    }

    public static TopicQuota buildFrom(TopicQuotaDTO dto) {
        TopicQuota topicQuota = new TopicQuota();
        topicQuota.setAppId(dto.getAppId());
        topicQuota.setClusterId(dto.getClusterId());
        topicQuota.setTopicName(dto.getTopicName());
        topicQuota.setProduceQuota(dto.getProduceQuota());
        topicQuota.setConsumeQuota(dto.getConsumeQuota());
        return topicQuota;
    }

}
