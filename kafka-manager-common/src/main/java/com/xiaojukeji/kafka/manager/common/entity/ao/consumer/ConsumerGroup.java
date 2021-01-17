package com.xiaojukeji.kafka.manager.common.entity.ao.consumer;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;

import java.util.Objects;

public class ConsumerGroup {
    private Long clusterId;

    private String consumerGroup;

    private OffsetLocationEnum offsetStoreLocation;

    public ConsumerGroup(Long clusterId, String consumerGroup, OffsetLocationEnum offsetStoreLocation) {
        this.clusterId = clusterId;
        this.consumerGroup = consumerGroup;
        this.offsetStoreLocation = offsetStoreLocation;
    }

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

    @Override
    public String toString() {
        return "ConsumerGroup{" +
                "clusterId=" + clusterId +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", offsetStoreLocation=" + offsetStoreLocation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsumerGroup that = (ConsumerGroup) o;
        return clusterId.equals(that.clusterId)
                && consumerGroup.equals(that.consumerGroup)
                && offsetStoreLocation == that.offsetStoreLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, consumerGroup, offsetStoreLocation);
    }
}
