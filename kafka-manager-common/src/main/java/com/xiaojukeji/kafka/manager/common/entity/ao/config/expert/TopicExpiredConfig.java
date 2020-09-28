package com.xiaojukeji.kafka.manager.common.entity.ao.config.expert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/9/17
 */
public class TopicExpiredConfig {
    private Integer minExpiredDay = 30;

    private List<Long> ignoreClusterIdList = new ArrayList<>();

    public Integer getMinExpiredDay() {
        return minExpiredDay;
    }

    public void setMinExpiredDay(Integer minExpiredDay) {
        this.minExpiredDay = minExpiredDay;
    }

    public List<Long> getIgnoreClusterIdList() {
        return ignoreClusterIdList;
    }

    public void setIgnoreClusterIdList(List<Long> ignoreClusterIdList) {
        this.ignoreClusterIdList = ignoreClusterIdList;
    }

    @Override
    public String toString() {
        return "TopicExpiredConfig{" +
                "minExpiredDay=" + minExpiredDay +
                ", ignoreClusterIdList=" + ignoreClusterIdList +
                '}';
    }
}