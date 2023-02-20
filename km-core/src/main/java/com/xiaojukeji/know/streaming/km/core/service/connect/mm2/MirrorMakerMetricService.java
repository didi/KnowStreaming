package com.xiaojukeji.know.streaming.km.core.service.connect.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.mm2.MetricsMirrorMakersDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.mm2.MirrorMakerTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;

import java.util.List;

/**
 * @author wyb
 * @date 2022/12/15
 */
public interface MirrorMakerMetricService {

    Result<MirrorMakerMetrics> collectMirrorMakerMetricsFromKafka(Long connectClusterPhyId, String mirrorMakerName, List<MirrorMakerTopic> mirrorMakerTopicList, String metricName);

    /**
     * 从ES中获取一段时间内聚合计算之后的指标线
     */
    Result<List<MetricMultiLinesVO>> listMirrorMakerClusterMetricsFromES(Long clusterPhyId, MetricsMirrorMakersDTO dto);

    Result<List<MirrorMakerMetrics>> getLatestMetricsFromES(Long clusterPhyId, List<Tuple<Long, String>> mirrorMakerList, List<String> metricNameList);

    Result<MirrorMakerMetrics> getLatestMetricsFromES(Long connectClusterId, String connectorName, List<String> metricsNames);
}
