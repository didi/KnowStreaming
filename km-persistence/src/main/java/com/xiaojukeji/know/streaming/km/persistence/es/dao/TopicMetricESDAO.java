package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchFuzzy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchShould;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchSort;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.TopicMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.common.utils.MetricsUtils;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslsConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;
import static com.xiaojukeji.know.streaming.km.common.constant.ESIndexConstant.*;

@Component
public class TopicMetricESDAO extends BaseMetricESDAO {

    @PostConstruct
    public void init() {
        super.indexName     = TOPIC_INDEX;
        super.indexTemplate = TOPIC_TEMPLATE;
        checkCurrentDayIndexExist();
        BaseMetricESDAO.register(indexName, this);
    }

    protected FutureWaitUtil<Void> queryFuture = FutureWaitUtil.init("TopicMetricESDAO", 4,8, 500);

    public List<TopicMetricPO> listTopicMaxMinMetrics(Long clusterPhyId, List<String> topics, String metric, boolean max, Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);
        SearchSort sort  = new SearchSort(metric, max, true);

        List<TopicMetricPO> ret = new ArrayList<>();
        for(String topic : topics){
            String sortDsl   = buildSortDsl(sort, SearchSort.DEFAULT);

            String dsl   = dslLoaderUtil.getFormatDslByFileName(
                    DslsConstant.GET_TOPIC_MAX_OR_MIN_SINGLE_METRIC, clusterPhyId, startTime, endTime, topic, sortDsl);
            TopicMetricPO topicMetricPO = esOpClient.performRequestAndTakeFirst(topic, realIndex, dsl, TopicMetricPO.class);
            ret.add(topicMetricPO);
        }

        return ret;
    }

    /**
     * 获取集群 clusterPhyId 中多个 topic 的每个 metric 的指定 topic 在指定时间[startTime、endTime]区间内聚合计算(avg、max)之后的统计值
     * 注意：es的(avg、max)聚合计算只能获取值，不能获取值之外的信息，如：时间
     */
    public Table<String/*topics*/, String/*metric*/, MetricPointVO> getTopicsAggsMetricsValue(Long clusterPhyId, List<String> topics, List<String> metrics,
                                                                                              String aggType, Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        String shouldDsl = buildShouldDsl(new SearchShould("topic", topics, true));
        StringBuilder appendQueryDsl = new StringBuilder();
        if(!StringUtils.isEmpty(shouldDsl)){
            appendQueryDsl.append(",").append(shouldDsl);
        }

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslsConstant.GET_TOPIC_AGG_SINGLE_METRICS, clusterPhyId, startTime, endTime, appendQueryDsl.toString(), aggDsl);

        return esOpClient.performRequest(realIndex, dsl,
                s -> handleSingleESQueryResponse(s, metrics, aggType), 3);
    }

    /**
     * 分页获取
     */
    public List<TopicMetricPO> listTopicWithLatestMetrics(Long clusterId, SearchSort sort, SearchFuzzy fuzzy, List<SearchShould> shoulds,  List<SearchTerm> terms){
        //1、构建dsl
        String sortDsl   = buildSortDsl(sort, SearchSort.DEFAULT);
        String shouldDsl = buildShouldDsl(shoulds);
        String prefixDsl = buildPrefixDsl(fuzzy);
        String termDsl   = buildTermsDsl(terms);

        StringBuilder appendQueryDsl = new StringBuilder();
        if(!StringUtils.isEmpty(termDsl)){
            appendQueryDsl.append(",").append(termDsl);
        }
        if(!StringUtils.isEmpty(prefixDsl)){
            appendQueryDsl.append(",").append(prefixDsl);
        }
        if(!StringUtils.isEmpty(shouldDsl)){
            appendQueryDsl.append(",").append(shouldDsl);
        }

        //2、获取最近的指标时间点
        Long latestMetricTime   = getLatestMetricTime(clusterId, appendQueryDsl.toString());
        if(null == latestMetricTime){
            return new ArrayList<>();
        }

        //3、获取需要查下的索引
        Long startTime   = latestMetricTime - ONE_HOUR;
        String realIndex = realIndex(startTime, latestMetricTime);

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslsConstant.LIST_TOPIC_WITH_LATEST_METRICS, clusterId, latestMetricTime, appendQueryDsl.toString(), sortDsl);

        return esOpClient.performRequest(realIndex, dsl, TopicMetricPO.class);
    }

    /**
     * 获取 match 命中或者不命中的次数，返回-1，代表查询异常
     */
    public Integer countMetricValue(Long clusterPhyId, String topic, SearchTerm term, Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);
        String termDsl   = buildTermsDsl(Arrays.asList(term));

        String dsl = term.isEqual()
                ? dslLoaderUtil.getFormatDslByFileName(DslsConstant.COUNT_TOPIC_METRIC_VALUE, clusterPhyId, topic, startTime, endTime, termDsl)
                : dslLoaderUtil.getFormatDslByFileName(DslsConstant.COUNT_TOPIC_NOT_METRIC_VALUE, clusterPhyId, topic, startTime, endTime, termDsl);

        return esOpClient.performRequestWithRouting(topic, realIndex, dsl,
                s -> handleESQueryResponseCount(s), 3);
    }

    /**
     * 获取 topic 所在 broker 最新的指标
     */
    public TopicMetricPO getTopicLatestMetricByBrokerId(Long clusterPhyId, String topic, Integer brokerId, List<String> metricNames){
        Long endTime    = getLatestMetricTime();
        Long startTime  = endTime - FIVE_MIN;

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslsConstant.GET_TOPIC_BROKER_LATEST_METRICS, clusterPhyId, topic, brokerId, startTime, endTime);

        TopicMetricPO topicMetricPO = esOpClient.performRequestAndTakeFirst(topic, realIndex(startTime, endTime), dsl, TopicMetricPO.class);

        return (null == topicMetricPO) ? new TopicMetricPO(topic, clusterPhyId) : filterMetrics(topicMetricPO, metricNames);
    }

    /**
     * 获取 topic 最新的指标
     */
    public List<TopicMetricPO> listTopicLatestMetric(Long clusterPhyId, List<String> topics, List<String> metricNames){
        Long endTime    = getLatestMetricTime();
        Long startTime  = endTime - FIVE_MIN;

        SearchShould should = new SearchShould("topic", topics);
        should.setField(true);

        String shouldDsl = buildShouldDsl(Arrays.asList(should));
        StringBuilder appendQueryDsl = new StringBuilder();
        if(!StringUtils.isEmpty(shouldDsl)){
            appendQueryDsl.append(",").append(shouldDsl);
        }

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslsConstant.GET_TOPIC_LATEST_METRICS, clusterPhyId, startTime, endTime, appendQueryDsl.toString());

        //topicMetricPOS 已经按照 timeStamp 倒序排好序了
        List<TopicMetricPO> topicMetricPOS = esOpClient.performRequest(realIndex(startTime, endTime), dsl, TopicMetricPO.class);

        //获取每个topic的第一个 TopicMetricPO 即可
        Map<String, TopicMetricPO> topicMetricMap = new HashMap<>();
        for(TopicMetricPO topicMetricPO : topicMetricPOS){
            topicMetricPO.setMetrics(topicMetricPO.getMetrics(metricNames));
            topicMetricMap.putIfAbsent(topicMetricPO.getTopic(), topicMetricPO);
        }

        return new ArrayList<>(topicMetricMap.values());
    }

    /**
     * 获取 topic 最新的指标
     */
    public TopicMetricPO getTopicLatestMetric(Long clusterPhyId, String topic, List<String> metricNames){
        Long endTime    = getLatestMetricTime();
        Long startTime  = endTime - FIVE_MIN;

        SearchTerm searchTerm = new SearchTerm("topic", topic);
        searchTerm.setField(true);

        String termDsl  = buildTermsDsl(Arrays.asList(searchTerm));
        StringBuilder appendQueryDsl = new StringBuilder();
        if(!StringUtils.isEmpty(termDsl)){
            appendQueryDsl.append(",").append(termDsl);
        }

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslsConstant.GET_TOPIC_LATEST_METRICS, clusterPhyId, startTime, endTime, appendQueryDsl.toString());

        TopicMetricPO topicMetricPO = esOpClient.performRequestAndTakeFirst(topic, realIndex(startTime, endTime), dsl, TopicMetricPO.class);

        return (null == topicMetricPO) ? new TopicMetricPO(topic, clusterPhyId) : filterMetrics(topicMetricPO, metricNames);
    }

    /**
     * 获取每个 metric 的 topN 个 topic 的指标，如果获取不到 topN 的topics, 则默认返回 defaultTopics 的指标
     */
    public Table<String/*metric*/, String/*topics*/, List<MetricPointVO>> listTopicMetricsByTopN(Long clusterPhyId, List<String> defaultTopics,
                                                                                                 List<String> metrics, String aggType, int topN,
                                                                                                 Long startTime, Long endTime){
        //1、获取topN要查询的topic，每一个指标的topN的topic可能不一样
        Map<String, List<String>> metricTopics = getTopNTopics(clusterPhyId, metrics, aggType, topN, startTime, endTime);

        Table<String, String, List<MetricPointVO>> table = HashBasedTable.create();

        for(String metric : metrics){
            table.putAll(listTopicMetricsByTopics(clusterPhyId, Arrays.asList(metric),
                    aggType, metricTopics.getOrDefault(metric, defaultTopics), startTime, endTime));
        }

        return table;
    }

    /**
     * 获取每个 metric 指定个 topic 的指标
     */
    public Table<String/*metric*/, String/*topics*/, List<MetricPointVO>> listTopicMetricsByTopics(Long clusterPhyId, List<String> metrics,
                                                                                                      String aggType, List<String> topics,
                                                                                                      Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        final Table<String, String, List<MetricPointVO>> table = HashBasedTable.create();

        //4、构造dsl查询条件
        for(String topic : topics){
            try {
                queryFuture.runnableTask(
                        String.format("class=TopicMetricESDAO||method=listTopicMetricsByTopics||ClusterPhyId=%d||topicName=%s",
                                clusterPhyId, topic),
                        3000,
                        () -> {
                            String dsl = dslLoaderUtil.getFormatDslByFileName(
                                    DslsConstant.GET_TOPIC_AGG_LIST_METRICS, clusterPhyId, topic, startTime, endTime, interval, aggDsl);

                            Map<String/*metric*/, List<MetricPointVO>> metricMap = esOpClient.performRequestWithRouting(topic, realIndex, dsl,
                                    s -> handleListESQueryResponse(s, metrics, aggType), 3);

                            synchronized (table){
                                for(String metric : metricMap.keySet()){
                                    table.put(metric, topic, metricMap.get(metric));
                                }
                            }
                        });
            }catch (Exception e){
                LOGGER.error("method=listBrokerMetricsByBrokerIds||clusterPhyId={}||brokerId{}||errMsg=exception!",
                        clusterPhyId, topic, e);
            }
        }

        queryFuture.waitExecute();

        return table;
    }

    //public for test
    public Map<String, List<String>> getTopNTopics(Long clusterPhyId, List<String> metrics,
                                                   String aggType, int topN,
                                                   Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        //4、查询es
        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslsConstant.GET_TOPIC_AGG_TOP_METRICS, clusterPhyId, startTime, endTime, interval, aggDsl);

        return esOpClient.performRequest(realIndex, dsl,
                s -> handleTopTopicESQueryResponse(s, metrics, topN), 3);
    }

    /**************************************************** private method ****************************************************/
    private Table<String/*topic*/, String/*metric*/, MetricPointVO> handleSingleESQueryResponse(ESQueryResponse response, List<String> metrics, String aggType){
        Table<String, String, MetricPointVO> table = HashBasedTable.create();

        Map<String, ESAggr> esAggrMap = checkBucketsAndHitsOfResponseAggs(response);
        if(null == esAggrMap){return table;}

        for(String metric : metrics){
            esAggrMap.get(HIST).getBucketList().forEach( esBucket -> {
                try {
                    if (null != esBucket.getUnusedMap().get(KEY)) {
                        String  topic  = esBucket.getUnusedMap().get(KEY).toString();
                        String  value  = esBucket.getAggrMap().get(metric).getUnusedMap().get(VALUE).toString();

                        MetricPointVO metricPoint = new MetricPointVO();
                        metricPoint.setAggType(aggType);
                        metricPoint.setValue(value);
                        metricPoint.setName(metric);

                        table.put(topic, metric, metricPoint);
                    }else {
                        LOGGER.debug("method=handleListESQueryResponse||metric={}||errMsg=get topic is null!", metric);
                    }
                }catch (Exception e){
                    LOGGER.error("method=handleListESQueryResponse||metric={}||errMsg=exception!", metric, e);
                }
            });
        }

        return table;
    }

    private Map<String, List<MetricPointVO>> handleListESQueryResponse(ESQueryResponse response, List<String> metrics, String aggType){
        Map<String, List<MetricPointVO>> metricMap = new HashMap<>();

        Map<String, ESAggr> esAggrMap = checkBucketsAndHitsOfResponseAggs(response);
        if(null == esAggrMap){return metricMap;}

        for(String metric : metrics){
            List<MetricPointVO> metricPoints = new ArrayList<>();

            esAggrMap.get(HIST).getBucketList().forEach( esBucket -> {
                try {
                    if (null != esBucket.getUnusedMap().get(KEY)) {
                        Long    timestamp = Long.valueOf(esBucket.getUnusedMap().get(KEY).toString());
                        String  value     = esBucket.getAggrMap().get(metric).getUnusedMap().get(VALUE).toString();

                        MetricPointVO metricPoint = new MetricPointVO();
                        metricPoint.setAggType(aggType);
                        metricPoint.setTimeStamp(timestamp);
                        metricPoint.setValue(value);
                        metricPoint.setName(metric);

                        metricPoints.add(metricPoint);
                    }else {
                        LOGGER.info("");
                    }
                }catch (Exception e){
                    LOGGER.error("method=handleListESQueryResponse||metric={}||errMsg=exception!", metric, e);
                }
            } );

            metricMap.put(metric, optimizeMetricPoints(metricPoints));
        }

        return metricMap;
    }

    private Map<String, List<String>> handleTopTopicESQueryResponse(ESQueryResponse response, List<String> metrics, int topN){
        Map<String, List<String>> ret = new HashMap<>();

        Map<String, ESAggr> esAggrMap = checkBucketsAndHitsOfResponseAggs(response);
        if(null == esAggrMap){return ret;}

        Map<String, List<Tuple<String, Double>>> metricsTopicValueMap = new HashMap<>();

        //1、先获取每个指标对应的所有 topic 以及指标的值
        for(String metric : metrics) {
            esAggrMap.get(HIST).getBucketList().forEach( esBucket -> {
                try {
                    if (null != esBucket.getUnusedMap().get(KEY)) {
                        String topic = esBucket.getUnusedMap().get(KEY).toString();
                        Double value = Double.valueOf(esBucket.getAggrMap().get(HIST).getBucketList().get(0).getAggrMap()
                                .get(metric).getUnusedMap().get(VALUE).toString());

                        List<Tuple<String, Double>> brokerValue = (null == metricsTopicValueMap.get(metric)) ?
                                new ArrayList<>() : metricsTopicValueMap.get(metric);

                        brokerValue.add(new Tuple<>(topic, value));
                        metricsTopicValueMap.put(metric, brokerValue);
                    }
                }catch (Exception e){
                    LOGGER.error("method=handleTopBrokerESQueryResponse||metric={}||errMsg=exception!", metric, e);
                }
            } );
        }

        //2、对每个指标的broker按照指标值排序，并截取前topN个brokerIds
        for(String metric : metricsTopicValueMap.keySet()){
            List<Tuple<String, Double>> brokerValue = metricsTopicValueMap.get(metric);

            brokerValue.sort((o1, o2) -> {
                if(null == o1 || null == o2){return 0;}
                return o2.getV2().compareTo(o1.getV2());
            } );

            List<Tuple<String, Double>> temp = (brokerValue.size() > topN) ? brokerValue.subList(0, topN) : brokerValue;
            List<String> topics = temp.stream().map(t -> t.getV1()).collect(Collectors.toList());

            ret.put(metric, topics);
        }

        return ret;
    }

    private Map<String/*metric*/, Map<String/*topic*/, List<MetricPointVO>>> topicMetricMap2MetricTopicMap(
            Map<String/*topic*/, Map<String/*metric*/, List<MetricPointVO>>> topicMetricMap){
        Map<String/*metric*/, Map<String/*topic*/, List<MetricPointVO>>> ret = new HashMap<>();

        for(String topic : topicMetricMap.keySet()){
            Map<String/*metric*/, List<MetricPointVO>> metricMap = topicMetricMap.get(topic);

            for(String metric : metricMap.keySet()){
                Map<String/*topic*/, List<MetricPointVO>> brokerMap = (null == ret.get(metric)) ? new HashMap<>() : ret.get(metric);

                brokerMap.put(topic, metricMap.get(metric));
                ret.put(metric, brokerMap);
            }
        }

        return ret;
    }
}
