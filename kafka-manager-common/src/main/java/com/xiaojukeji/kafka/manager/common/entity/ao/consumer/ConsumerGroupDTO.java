package com.xiaojukeji.kafka.manager.common.entity.ao.consumer;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetLocationEnum;

import java.util.List;
import java.util.Objects;

/**
 * 消费组信息
 * @author zengqiao
 * @date 19/4/18
 */
public class ConsumerGroupDTO {
    private Long clusterId;

    private String consumerGroup;

    private List<String> appIdList;

    private OffsetLocationEnum offsetStoreLocation;

    public ConsumerGroupDTO(Long clusterId,
                            String consumerGroup,
                            List<String> appIdList,
                            OffsetLocationEnum offsetStoreLocation) {
        this.clusterId = clusterId;
        this.consumerGroup = consumerGroup;
        this.appIdList = appIdList;
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

    public List<String> getAppIdList() {
        return appIdList;
    }

    public void setAppIdList(List<String> appIdList) {
        this.appIdList = appIdList;
    }

    public OffsetLocationEnum getOffsetStoreLocation() {
        return offsetStoreLocation;
    }

    public void setOffsetStoreLocation(OffsetLocationEnum offsetStoreLocation) {
        this.offsetStoreLocation = offsetStoreLocation;
    }

    @Override
    public String toString() {
        return "ConsumerGroupDTO{" +
                "clusterId=" + clusterId +
                ", consumerGroup='" + consumerGroup + '\'' +
                ", appIdList=" + appIdList +
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
        ConsumerGroupDTO that = (ConsumerGroupDTO) o;
        return clusterId.equals(that.clusterId)
                && consumerGroup.equals(that.consumerGroup)
                && offsetStoreLocation == that.offsetStoreLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, consumerGroup, offsetStoreLocation);
    }
}
