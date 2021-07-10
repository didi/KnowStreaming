package com.xiaojukeji.kafka.manager.common.entity.ao.consumer;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;

import java.util.List;

public class ConsumerGroupSummary {
    private Long clusterId;

    private String consumerGroup;

    private OffsetLocationEnum offsetStoreLocation;

    private List<String> appIdList;

    private String state;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public OffsetLocationEnum getOffsetStoreLocation() {
        return offsetStoreLocation;
    }

    public void setOffsetStoreLocation(OffsetLocationEnum offsetStoreLocation) {
        this.offsetStoreLocation = offsetStoreLocation;
    }

    public List<String> getAppIdList() {
        return appIdList;
    }

    public void setAppIdList(List<String> appIdList) {
        this.appIdList = appIdList;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ConsumerGroupSummary{" +
                "clusterId=" + clusterId +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", offsetStoreLocation=" + offsetStoreLocation +
                ", appIdList=" + appIdList +
                ", state='" + state + '\'' +
                '}';
    }
}
