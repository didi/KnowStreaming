package com.xiaojukeji.kafka.manager.common.entity.ao;

import lombok.Data;

import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/6/10
 */
@Data
public class RdTopicBasic {
    private Long clusterId;

    private String clusterName;

    private String topicName;

    private Long retentionTime;

    private String appId;

    private String appName;

    private Properties properties;

    private String description;

    private List<String> regionNameList;

    private Integer haRelation;

    @Override
    public String toString() {
        return "RdTopicBasic{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", retentionTime=" + retentionTime +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", properties=" + properties +
                ", description='" + description + '\'' +
                ", regionNameList=" + regionNameList +
                ", haRelation=" + haRelation +
                '}';
    }
}