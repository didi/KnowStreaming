package com.xiaojukeji.kafka.manager.common.entity.ao.config.expert;

import java.util.ArrayList;
import java.util.List;

/**
 * 专家服务-Topic分区不足配置
 * @author zengqiao
 * @date 20/8/23
 */
public class TopicInsufficientPartitionConfig {
    private Long maxBytesInPerPartitionUnitB = 3 * 1024 * 1024L;

    private Long minTopicBytesInUnitB = 3 * 1024 * 1024L;

    private List<Long> ignoreClusterIdList = new ArrayList<>();

    public Long getMaxBytesInPerPartitionUnitB() {
        return maxBytesInPerPartitionUnitB;
    }

    public void setMaxBytesInPerPartitionUnitB(Long maxBytesInPerPartitionUnitB) {
        this.maxBytesInPerPartitionUnitB = maxBytesInPerPartitionUnitB;
    }

    public Long getMinTopicBytesInUnitB() {
        return minTopicBytesInUnitB;
    }

    public void setMinTopicBytesInUnitB(Long minTopicBytesInUnitB) {
        this.minTopicBytesInUnitB = minTopicBytesInUnitB;
    }

    public List<Long> getIgnoreClusterIdList() {
        return ignoreClusterIdList;
    }

    public void setIgnoreClusterIdList(List<Long> ignoreClusterIdList) {
        this.ignoreClusterIdList = ignoreClusterIdList;
    }

    @Override
    public String toString() {
        return "TopicInsufficientPartitionConfig{" +
                "maxBytesInPerPartitionUnitB=" + maxBytesInPerPartitionUnitB +
                ", minTopicBytesInUnitB=" + minTopicBytesInUnitB +
                ", ignoreClusterIdList=" + ignoreClusterIdList +
                '}';
    }
}