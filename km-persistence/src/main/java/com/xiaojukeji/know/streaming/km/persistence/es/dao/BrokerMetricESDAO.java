package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BrokerMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.utils.MetricsUtils;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;
import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.BROKER_INDEX;

@Component
public class BrokerMetricESDAO extends BaseMetricESDAO {
    @PostConstruct
    public void init() {
        super.indexName = BROKER_INDEX;
        checkCurrentDayIndexExist();
        register( this);
    }

    /**
     * 获取集群 clusterId 中 brokerId 最新的统计指标
     */
    public BrokerMetricPO getBrokerLatestMetrics(Long clusterId, Integer brokerId){
        Long endTime   = getLatestMetricTime(clusterId);
        Long startTime = endTime - FIVE_MIN;

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_BROKER_LATEST_METRICS, clusterId, brokerId, startTime, endTime);

        BrokerMetricPO brokerMetricPO = esOpClient.performRequestAndTakeFirst(
                brokerId.toString(),
                realIndex(startTime, endTime),
                dsl,
                BrokerMetricPO.class
        );

        return (null == brokerMetricPO) ? new BrokerMetricPO(clusterId, brokerId) : brokerMetricPO;
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的指定 broker 在指定时间[startTime、endTime]区间内聚合计算(avg、max)之后的统计值
     */
    public Map<String/*metric*/, MetricPointVO> getBrokerMetricsPoint(Long clusterPhyId,
                                                                      Integer brokerId,
                                                                      List<String> metrics,
                                                                      String aggType,
                                                                      Long startTime,
                                                                      Long endTime) {
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_BROKER_AGG_SINGLE_METRICS, clusterPhyId, brokerId, startTime, endTime, aggDsl);

        return esOpClient.performRequestWithRouting(
                String.valueOf(brokerId),
                realIndex,
                dsl,
                s -> handleSingleESQueryResponse(s, metrics, aggType),
                3
        );
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的 topN 的 broker 在指定时间[startTime、endTime]区间内所有的指标
     * topN 按照[startTime, endTime] 时间段内最后一个值来排序
     */
    public Table<String/*metric*/, Long/*brokerId*/, List<MetricPointVO>> listBrokerMetricsByTop(Long clusterPhyId, List<Long> brokerIds,
                                                                                                 List<String> metrics, String aggType, int topN,
                                                                                                 Long startTime, Long endTime){
        //1、获取topN要查询brokerId，每一个指标的topN的brokerId可能不一样
        Map<String, List<Long>> metricBrokerIds = getTopNBrokerIds(clusterPhyId, metrics, aggType, topN, startTime, endTime);

        Table<String, Long, List<MetricPointVO>> table = HashBasedTable.create();

        //2、查询指标
        for(String metric : metrics) {
            table.putAll(
                    this.listBrokerMetricsByBrokerIds(
                            clusterPhyId,
                            Arrays.asList(metric),
                            aggType,
                            metricBrokerIds.getOrDefault(metric, brokerIds),
                            startTime,
                            endTime
                    )
            );
        }

        return table;
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的指定 brokers 在指定时间[startTime、endTime]区间内所有的指标
     */
    public Table<String/*metric*/, Long/*brokerId*/, List<MetricPointVO>> listBrokerMetricsByBrokerIds(Long clusterPhyId,
                                                                                                       List<String> metrics,
                                                                                                       String aggType,
                                                                                                       List<Long> brokerIds,
                                                                                                       Long startTime,
                                                                                                       Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        final Table<String, Long, List<MetricPointVO>> table = HashBasedTable.create();

        //4、构造dsl查询条件
        for(Long brokerId : brokerIds){
            try {
                String dsl = dslLoaderUtil.getFormatDslByFileName(
                        DslConstant.GET_BROKER_AGG_LIST_METRICS,
                        clusterPhyId,
                        brokerId,
                        startTime,
                        endTime,
                        interval,
                        aggDsl
                );

                esTPService.submitSearchTask(
                        String.format("class=BrokerMetricESDAO||method=listBrokerMetricsByBrokerIds||ClusterPhyId=%d", clusterPhyId),
                        5000,
                        () -> {
                            Map<String, List<MetricPointVO>> metricMap = esOpClient.performRequestWithRouting(
                                    String.valueOf(brokerId),
                                    realIndex,
                                    dsl,
                                    s -> handleListESQueryResponse(s, metrics, aggType),
                                    3
                            );

                            synchronized (table) {
                                for(Map.Entry<String, List<MetricPointVO>> entry: metricMap.entrySet()){
                                    table.put(entry.getKey(), brokerId, entry.getValue());
                                }
                            }
                        });
            } catch (Exception e){
                LOGGER.error("method=listBrokerMetricsByBrokerIds||clusterPhyId={}||brokerId{}||errMsg=exception!", clusterPhyId, brokerId, e);
            }
        }

        esTPService.waitExecute();

        return table;
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的 topN 的 broker
     */
    //public for test
    public Map<String, List<Long>> getTopNBrokerIds(Long clusterPhyId, List<String> metrics,
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
                DslConstant.GET_BROKER_AGG_TOP_METRICS, clusterPhyId, startTime, endTime, interval, aggDsl);

        return esOpClient.performRequest(realIndex, dsl,
                s -> handleTopBrokerESQueryResponse(s, metrics, topN), 3);
    }

    /**************************************************** private method ****************************************************/
    private Map<String/*metric*/, MetricPointVO> handleSingleESQueryResponse(ESQueryResponse response, List<String> metrics, String aggType){
        Map<String/*metric*/, MetricPointVO> metricMap = new HashMap<>();

        if(null == response || null == response.getAggs()){
            return metricMap;
        }

        Map<String, ESAggr> esAggrMap = response.getAggs().getEsAggrMap();
        if (null == esAggrMap) {
            return metricMap;
        }

        for(String metric : metrics){
            Object value = esAggrMap.get(metric).getUnusedMap().get(VALUE);
            if(null      == value){continue;}

            MetricPointVO metricPoint = new MetricPointVO();
            metricPoint.setAggType(aggType);
            metricPoint.setValue(value.toString());
            metricPoint.setName(metric);

            metricMap.put(metric, metricPoint);
        }

        return metricMap;
    }

    private Map<String, List<MetricPointVO>> handleListESQueryResponse(ESQueryResponse response, List<String> metricNameList, String aggType){
        Map<String, List<MetricPointVO>> metricMap = new HashMap<>();

        Map<String, ESAggr> esAggrMap = this.checkBucketsAndHitsOfResponseAggs(response);
        if (esAggrMap == null) {
            return metricMap;
        }

        for(String metricName : metricNameList){
            List<MetricPointVO> metricPoints = new ArrayList<>();

            esAggrMap.get(HIST).getBucketList().forEach(esBucket -> {
                try {
                    if (null == esBucket.getUnusedMap().get(KEY)) {
                        return;
                    }

                    Long    timestamp = Long.valueOf(esBucket.getUnusedMap().get(KEY).toString());
                    Object  value     = esBucket.getAggrMap().get(metricName).getUnusedMap().get(VALUE);
                    if(null == value) {
                        return;
                    }

                    metricPoints.add(new MetricPointVO(metricName, timestamp, value.toString(), aggType));
                } catch (Exception e){
                    LOGGER.error("method=handleListESQueryResponse||metricName={}||errMsg=exception!", metricName, e);
                }
            } );

            metricMap.put(metricName, optimizeMetricPoints(metricPoints));
        }

        return metricMap;
    }

    private Map<String, List<Long>> handleTopBrokerESQueryResponse(ESQueryResponse response, List<String> metricNameList, int topN) {
        Map<String, List<Long>> ret = new HashMap<>();

        Map<String, ESAggr> esAggrMap = this.checkBucketsAndHitsOfResponseAggs(response);
        if (esAggrMap == null) {
            return ret;
        }

        Map<String, List<Tuple<Long, Double>>> metricNameBrokerValueMap = new HashMap<>();

        //1、先获取每个指标对应的所有brokerIds以及指标的值
        for(String metricName : metricNameList) {
            esAggrMap.get(HIST).getBucketList().forEach(esBucket -> {
                try {
                    if (null == esBucket.getUnusedMap().get(KEY)) {
                        return;
                    }

                    Long brokerId = Long.valueOf(esBucket.getUnusedMap().get(KEY).toString());
                    Object value  = esBucket.getAggrMap().get(HIST).getBucketList().get(0).getAggrMap().get(metricName).getUnusedMap().get(VALUE);
                    if(null == value) {
                        return;
                    }

                    metricNameBrokerValueMap.putIfAbsent(metricName, new ArrayList<>());
                    metricNameBrokerValueMap.get(metricName).add(new Tuple<>(brokerId, Double.valueOf(value.toString())));
                } catch (Exception e) {
                    LOGGER.error("method=handleTopBrokerESQueryResponse||metric={}||errMsg=exception!", metricName, e);
                }
            });
        }

        //2、对每个指标的broker按照指标值排序，并截取前topN个brokerIds
        for(Map.Entry<String, List<Tuple<Long, Double>>> entry : metricNameBrokerValueMap.entrySet()){
            entry.getValue().sort((o1, o2) -> {
                if(null == o1 || null == o2){
                    return 0;
                }

                return o2.getV2().compareTo(o1.getV2());
            } );

            // 获取TopN的Broker
            List<Long> brokerIdList = entry.getValue().subList(0, Math.min(topN, entry.getValue().size())).stream().map(elem -> elem.getV1()).collect(Collectors.toList());
            ret.put(entry.getKey(), brokerIdList);
        }

        return ret;
    }
}
