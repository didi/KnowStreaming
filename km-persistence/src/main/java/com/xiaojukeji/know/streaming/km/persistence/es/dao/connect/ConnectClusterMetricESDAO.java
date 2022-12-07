package com.xiaojukeji.know.streaming.km.persistence.es.dao.connect;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.common.utils.MetricsUtils;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.BaseMetricESDAO;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;
import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.CONNECT_CLUSTER_INDEX;

@Component
public class ConnectClusterMetricESDAO extends BaseMetricESDAO {
    @PostConstruct
    public void init() {
        super.indexName     = CONNECT_CLUSTER_INDEX;
        checkCurrentDayIndexExist();
        register( this);
    }

    protected FutureWaitUtil<Void> queryFuture = FutureWaitUtil.init("ConnectClusterMetricESDAO", 4,8, 500);

    /**
     * 获取集群 clusterPhyId 中每个 metric 的 topN 的 connectCluster 在指定时间[startTime、endTime]区间内所有的指标
     * topN 按照[startTime, endTime] 时间段内最后一个值来排序
     */
    public Table<String/*metric*/, Long/*connectClusterId*/, List<MetricPointVO>> listMetricsByTop(Long clusterPhyId,
                                                                                                   List<Long> connectClusterIdList,
                                                                                                   List<String> metricNameList,
                                                                                                   String aggType,
                                                                                                   int topN,
                                                                                                   Long startTime,
                                                                                                   Long endTime){
        // 1、获取TopN
        Map<String, List<Long>> topNConnectClusterIds = getTopNConnectClusterIds(clusterPhyId, metricNameList, aggType, topN, startTime, endTime);

        Table<String, Long, List<MetricPointVO>> table = HashBasedTable.create();

        // 2、查询指标
        for(String metric : metricNameList) {
            table.putAll(
                    this.listMetricsByConnectClusterIdList(
                            clusterPhyId,
                            Arrays.asList(metric),
                            aggType,
                            topNConnectClusterIds.getOrDefault(metric, connectClusterIdList),
                            startTime,
                            endTime
                    )
            );
        }

        return table;
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的指定 connectClusters 在指定时间[startTime、endTime]区间内所有的指标
     */
    public Table<String/*metric*/, Long/*connectClusterId*/, List<MetricPointVO>> listMetricsByConnectClusterIdList(Long clusterPhyId,
                                                                                                                    List<String> metricNameList,
                                                                                                                    String aggType,
                                                                                                                    List<Long> connectClusterIdList,
                                                                                                                    Long startTime,
                                                                                                                    Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metricNameList, aggType);

        final Table<String, Long, List<MetricPointVO>> table = HashBasedTable.create();

        //4、构造dsl查询条件
        for(Long connectClusterId : connectClusterIdList){
            try {
                String dsl = dslLoaderUtil.getFormatDslByFileName(
                        DslConstant.GET_CONNECT_CLUSTER_AGG_LIST_METRICS,
                        clusterPhyId,
                        connectClusterId,
                        startTime,
                        endTime,
                        interval,
                        aggDsl
                );

                queryFuture.runnableTask(
                        String.format("class=ConnectClusterMetricESDAO||method=listMetricsByConnectClusterIdList||ClusterPhyId=%d", clusterPhyId),
                        5000,
                        () -> {
                            Map<String, List<MetricPointVO>> metricMap = esOpClient.performRequestWithRouting(
                                    String.valueOf(connectClusterId),
                                    realIndex,
                                    dsl,
                                    s -> handleListESQueryResponse(s, metricNameList, aggType),
                                    3
                            );

                            synchronized (table) {
                                for(String metric : metricMap.keySet()){
                                    table.put(metric, connectClusterId, metricMap.get(metric));
                                }
                            }
                        });
            } catch (Exception e) {
                LOGGER.error(
                        "class=ConnectClusterMetricESDAO||method=listMetricsByConnectClusterIdList||clusterPhyId={}||connectClusterId{}||errMsg=exception!",
                        clusterPhyId, connectClusterId, e
                );
            }
        }

        queryFuture.waitExecute();

        return table;
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的 topN 的 broker
     */
    //public for test
    public Map<String, List<Long>> getTopNConnectClusterIds(Long clusterPhyId,
                                                            List<String> metricNameList,
                                                            String aggType,
                                                            int topN,
                                                            Long startTime,
                                                            Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metricNameList, aggType);

        //4、查询es
        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_CONNECT_CLUSTER_AGG_TOP_METRICS,
                clusterPhyId,
                startTime,
                endTime,
                interval,
                aggDsl
        );

        return esOpClient.performRequest(
                realIndex,
                dsl,
                s -> handleTopConnectClusterESQueryResponse(s, metricNameList, topN),
                3
        );
    }

    /**************************************************** private method ****************************************************/

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
                        Object  value     = esBucket.getAggrMap().get(metric).getUnusedMap().get(VALUE);
                        if(null           == value){return;}

                        MetricPointVO metricPoint = new MetricPointVO();
                        metricPoint.setAggType(aggType);
                        metricPoint.setTimeStamp(timestamp);
                        metricPoint.setValue(value.toString());
                        metricPoint.setName(metric);

                        metricPoints.add(metricPoint);
                    }else {
                        LOGGER.info("");
                    }
                }catch (Exception e){
                    LOGGER.error("metric={}||errMsg=exception!", metric, e);
                }
            } );

            metricMap.put(metric, optimizeMetricPoints(metricPoints));
        }

        return metricMap;
    }

    private Map<String, List<Long>> handleTopConnectClusterESQueryResponse(ESQueryResponse response, List<String> metrics, int topN){
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
                        Long connectorClusterId = Long.valueOf(esBucket.getUnusedMap().get(KEY).toString());
                        Object           value  = esBucket.getAggrMap().get(HIST).getBucketList()
                                                    .get(0).getAggrMap().get(metric).getUnusedMap().get(VALUE);

                        if(null == value){return;}

                        List<Tuple<Long, Double>> connectorClusterValue = (null == metricBrokerValueMap.get(metric)) ?
                                new ArrayList<>() : metricBrokerValueMap.get(metric);

                        connectorClusterValue.add(new Tuple<>(connectorClusterId, Double.valueOf(value.toString())));
                        metricBrokerValueMap.put(metric, connectorClusterValue);
                    }
                }catch (Exception e){
                    LOGGER.error("metric={}||errMsg=exception!", metric, e);
                }
            } );
        }

        //2、对每个指标的broker按照指标值排序，并截取前topN个brokerIds
        for(String metric : metricBrokerValueMap.keySet()){
            List<Tuple<Long, Double>> connectorClusterValue = metricBrokerValueMap.get(metric);

            connectorClusterValue.sort((o1, o2) -> {
                if(null == o1 || null == o2){return 0;}
                return o2.getV2().compareTo(o1.getV2());
            } );

            List<Tuple<Long, Double>> temp = (connectorClusterValue.size() > topN) ? connectorClusterValue.subList(0, topN) : connectorClusterValue;
            List<Long> connectorClusterIds = temp.stream().map(t -> t.getV1()).collect(Collectors.toList());

            ret.put(metric, connectorClusterIds);
        }

        return ret;
    }
}
