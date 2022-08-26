package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplicationMetrics extends BaseMetrics{
    private String      topic;

    private Integer     partitionId;

    private Integer     brokerId;

    public ReplicationMetrics(Long clusterId, String topicName, Integer brokerId, Integer partitionId){
        super(clusterId);
        this.topic          = topicName;
        this.brokerId       = brokerId;
        this.partitionId    = partitionId;
    }

    @Override
    public String unique() {
        return "R@" + clusterPhyId + "@" + topic + "@" + brokerId + "@" +partitionId;
    }

}
