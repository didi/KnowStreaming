package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BrokerMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.common.utils.MetricsUtils;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslsConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;
import static com.xiaojukeji.know.streaming.km.common.enums.metric.KafkaMetricIndexEnum.BROKER_INFO;

@Component
public class BrokerMetricESDAO extends BaseMetricESDAO {
    @PostConstruct
    public void init() {
        super.indexName = BROKER_INFO.getIndex();
        BaseMetricESDAO.register(BROKER_INFO, this);
    }

    protected FutureWaitUtil<Void> queryFuture = FutureWaitUtil.init("BrokerMetricESDAO", 4,8, 500);

    /**
     * 获取集群 clusterId 中 brokerId 最新的统计指标
     */
    public BrokerMetricPO getBrokerLatestMetrics(Long clusterId, Integer brokerId){
        Long endTime   = getLatestMetricTime(clusterId);
        Long startTime = endTime - FIVE_MIN;

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslsConstant.GET_BROKER_LATEST_METRICS, clusterId, brokerId, startTime, endTime);

        BrokerMetricPO brokerMetricPO = esOpClient.performRequestAndTakeFirst(
                brokerId.toString(), realIndex(startTime, endTime), dsl, BrokerMetricPO.class);

        return (null == brokerMetricPO) ? new BrokerMetricPO(clusterId, brokerId) : brokerMetricPO;
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的指定 broker 在指定时间[startTime、endTime]区间内聚合计算(avg、max)之后的统计值
     */
    public Map<String/*metric*/, MetricPointVO> getBrokerMetricsPoint(Long clusterPhyId, Integer brokerId, List<String> metrics,
                                                           String aggType, Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslsConstant.GET_BROKER_AGG_SINGLE_METRICS, clusterPhyId, brokerId, startTime, endTime, aggDsl);

        return esOpClient.performRequestWithRouting(String.valueOf(brokerId), realIndex, dsl,
                s -> handleSingleESQueryResponse(s, metrics, aggType), 3);
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
        for(String metric : metricBrokerIds.keySet()){
            table.putAll(listBrokerMetricsByBrokerIds(clusterPhyId, Arrays.asList(metric),
                    aggType, metricBrokerIds.getOrDefault(metric, brokerIds), startTime, endTime));
        }

        return table;
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的指定 brokers 在指定时间[startTime、endTime]区间内所有的指标
     */
    public Table<String/*metric*/, Long/*brokerId*/, List<MetricPointVO>> listBrokerMetricsByBrokerIds(Long clusterPhyId, List<String> metrics,
                                                                                                       String aggType, List<Long> brokerIds,
                                                                                                       Long startTime, Long endTime){
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
                        DslsConstant.GET_BROKER_AGG_LIST_METRICS, clusterPhyId, brokerId, startTime, endTime, interval, aggDsl);

                queryFuture.runnableTask(
                        String.format("class=BrokerMetricESDAO||method=listBrokerMetricsByBrokerIds||ClusterPhyId=%d", clusterPhyId),
                        5000,
                        () -> {
                            Map<String, List<MetricPointVO>> metricMap = esOpClient.performRequestWithRouting(String.valueOf(brokerId), realIndex, dsl,
                                    s -> handleListESQueryResponse(s, metrics, aggType), 3);

                            synchronized (table){
                                for(String metric : metricMap.keySet()){
                                    table.put(metric, brokerId, metricMap.get(metric));
                                }
                            }
                        });
            }catch (Exception e){
                LOGGER.error("method=listBrokerMetricsByBrokerIds||clusterPhyId={}||brokerId{}||errMsg=exception!", clusterPhyId, brokerId, e);
            }
        }

        queryFuture.waitExecute();

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
                DslsConstant.GET_BROKER_AGG_TOP_METRICS, clusterPhyId, startTime, endTime, interval, aggDsl);

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
            String value = esAggrMap.get(metric).getUnusedMap().get(VALUE).toString();

            MetricPointVO metricPoint = new MetricPointVO();
            metricPoint.setAggType(aggType);
            metricPoint.setValue(value);
            metricPoint.setName(metric);

            metricMap.put(metric, metricPoint);
        }

        return metricMap;
    }

    private Map<String, List<MetricPointVO>> handleListESQueryResponse(ESQueryResponse response, List<String> metrics, String aggType){
        Map<String, List<MetricPointVO>> metricMap = new HashMap<>();

        if(null == response || null == response.getAggs()){
            return metricMap;
        }

        Map<String, ESAggr> esAggrMap = response.getAggs().getEsAggrMap();
        if (null == esAggrMap || null == esAggrMap.get(HIST)) {
            return metricMap;
        }

        if(CollectionUtils.isEmpty(esAggrMap.get(HIST).getBucketList())){
            return metricMap;
        }

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
                    LOGGER.error("metric={}||errMsg=exception!", metric, e);
                }
            } );

            metricMap.put(metric, metricPoints);
        }

        return metricMap;
    }

    private Map<String, List<Long>> handleTopBrokerESQueryResponse(ESQueryResponse response, List<String> metrics, int topN){
        Map<String, List<Long>> ret = new HashMap<>();

        if(null == response || null == response.getAggs()){
            return ret;
        }

        Map<String, ESAggr> esAggrMap = response.getAggs().getEsAggrMap();
        if (null == esAggrMap || null == esAggrMap.get(HIST)) {
            return ret;
        }

        if(CollectionUtils.isEmpty(esAggrMap.get(HIST).getBucketList())){
            return ret;
        }

        Map<String, List<Tuple<Long, Double>>> metricBrokerValueMap = new HashMap<>();

        //1、先获取每个指标对应的所有brokerIds以及指标的值
        for(String metric : metrics) {
            esAggrMap.get(HIST).getBucketList().forEach( esBucket -> {
                try {
                    if (null != esBucket.getUnusedMap().get(KEY)) {
                        Long brokerId = Long.valueOf(esBucket.getUnusedMap().get(KEY).toString());
                        Double value  = Double.valueOf(esBucket.getAggrMap().get(HIST).getBucketList().get(0).getAggrMap()
                                .get(metric).getUnusedMap().get(VALUE).toString());

                        List<Tuple<Long, Double>> brokerValue = (null == metricBrokerValueMap.get(metric)) ?
                                new ArrayList<>() : metricBrokerValueMap.get(metric);

                        brokerValue.add(new Tuple<>(brokerId, value));
                        metricBrokerValueMap.put(metric, brokerValue);
                    }
                }catch (Exception e){
                    LOGGER.error("metrice={}||errMsg=exception!", metric, e);
                }
            } );
        }

        //2、对每个指标的broker按照指标值排序，并截取前topN个brokerIds
        for(String metric : metricBrokerValueMap.keySet()){
            List<Tuple<Long, Double>> brokerValue = metricBrokerValueMap.get(metric);

            brokerValue.sort((o1, o2) -> {
                if(null == o1 || null == o2){return 0;}
                return o2.getV2().compareTo(o1.getV2());
            } );

            List<Tuple<Long, Double>> temp = (brokerValue.size() > topN) ? brokerValue.subList(0, topN) : brokerValue;
            List<Long> brokerIds = temp.stream().map(t -> t.getV1()).collect( Collectors.toList());

            ret.put(metric, brokerIds);
        }

        return ret;
    }
}
