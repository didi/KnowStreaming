package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.GroupTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicPartitionKS;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.GroupMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.MetricsUtils;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.Constant.ZERO;
import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;
import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.GROUP_INDEX;

@Component
public class GroupMetricESDAO extends BaseMetricESDAO {

    @PostConstruct
    public void init() {
        super.indexName = GROUP_INDEX;
        checkCurrentDayIndexExist();
        register(this);
    }

    public List<GroupMetricPO> listLatestMetricsAggByGroupTopic(Long clusterPhyId, List<GroupTopic> groupTopicList, List<String> metrics, AggTypeEnum aggType){
        Long latestTime = getLatestMetricTime();
        Long startTime  = latestTime - FIVE_MIN;

        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, latestTime);

        //2、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType.getAggType());

        List<GroupMetricPO> groupMetricPOS = new CopyOnWriteArrayList<>();
        for(GroupTopic groupTopic : groupTopicList){
            esTPService.submitSearchTask(
                    String.format("class=GroupMetricESDAO||method=listLatestMetricsAggByGroupTopic||ClusterPhyId=%d||groupName=%s||topicName=%s",
                            clusterPhyId, groupTopic.getGroupName(), groupTopic.getTopicName()),
                    5000,
                    () -> {
                        String group = groupTopic.getGroupName();
                        String topic = groupTopic.getTopicName();
                        try {
                            String dsl = dslLoaderUtil.getFormatDslByFileName(
                                    DslConstant.LIST_GROUP_LATEST_METRICS_BY_GROUP_TOPIC, clusterPhyId, group, topic,
                                    startTime, latestTime, aggDsl);

                            String routing = routing(clusterPhyId, group);

                            GroupMetricPO groupMetricPO = esOpClient.performRequestWithRouting(routing, realIndex, dsl,
                                    s -> handleGroupMetricESQueryResponse(s, metrics, clusterPhyId, group, topic), 3);
                            groupMetricPOS.add(groupMetricPO);
                        }catch (Exception e){
                            LOGGER.error("method=listLatestMetricsAggByGroupTopic||clusterPhyId={}||group{}||topic={}||errMsg=exception!",
                                    clusterPhyId, group, topic, e);
                        }
                    });
        }

        esTPService.waitExecute();
        return groupMetricPOS;
    }

    public List<GroupMetricPO> listPartitionLatestMetrics(Long clusterPhyId, String group, String topic, List<String> metrics){
        Long latestTime = getLatestMetricTime();
        Long startTime  = latestTime - FIVE_MIN;

        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, latestTime);

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.LIST_GROUP_LATEST_METRICS_OF_PARTITION, clusterPhyId, group, topic, latestTime);

        List<GroupMetricPO> groupMetricPOS = esOpClient.performRequest(realIndex, dsl, GroupMetricPO.class);
        return filterMetrics(groupMetricPOS, metrics);
    }

    /**
     * 获取 match 命中或者不命中的次数，返回-1，代表查询异常
     */
    public Integer countMetricValue(Long clusterPhyId, String groupName, SearchTerm match, Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);
        String matchDsl  = buildTermsDsl(Arrays.asList(match));

        String dsl = match.isEqual()
                ? dslLoaderUtil.getFormatDslByFileName( DslConstant.COUNT_GROUP_METRIC_VALUE, clusterPhyId, groupName, startTime, endTime, matchDsl)
                : dslLoaderUtil.getFormatDslByFileName( DslConstant.COUNT_GROUP_NOT_METRIC_VALUE, clusterPhyId, groupName, startTime, endTime, matchDsl);

        return esOpClient.performRequestWithRouting(clusterPhyId.toString() + "@" + groupName, realIndex, dsl,
                s -> handleESQueryResponseCount(s), 3);
    }

    public Table<String/*metric*/, String/*topic&partition*/, List<MetricPointVO>> listGroupMetrics(Long clusterId, String groupName,
                                                                                            List<TopicPartitionKS> topicPartitionKS, List<String> metrics,
                                                                                            String aggType, Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, "avg");

        final Table<String, String, List<MetricPointVO>> table = HashBasedTable.create();

        for(TopicPartitionKS tp : topicPartitionKS){
            String  topic       = tp.getTopic();
            Integer partition   = tp.getPartition();

            String dsl = dslLoaderUtil.getFormatDslByFileName(
                    DslConstant.LIST_GROUP_METRICS,
                    clusterId,
                    groupName,
                    topic,
                    partition,
                    startTime,
                    endTime,
                    interval,
                    aggDsl
            );

            Map<String/*metric*/, List<MetricPointVO>> metricMap = esOpClient.performRequest(
                    realIndex,
                    dsl,
                    s -> handleGroupMetrics(s, aggType, metrics),
                    DEFAULT_RETRY_TIME
            );

            for(Map.Entry<String/*metric*/, List<MetricPointVO>> entry: metricMap.entrySet()){
                table.put(entry.getKey(), topic + "&" + partition, entry.getValue());
            }
        }

        return table;
    }

    /**
     * 获取[startTime,endTime]时间段内的groupName对应的topic&partition
     */
    public Set<TopicPartitionKS> listGroupTopicPartitions(Long clusterPhyId, String groupName, Long startTime, Long endTime) {
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_GROUP_TOPIC_PARTITION, clusterPhyId, groupName, startTime, endTime);

        List<GroupMetricPO> groupMetricPOS =  esOpClient.performRequestWithRouting(routing(clusterPhyId, groupName), realIndex, dsl, GroupMetricPO.class);
        return groupMetricPOS.stream().map(g -> new TopicPartitionKS(g.getTopic(), g.getPartitionId().intValue())).collect( Collectors.toSet());
    }

    /**************************************************** private method ****************************************************/
    private GroupMetricPO handleGroupMetricESQueryResponse(ESQueryResponse response, List<String> metrics,
                                                           Long clusterPhyId, String group, String topic){
        GroupMetricPO groupMetricPO = new GroupMetricPO();
        groupMetricPO.setClusterPhyId(clusterPhyId);
        groupMetricPO.setGroup(group);
        groupMetricPO.setTopic(topic);
        groupMetricPO.setGroupMetric(ZERO);

        if(null == response || null == response.getAggs()){
            return groupMetricPO;
        }

        Map<String, ESAggr> esAggrMap = response.getAggs().getEsAggrMap();
        if (null == esAggrMap) {
            return groupMetricPO;
        }

        for(String metric : metrics){
            Object value = esAggrMap.get(metric).getUnusedMap().get(VALUE);
            if(value     == null){continue;}
            groupMetricPO.getMetrics().put(metric, Float.parseFloat(value.toString()));
        }

        return groupMetricPO;
    }

    private Map<String/*metric*/, List<MetricPointVO>> handleGroupMetrics(ESQueryResponse response, String aggType, List<String> metrics){
        Map<String, ESAggr> esAggrMap = checkBucketsAndHitsOfResponseAggs(response);
        if(null == esAggrMap){return new HashMap<>();}

        Map<String, List<MetricPointVO>> metricMap = new HashMap<>();
        for(String metric : metrics){
            List<MetricPointVO> metricPoints = new ArrayList<>();

            esAggrMap.get(HIST).getBucketList().forEach(esBucket -> {
                try {
                    if (null == esBucket.getUnusedMap().get(KEY)) {
                        return;
                    }

                    Long    timestamp = Long.valueOf(esBucket.getUnusedMap().get(KEY).toString());
                    Object  value     = esBucket.getAggrMap().get(metric).getUnusedMap().get(VALUE);
                    if(value == null) {
                        return;
                    }

                    MetricPointVO metricPoint = new MetricPointVO();
                    metricPoint.setAggType(aggType);
                    metricPoint.setTimeStamp(timestamp);
                    metricPoint.setValue(value.toString());
                    metricPoint.setName(metric);

                    metricPoints.add(metricPoint);
                }catch (Exception e){
                    LOGGER.error("method=handleGroupMetrics||metric={}||errMsg=exception!", metric, e);
                }
            } );

            metricMap.put(metric, optimizeMetricPoints(metricPoints));
        }

        return metricMap;
    }

    private String routing(Long clusterPhyId, String groupName){
        return clusterPhyId + "@" + groupName;
    }
}
