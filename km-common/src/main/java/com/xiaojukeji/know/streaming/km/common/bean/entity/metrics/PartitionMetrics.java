package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/6/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PartitionMetrics extends BaseMetrics {
    private String      topic;

    private Integer     partitionId;

    private Integer     brokerId;

    public PartitionMetrics(Long clusterId, String topicName, Integer brokerId, Integer partitionId) {
        super(clusterId);
        this.topic          = topicName;
        this.brokerId       = brokerId;
        this.partitionId    = partitionId;
    }

    @Override
    public String unique() {
        return "P@" + clusterPhyId + "@" + topic + "@" + brokerId + "@" +partitionId;
    }
}