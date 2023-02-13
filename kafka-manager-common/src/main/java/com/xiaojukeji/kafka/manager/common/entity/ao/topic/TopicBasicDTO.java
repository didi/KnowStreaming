package com.xiaojukeji.kafka.manager.common.entity.ao.topic;

import lombok.Data;

import java.util.List;

/**
 * @author arthur
 * @date 2018/09/03
 */
@Data
public class TopicBasicDTO {
    private Long clusterId;

    private String appId;

    private String appName;

    private String principals;

    private String topicName;

    private String description;

    private List<String> regionNameList;

    private Integer score;

    private String topicCodeC;

    private Integer partitionNum;

    private Integer replicaNum;

    private Integer brokerNum;

    private Long modifyTime;

    private Long createTime;

    private Long retentionTime;

    private Long retentionBytes;

    private Integer haRelation;

    @Override
    public String toString() {
        return "TopicBasicDTO{" +
                "clusterId=" + clusterId +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", principals='" + principals + '\'' +
                ", topicName='" + topicName + '\'' +
                ", description='" + description + '\'' +
                ", regionNameList=" + regionNameList +
                ", score=" + score +
                ", topicCodeC='" + topicCodeC + '\'' +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", brokerNum=" + brokerNum +
                ", modifyTime=" + modifyTime +
                ", createTime=" + createTime +
                ", retentionTime=" + retentionTime +
                ", retentionBytes=" + retentionBytes +
                ", haRelation=" + haRelation +
                '}';
    }
}
