package com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMetricParam extends MetricParam {

    private Long        clusterPhyId;

    private String      groupName;

    private String      topicName;

    private Long        partitionId;

    private String      metric;

    public GroupMetricParam(Long clusterPhyId, String groupName, String metric){
        this.clusterPhyId   = clusterPhyId;
        this.groupName      = groupName;
        this.metric         = metric;
    }
}
