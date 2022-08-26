package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/6/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClusterMetrics extends BaseMetrics {
    private String kafkaVersion;

    public ClusterMetrics(Long clusterPhyId){
        super(clusterPhyId);
    }

    public ClusterMetrics(Long clusterPhyId, String kafkaVersion){
        super(clusterPhyId);
        this.kafkaVersion = kafkaVersion;
    }

    public static ClusterMetrics initWithMetrics(Long clusterId, String metric, Float value){
        ClusterMetrics clusterMetrics = new ClusterMetrics();
        clusterMetrics.setClusterPhyId(clusterId);
        clusterMetrics.putMetric(metric, value);

        return clusterMetrics;
    }

    public static ClusterMetrics initWithMetrics(Long clusterId, String metric, int value){
        ClusterMetrics clusterMetrics = new ClusterMetrics();
        clusterMetrics.setClusterPhyId(clusterId);
        clusterMetrics.putMetric(metric, (float)value);

        return clusterMetrics;
    }

    @Override
    public String unique() {
        return "C@" + clusterPhyId;
    }
}