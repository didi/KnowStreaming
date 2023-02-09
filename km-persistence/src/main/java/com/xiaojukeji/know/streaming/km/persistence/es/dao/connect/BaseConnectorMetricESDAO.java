package com.xiaojukeji.know.streaming.km.persistence.es.dao.connect;

import com.alibaba.druid.util.StringUtils;
import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.connect.ConnectorMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.utils.MetricsUtils;
import com.xiaojukeji.know.streaming.km.common.utils.Triple;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.BaseMetricESDAO;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;

public class BaseConnectorMetricESDAO extends BaseMetricESDAO {

    /**
     * 获取每个 metric 的 topN 个 connector 的指标，如果获取不到 topN 的 connectors, 则默认返回 defaultConnectorList 的指标
     */
    public Table<String/*metric*/, Tuple<Long, String>, List<MetricPointVO>> listMetricsByTopN(Long clusterPhyId,
                                                                                               List<Tuple<Long, String>> defaultConnectorList,
                                                                                               List<String> metricNameList,
                                                                                               String aggType,
                                                                                               int topN,
                                                                                               Long startTime,
                                                                                               Long endTime) {
        //1、获取topN要查询的topic，每一个指标的topN的topic可能不一样
        Map<String, List<Tuple<Long, String>>> metricsMap = this.getTopNConnectors(clusterPhyId, metricNameList, aggType, topN, startTime, endTime);

        //2、获取connector列表
        Set<Tuple<Long, String>> connectorSet = new HashSet<>(defaultConnectorList);
        metricsMap.values().forEach(elem -> connectorSet.addAll(elem));

        //3、批量获取信息
        Table<String, Tuple<Long, String>, List<MetricPointVO>> allMetricsTable = this.listMetricsByConnectors(
                clusterPhyId,
                metricNameList,
                aggType,
                new ArrayList<>(connectorSet),
                startTime,
                endTime
        );

        //4、获取Top-Metric
        Table<String, Tuple<Long, String>, List<MetricPointVO>> metricTable = HashBasedTable.create();
        for (String metricName: metricNameList) {
            for (Tuple<Long, String> connector: metricsMap.getOrDefault(metricName, defaultConnectorList)) {
                List<MetricPointVO> voList = allMetricsTable.get(metricName, connector);
                if (voList == null) {
                    continue;
                }

                metricTable.put(metricName, connector, voList);
            }
        }

        // 返回结果
        return metricTable;
    }

    public List<ConnectorMetricPO> getConnectorLatestMetric(Long clusterPhyId, List<Tuple<Long, String>> connectClusterIdAndConnectorNameList, List<String> metricsNames){
        List<ConnectorMetricPO> connectorMetricPOS = new CopyOnWriteArrayList<>();

        for(Tuple<Long, String> connectClusterIdAndConnectorName : connectClusterIdAndConnectorNameList){
            esTPService.submitSearchTask(
                    "getConnectorLatestMetric",
                    30000,
                    () -> {
                        ConnectorMetricPO connectorMetricPO = this.getConnectorLatestMetric(clusterPhyId, connectClusterIdAndConnectorName.getV1(), connectClusterIdAndConnectorName.getV2(), metricsNames);
                        connectorMetricPOS.add(connectorMetricPO);
                    });
        }

        esTPService.waitExecute();
        return connectorMetricPOS;
    }

    public ConnectorMetricPO getConnectorLatestMetric(Long clusterPhyId, Long connectClusterId, String connectorName, List<String> metricsNames){
        Long endTime    = getLatestMetricTime();
        Long startTime  = endTime - FIVE_MIN;

        SearchTerm searchClusterIdTerm = new SearchTerm("connectClusterId", connectClusterId.toString());
        searchClusterIdTerm.setField(true);

        SearchTerm searchClusterNameTerm = new SearchTerm("connectorName", connectorName);
        searchClusterNameTerm.setField(true);

        String termDsl  = buildTermsDsl(Arrays.asList(searchClusterIdTerm, searchClusterNameTerm));
        StringBuilder appendQueryDsl = new StringBuilder();
        if(!StringUtils.isEmpty(termDsl)){
            appendQueryDsl.append(",").append(termDsl);
        }

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_CONNECTOR_LATEST_METRICS, connectClusterId, connectorName, startTime, endTime, appendQueryDsl.toString());

        ConnectorMetricPO connectorMetricPO = esOpClient.performRequestAndTakeFirst(
                connectClusterId.toString(), realIndex(startTime, endTime), dsl, ConnectorMetricPO.class);

        return (null == connectorMetricPO) ? new ConnectorMetricPO(clusterPhyId, connectClusterId, connectorName)
                : filterMetrics(connectorMetricPO, metricsNames);
    }

    /**
     * 获取每个 metric 指定个 topic 的指标
     */
    public Table<String/*metric*/, Tuple<Long, String>, List<MetricPointVO>> listMetricsByConnectors(Long clusterPhyId,
                                                                                                     List<String> metricNameList,
                                                                                                     String aggType,
                                                                                                     List<Tuple<Long, String>> connectorList,
                                                                                                     Long startTime,
                                                                                                     Long endTime) {
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metricNameList, aggType);

        final Table<String, Tuple<Long, String>, List<MetricPointVO>> table = HashBasedTable.create();

        //4、构造dsl查询条件
        for(Tuple<Long, String> connector : connectorList) {
            try {
                esTPService.submitSearchTask(
                        String.format("class=BaseConnectorMetricESDAO||method=listMetricsByConnectors||ClusterPhyId=%d||connectorName=%s", clusterPhyId, connector.getV2()),
                        3000,
                        () -> {
                            String dsl = dslLoaderUtil.getFormatDslByFileName(
                                    DslConstant.GET_CONNECTOR_AGG_LIST_METRICS,
                                    clusterPhyId,
                                    connector.getV1(),
                                    connector.getV2(),
                                    startTime,
                                    endTime,
                                    interval,
                                    aggDsl
                            );

                            Map<String/*metric*/, List<MetricPointVO>> metricMap = esOpClient.performRequestWithRouting(
                                    connector.getV1().toString(),
                                    realIndex,
                                    dsl,
                                    s -> handleListESQueryResponse(s, metricNameList, aggType),
                                    3
                            );

                            synchronized (table) {
                                for(Map.Entry<String/*metricName*/, List<MetricPointVO>> entry: metricMap.entrySet()){
                                    table.put(entry.getKey(), connector, entry.getValue());
                                }
                            }
                        });
            } catch (Exception e) {
                LOGGER.error(
                        "method=listMetricsByConnectors||clusterPhyId={}||connectClusterId={}||connectorName{}||errMsg=exception!",
                        clusterPhyId, connector.getV1(), connector.getV2(), e
                );
            }
        }

        esTPService.waitExecute();

        return table;
    }

    //public for test
    public Map<String, List<Tuple<Long, String>>> getTopNConnectors(Long clusterPhyId,
                                                                    List<String> metricNameList,
                                                                    String aggType,
                                                                    int topN,
                                                                    Long startTime,
                                                                    Long endTime) {
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metricNameList, aggType);

        //4、查询es
        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_CONNECTOR_AGG_TOP_METRICS,
                clusterPhyId,
                startTime,
                endTime,
                interval,
                aggDsl
        );

        return esOpClient.performRequest(
                realIndex,
                dsl,
                s -> handleTopConnectorESQueryResponse(s, metricNameList, topN),
                3
        );
    }


    /**************************************************** private method ****************************************************/


    private Map<String, List<MetricPointVO>> handleListESQueryResponse(ESQueryResponse response, List<String> metrics, String aggType){
        Map<String, List<MetricPointVO>> metricMap = new HashMap<>();

        Map<String, ESAggr> esAggrMap = this.checkBucketsAndHitsOfResponseAggs(response);
        if(null == esAggrMap) {
            return metricMap;
        }

        for(String metric : metrics) {
            List<MetricPointVO> metricPoints = new ArrayList<>();

            esAggrMap.get(HIST).getBucketList().forEach( esBucket -> {
                try {
                    if (null != esBucket.getUnusedMap().get(KEY)) {
                        Long    timestamp = Long.valueOf(esBucket.getUnusedMap().get(KEY).toString());
                        Object  value  = esBucket.getAggrMap().get(metric).getUnusedMap().get(VALUE);
                        if(value == null){
                            return;
                        }

                        metricPoints.add(new MetricPointVO(metric, timestamp, value.toString(), aggType));
                    }
                }catch (Exception e){
                    LOGGER.error("method=handleListESQueryResponse||metric={}||errMsg=exception!", metric, e);
                }
            } );

            metricMap.put(metric, optimizeMetricPoints(metricPoints));
        }

        return metricMap;
    }

    private Map<String, List<Tuple<Long, String>>> handleTopConnectorESQueryResponse(ESQueryResponse response,
                                                                                     List<String> metricNameList,
                                                                                     int topN) {
        Map<String, List<Tuple<Long, String>>> ret = new HashMap<>();

        Map<String, ESAggr> esAggrMap = this.checkBucketsAndHitsOfResponseAggs(response);
        if(null == esAggrMap) {
            return ret;
        }

        Map<String, List<Triple<Long, String, Double>>> metricValueMap = new HashMap<>();

        // 1、先获取每个指标对应的所有 connector 以及指标的值
        for(String metricName : metricNameList) {
            esAggrMap.get(HIST).getBucketList().forEach(esBucket -> {
                try {
                    if (null != esBucket.getUnusedMap().get(KEY)) {
                        String connectorNameAndClusterId    = esBucket.getUnusedMap().get(KEY).toString();
                        Object value = esBucket.getAggrMap().get(HIST).getBucketList().get(0).getAggrMap().get(metricName).getUnusedMap().get(VALUE);
                        if (value == null) {
                            return;
                        }
                        Double metricValue = Double.valueOf(value.toString());

                        Tuple<String, Long> tuple = splitConnectorNameAndClusterId(connectorNameAndClusterId);
                        if (null == tuple) {
                            return;
                        }

                        metricValueMap.putIfAbsent(metricName, new ArrayList<>());
                        metricValueMap.get(metricName).add(new Triple<>(tuple.getV2(), tuple.getV1(), metricValue));
                    }
                } catch (Exception e) {
                    LOGGER.error("method=handleTopConnectorESQueryResponse||metricName={}||errMsg=exception!", metricName, e);
                }
            } );
        }

        //2、对每个指标的connector按照指标值排序，并截取前topN个connectors
        for(Map.Entry<String, List<Triple<Long, String, Double>>> entry : metricValueMap.entrySet()){
            entry.getValue().sort((o1, o2) -> {
                if(null == o1 || null == o2) {
                    return 0;
                }

                return o2.v3().compareTo(o1.v3());
            } );

            List<Triple<Long, String, Double>> temp = (entry.getValue().size() > topN) ? entry.getValue().subList(0, topN) : entry.getValue();

            List<Tuple<Long, String>> connectorList = new ArrayList<>();
            for (Triple<Long, String, Double> triple: temp) {
                connectorList.add(new Tuple<>(triple.v1(), triple.v2()));
            }

            ret.put(entry.getKey(), connectorList);
        }

        return ret;
    }

    private Tuple<String, Long> splitConnectorNameAndClusterId(String connectorNameAndClusterId){
        String[] ss = connectorNameAndClusterId.split("#");
        if(null == ss || ss.length != 2) {
            return null;
        }

        return new Tuple<>(ss[0], Long.valueOf(ss[1]));
    }
}
