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
public class ReplicationMetricPO extends BaseMetricESPO{

    private String  topic;

    private Integer brokerId;

    private Integer partitionId;

    public ReplicationMetricPO(Long clusterId, String topicName, Integer brokerId, Integer partitionId){
        super(clusterId);
        this.topic          = topicName;
        this.brokerId       = brokerId;
        this.partitionId    = partitionId;
    }

    @Override
    public String getKey() {
        return "R@" + clusterPhyId + "@" + topic + "@" + brokerId + "@" +partitionId + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return String.valueOf(partitionId);
    }
}
