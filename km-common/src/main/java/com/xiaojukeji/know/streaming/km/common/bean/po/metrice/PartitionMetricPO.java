package com.xiaojukeji.know.streaming.km.common.bean.po.metrice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartitionMetricPO extends BaseMetricESPO{

    private String  topic;

    private Integer brokerId;

    private Integer partitionId;

    public PartitionMetricPO(Long clusterId, String topicName, Integer brokerId, Integer partitionId){
        super(clusterId);
        this.topic          = topicName;
        this.brokerId       = brokerId;
        this.partitionId    = partitionId;
    }

    @Override
    public String getKey() {
        return "P@" + clusterPhyId + "@" + topic + "@" + brokerId + "@" +partitionId + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return String.valueOf(partitionId);
    }
}
