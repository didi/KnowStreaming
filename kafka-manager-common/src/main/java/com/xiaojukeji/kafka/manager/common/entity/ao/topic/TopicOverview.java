package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

import lombok.Data;

/**
 * Topic概览信息
 * @author zengqiao
 * @date 20/5/14
 */
@Data
public class TopicOverview {
    private Long clusterId;

    private String topicName;

    private Integer replicaNum;

    private Integer partitionNum;

    private Long retentionTime;

    private Object byteIn;

    private Object byteOut;

    private Object produceRequest;

    private String appName;

    private String appId;

    private String description;

    private Long updateTime;

    private Long logicalClusterId;

    private Integer haRelation;

    @Override
    public String toString() {
        return "TopicOverview{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", retentionTime=" + retentionTime +
                ", byteIn=" + byteIn +
                ", byteOut=" + byteOut +
                ", produceRequest=" + produceRequest +
                ", appName='" + appName + '\'' +
                ", appId='" + appId + '\'' +
                ", description='" + description + '\'' +
                ", updateTime=" + updateTime +
                ", logicalClusterId=" + logicalClusterId +
                ", haRelation=" + haRelation +
                '}';
    }
}
