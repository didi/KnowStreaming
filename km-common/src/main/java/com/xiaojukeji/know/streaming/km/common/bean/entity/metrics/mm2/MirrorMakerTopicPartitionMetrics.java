package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BaseMetrics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wyb
 * @date 2022/12/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MirrorMakerTopicPartitionMetrics extends BaseMetrics {
    private Long connectClusterId;

    private String mirrorMakerName;

    private String clusterAlias;

    private String topicName;

    private Integer partitionId;

    private String workerId;

    @Override
    public String unique() {
        return "KCOR@" + connectClusterId + "@" + mirrorMakerName + "@" + clusterAlias + "@" + workerId + "@" + topicName + "@" + partitionId;
    }

    public static MirrorMakerTopicPartitionMetrics initWithMetric(Long connectClusterId, String mirrorMakerName, String clusterAlias, String topicName, Integer partitionId, String workerId, String metricName, Float value) {
        MirrorMakerTopicPartitionMetrics metrics = new MirrorMakerTopicPartitionMetrics(connectClusterId, mirrorMakerName, clusterAlias, topicName, partitionId, workerId);
        metrics.putMetric(metricName, value);
        return metrics;
    }
}
