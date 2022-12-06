package com.xiaojukeji.know.streaming.km.core.service.connect.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.connect.MetricsConnectClustersDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;

import java.util.List;

/**
 * @author didi
 */
public interface ConnectClusterMetricService {

    /**
     * 从Kafka获取指标
     */
    Result<ConnectClusterMetrics> collectConnectClusterMetricsFromKafkaWithCacheFirst(Long connectClusterPhyId, String metricName);
    Result<ConnectClusterMetrics> collectConnectClusterMetricsFromKafka(Long connectClusterPhyId, String metricName);

    /**
     * 从ES中获取一段时间内聚合计算之后的指标线
     */
    Result<List<MetricMultiLinesVO>> listConnectClusterMetricsFromES(Long clusterPhyId, MetricsConnectClustersDTO dto);

    boolean isMetricName(String str);
}
