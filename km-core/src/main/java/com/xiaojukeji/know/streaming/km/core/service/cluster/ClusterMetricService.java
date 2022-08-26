package com.xiaojukeji.know.streaming.km.core.service.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsClusterPhyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.*;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;

import java.util.List;

/**
 * @author didi
 */
public interface ClusterMetricService {

    /**
     * 从Kafka获取指标
     */
    Result<ClusterMetrics> collectClusterMetricsFromKafka(Long clusterId, String metric);
    Result<ClusterMetrics> collectClusterMetricsFromKafka(Long clusterId, List<String> metrics);

    /**
     * listClusterMetricsFromES
     * @param dto
     * @return
     */
    Result<List<MetricMultiLinesVO>> listClusterMetricsFromES(MetricsClusterPhyDTO dto);

    /**
     * 获取多个存储在ES中的指标数据，最新的一条即可
     */
    Result<ClusterMetrics> getLatestMetricsFromES(Long clusterPhyId, List<String> metricNameList);

    /**
     * 优先从本地缓存获取metrics信息
     */
    ClusterMetrics getLatestMetricsFromCache(Long clusterPhyId);

    Result<List<MetricPointVO>> getMetricPointsFromES(Long clusterId, MetricDTO metricDTO);


    PaginationResult<ClusterMetrics> pagingClusterWithLatestMetricsFromES(List<Long> clusterIds,
                                                                          List<SearchTerm> searchMatches,
                                                                          List<SearchShould> shoulds,
                                                                          SearchSort sort, SearchRange range, SearchPage page);
}
