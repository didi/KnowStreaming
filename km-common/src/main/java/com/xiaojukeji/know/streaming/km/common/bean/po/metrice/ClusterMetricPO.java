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
public class ClusterMetricPO extends BaseMetricESPO {

    private String kafkaVersion;

    public ClusterMetricPO(Long clusterPhyId){
        super(clusterPhyId);
    }

    public ClusterMetricPO(Long clusterPhyId, String kafkaVersion){
        super(clusterPhyId);
        this.kafkaVersion = kafkaVersion;
    }

    @Override
    public String getKey() {
        return "C@" + clusterPhyId + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return String.valueOf( clusterPhyId );
    }
}
