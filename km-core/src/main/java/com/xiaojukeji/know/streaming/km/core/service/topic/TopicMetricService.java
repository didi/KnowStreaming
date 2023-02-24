package com.xiaojukeji.know.streaming.km.core.service.topic;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricsTopicDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.*;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;

import java.util.List;
import java.util.Map;

public interface TopicMetricService {
    /**
     * 从 kafka 中获取相应的指标
     */
    Result<List<TopicMetrics>> collectTopicMetricsFromKafka(Long clusterPhyId, String topicName, String metricName);
    Result<List<TopicMetrics>> collectTopicMetricsFromKafkaWithCacheFirst(Long clusterPhyId, String topicName, String metricName);

    /**
     * 优先从本地缓存获取metrics信息
     */
    Map<String, TopicMetrics> getLatestMetricsFromCache(Long clusterPhyId);

    /**
     * 获取Topic在具体Broker上最新的一个指标
     * @param clusterPhyId 物理集群ID
     * @param brokerId brokerId
     * @param topicName Topic名称
     * @param metricNameList 需获取指标列表
     * @return
     */
    TopicMetrics getTopicLatestMetricsFromES(Long clusterPhyId, Integer brokerId, String topicName, List<String> metricNameList);

    /**
     * 获取Topic维度最新的一条指标
     */
    List<TopicMetrics> listTopicLatestMetricsFromES(Long clusterPhyId, List<String> topicNames, List<String> metricNameList);

    /**
     * 获取Topic维度最新的一条指标
     * @param clusterPhyId
     * @param topicName
     * @param metricNameList
     * @return
     */
    TopicMetrics getTopicLatestMetricsFromES(Long clusterPhyId, String topicName, List<String> metricNameList);

    /**
     * 从 es 中获取聚合计算之后的指标用于页面展示
     * @param dto
     * @return
     */
    Result<List<MetricMultiLinesVO>> listTopicMetricsFromES(Long clusterId, MetricsTopicDTO dto);

    /**
     *
     * @param clusterId
     * @param topicName
     * @param dto
     * @return
     */
    Result<List<MetricPointVO>> getMetricPointsFromES(Long clusterId, String topicName, MetricDTO dto);

    /**
     *
     * @param clusterPhyId
     * @param topics
     * @param metric
     * @param max
     * @param startTime
     * @param endTime
     * @return
     */
    Result<List<TopicMetrics>> listTopicMaxMinMetrics(Long clusterPhyId, List<String> topics, String metric, boolean max, Long startTime, Long endTime);

    /**
     * 获取聚合指标点
     * @param clusterPhyId
     * @param topicNames
     * @param metricName
     * @param aggTypeEnum
     * @param startTime
     * @param endTime
     * @return
     */
    Result<Map<String, MetricPointVO>> getAggMetricPointFromES(Long clusterPhyId,
                                                               List<String> topicNames,
                                                               String metricName,
                                                               AggTypeEnum aggTypeEnum,
                                                               Long startTime,
                                                               Long endTime);

    /**
     *
     * @param clusterId
     * @param topicName
     * @param dto
     * @return
     */
    Result<Map<String, List<MetricPointVO>>> getMetricPointsFromES(Long clusterId, List<String> topicName, MetricDTO dto);

    /**
     *
     * @param clusterId
     * @param metricNameList
     * @param sort
     * @param fuzzy
     * @param searchTermList
     * @param page
     * @return
     */
    PaginationResult<TopicMetrics> pagingTopicWithLatestMetricsFromES(Long clusterId, List<String> metricNameList,
                                                                      SearchSort sort,
                                                                      SearchFuzzy fuzzy,
                                                                      List<SearchShould> shoulds,
                                                                      List<SearchTerm> searchTermList,
                                                                      SearchPage page);

    /**
     * 统计一段时间内，某个指标出现某个值的次数
     * @param clusterPhyId 集群ID
     * @param topicName 消费组名称
     * @param searchMatch 指标
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @return
     */
    Result<Integer> countMetricValueOccurrencesFromES(Long clusterPhyId, String topicName, SearchTerm searchMatch, Long startTime, Long endTime);
}
