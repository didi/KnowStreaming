package com.xiaojukeji.know.streaming.km.core.service.group;

import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricGroupPartitionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.GroupTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.GroupMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;

import java.util.List;

public interface GroupMetricService {
    /**
     * 从Kafka查询指标
     * @param clusterId 集群ID
     * @param metric
     * @return
     */
    Result<List<GroupMetrics>> collectGroupMetricsFromKafka(Long clusterId, String groupName, String metric);
    Result<List<GroupMetrics>> collectGroupMetricsFromKafka(Long clusterId, String groupName, List<String> metrics);

    /**
     * 从ES查询指标
     * @param clusterId 集群ID
     * @param dto 查询dto
     * @return
     */
    Result<List<MetricMultiLinesVO>> listGroupMetricsFromES(Long clusterId, MetricGroupPartitionDTO dto);

    /**
     * 获取该 Group&Topic 的最近指标
     * @param clusterPhyId
     * @param groupName
     * @param topicName
     * @param metricNames
     * @return
     */
    Result<List<GroupMetrics>> listPartitionLatestMetricsFromES(Long clusterPhyId, String groupName, String topicName, List<String> metricNames);

    /**
     * 获取 按照Group&Topic维度进行聚合 后的指标
     * @param clusterPhyId 物理集群ID
     * @param groupTopicList GroupTopic列表
     * @param metricNames 指标列表
     * @param aggType 聚合类型：avg、max、min、sum
     * @return
     */
    Result<List<GroupMetrics>> listLatestMetricsAggByGroupTopicFromES(Long clusterPhyId, List<GroupTopic> groupTopicList, List<String> metricNames, AggTypeEnum aggType);

    /**
     * 统计一段时间内，某个指标出现某个值的次数
     * @param clusterPhyId 集群ID
     * @param groupName 消费组名称
     * @param term 指标
     * @param startTime 起始时间
     * @param endTime 结束时间
     * @return
     */
    Result<Integer> countMetricValueOccurrencesFromES(Long clusterPhyId, String groupName, SearchTerm term, Long startTime, Long endTime);
}
