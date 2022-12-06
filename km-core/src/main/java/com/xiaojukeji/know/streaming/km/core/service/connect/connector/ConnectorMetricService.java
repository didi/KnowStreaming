package com.xiaojukeji.know.streaming.km.core.service.connect.connector;

import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.ClusterConnectorDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.connect.MetricsConnectorsDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.enums.connect.ConnectorTypeEnum;

import java.util.List;

/**
 * @author didi
 */
public interface ConnectorMetricService {

    /**
     * 从Kafka获取指标
     */
    Result<ConnectorMetrics> collectConnectClusterMetricsFromKafkaWithCacheFirst(Long connectClusterPhyId, String connectorName, String metricName);

    Result<ConnectorMetrics> collectConnectClusterMetricsFromKafka(Long connectClusterPhyId, String connectorName, String metricName);

    Result<ConnectorMetrics> collectConnectClusterMetricsFromKafka(Long connectClusterPhyId, String connectorName, String metricName, ConnectorTypeEnum connectorType);

    /**
     * 从ES中获取一段时间内聚合计算之后的指标线
     */
    Result<List<MetricMultiLinesVO>> listConnectClusterMetricsFromES(Long clusterPhyId, MetricsConnectorsDTO dto);

    Result<List<ConnectorMetrics>> getLatestMetricsFromES(Long clusterPhyId, List<ClusterConnectorDTO> connectorNameList, List<String> metricNameList);

    Result<ConnectorMetrics> getLatestMetricsFromES(Long connectClusterId, String connectorName, List<String> metricsNames);

    boolean isMetricName(String str);
}
