package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;

/**
 * @author zengqiao
 * @date 20/9/2
 */
public class TopicExpiredData {
    private Long clusterId;

    private String topicName;

    private LogicalClusterDO logicalClusterDO;

    private AppDO appDO;

    private Integer fetchConnectionNum;

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

    public LogicalClusterDO getLogicalClusterDO() {
        return logicalClusterDO;
    }

    public void setLogicalClusterDO(LogicalClusterDO logicalClusterDO) {
        this.logicalClusterDO = logicalClusterDO;
    }

    public AppDO getAppDO() {
        return appDO;
    }

    public void setAppDO(AppDO appDO) {
        this.appDO = appDO;
    }

    public Integer getFetchConnectionNum() {
        return fetchConnectionNum;
    }

    public void setFetchConnectionNum(Integer fetchConnectionNum) {
        this.fetchConnectionNum = fetchConnectionNum;
    }

    @Override
    public String toString() {
        return "TopicExpiredData{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", logicalClusterDO=" + logicalClusterDO +
                ", appDO=" + appDO +
                ", fetchConnectionNum=" + fetchConnectionNum +
                '}';
    }
}