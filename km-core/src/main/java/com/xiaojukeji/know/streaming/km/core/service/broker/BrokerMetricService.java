package com.xiaojukeji.know.streaming.km.core.service.broker;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsBrokerDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;

import java.util.List;

/**
 * @author didi
 */
public interface BrokerMetricService {

    /**
     * 从Kafka获取指标
     */
    Result<BrokerMetrics> collectBrokerMetricsFromKafkaWithCacheFirst(Long clusterPhyId, Integer brokerId, String metricName);
    Result<BrokerMetrics> collectBrokerMetricsFromKafka(Long clusterPhyId, Integer brokerId, String metricName);
    Result<BrokerMetrics> collectBrokerMetricsFromKafka(Long clusterPhyId, Integer brokerId, List<String> metricNames);

    /**
     * 从ES中获取一段时间内聚合计算之后的指标线
     */
    Result<List<MetricMultiLinesVO>> listBrokerMetricsFromES(Long clusterId, MetricsBrokerDTO dto);

    /**
     * 从ES中获取聚合计算之后的一个指标点
     */
    Result<List<MetricPointVO>> getMetricPointsFromES(Long clusterPhyId, Integer brokerId, MetricDTO dto);

    /**
     * 从ES中获取最近的一个指标
     */
    Result<List<BrokerMetrics>> getLatestMetricsFromES(Long clusterPhyId, List<Integer> brokerIdList);

    /**
     * 从ES中获取最近的一个指标
     */
    Result<BrokerMetrics> getLatestMetricsFromES(Long clusterPhyId, Integer brokerId);

    boolean isMetricName(String str);
}
