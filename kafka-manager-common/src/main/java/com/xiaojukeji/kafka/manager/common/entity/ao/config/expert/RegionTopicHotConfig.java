package com.xiaojukeji.kafka.manager.common.entity.ao.config.expert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/23
 */
public class RegionTopicHotConfig {
    private Long minTopicBytesInUnitB;

    private Integer maxDisPartitionNum;

    private List<Long> ignoreClusterIdList;

    public Long getMinTopicBytesInUnitB() {
        if (minTopicBytesInUnitB == null) {
            return 3 * 1024 * 1024L;
        }
        return minTopicBytesInUnitB;
    }

    public void setMinTopicBytesInUnitB(Long minTopicBytesInUnitB) {
        this.minTopicBytesInUnitB = minTopicBytesInUnitB;
    }

    public Integer getMaxDisPartitionNum() {
        if (maxDisPartitionNum == null) {
            return 3;
        }
        return maxDisPartitionNum;
    }

    public void setMaxDisPartitionNum(Integer maxDisPartitionNum) {
        this.maxDisPartitionNum = maxDisPartitionNum;
    }

    public List<Long> getIgnoreClusterIdList() {
        if (ignoreClusterIdList == null) {
            return new ArrayList<>();
        }
        return ignoreClusterIdList;
    }

    public void setIgnoreClusterIdList(List<Long> ignoreClusterIdList) {
        this.ignoreClusterIdList = ignoreClusterIdList;
    }

    @Override
    public String toString() {
        return "RegionTopicHotConfig{" +
                "minTopicBytesInUnitB=" + minTopicBytesInUnitB +
                ", maxDisPartitionNum=" + maxDisPartitionNum +
                ", ignoreClusterIdList=" + ignoreClusterIdList +
                '}';
    }
}