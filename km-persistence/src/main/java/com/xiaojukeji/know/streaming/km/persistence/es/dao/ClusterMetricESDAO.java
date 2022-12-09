package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchShould;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchRange;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchSort;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ClusterMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.utils.MetricsUtils;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;
import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.CLUSTER_INDEX;

@Component
public class ClusterMetricESDAO extends BaseMetricESDAO {

    @PostConstruct
    public void init() {
        super.indexName = CLUSTER_INDEX;
        checkCurrentDayIndexExist();
        register(this);
    }

    /**
     * 获取集群 clusterId 最新的统计指标
     */
    public ClusterMetricPO getClusterLatestMetrics(Long clusterId, List<String> metricNames){
        Long endTime   = getLatestMetricTime(clusterId);
        Long startTime = endTime - FIVE_MIN;

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_CLUSTER_LATEST_METRICS, clusterId, startTime, endTime);

        ClusterMetricPO clusterMetricPO = esOpClient.performRequestAndTakeFirst(
                clusterId.toString(), realIndex(startTime, endTime), dsl, ClusterMetricPO.class);

        return (null == clusterMetricPO) ? new ClusterMetricPO(clusterId)
                                         : filterMetrics(clusterMetricPO, metricNames);
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 在指定时间[startTime、endTime]区间内聚合计算(avg、max)之后的统计值
     */
    public Map<String/*metric*/, MetricPointVO> getClusterMetricsPoint(Long clusterPhyId, List<String> metrics,
                                                                      String aggType, Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_CLUSTER_AGG_SINGLE_METRICS, clusterPhyId, startTime, endTime, aggDsl);

        return esOpClient.performRequestWithRouting(String.valueOf(clusterPhyId), realIndex, dsl,
                s -> handleSingleESQueryResponse(s, metrics, aggType), 3);
    }

    /**
     * 获取某个 metric 的排序后的分页集群 ids
     */
    public List<ClusterMetricPO> pagingClusterWithLatestMetrics(List<SearchTerm> terms, List<SearchShould> shoulds,
                                                                SearchSort sort, SearchRange range){
        Long latestMetricTime   = getLatestMetricTime();
        Long startTime          = latestMetricTime - FIVE_MIN;

        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, latestMetricTime);

        String sortDsl      = buildSortDsl(sort, SearchSort.DEFAULT);
        String rangeDsl     = buildRangeDsl(range);
        String termDsl      = buildTermsDsl(terms);
        String shouldDsl    = buildShouldDsl(shoulds);

        StringBuilder appendQueryDsl = new StringBuilder();
        if(!StringUtils.isEmpty(rangeDsl)){
            appendQueryDsl.append(",").append(rangeDsl);
        }

        if(!StringUtils.isEmpty(termDsl)){
            appendQueryDsl.append(",").append(termDsl);
        }

        if(!StringUtils.isEmpty(shouldDsl)){
            appendQueryDsl.append(",").append(shouldDsl);
        }

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.LIST_CLUSTER_WITH_LATEST_METRICS, latestMetricTime, appendQueryDsl.toString(), sortDsl);

        return esOpClient.performRequest(realIndex, dsl, ClusterMetricPO.class);
    }

    /**
     * 获取一批集群 clusterPhyIds 一段时间内 metrics 的指标值
     */
    public Table<String/*metric*/, Long/*clusterId*/, List<MetricPointVO>> listClusterMetricsByClusterIds(List<String> metrics,
                                                                                                          String aggType, List<Long> clusterPhyIds,
                                                                                                          Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        final Table<String, Long, List<MetricPointVO>> table = HashBasedTable.create();

        //4、构造dsl查询条件，开始查询
        for(Long clusterPhyId : clusterPhyIds){
            try {
                esTPService.submitSearchTask(
                        String.format("class=ClusterMetricESDAO||method=listClusterMetricsByClusterIds||ClusterPhyId=%d", clusterPhyId),
                        5000,
                        () -> {
                            String dsl = dslLoaderUtil.getFormatDslByFileName(
                                    DslConstant.GET_CLUSTER_AGG_LIST_METRICS,
                                    clusterPhyId,
                                    startTime,
                                    endTime,
                                    interval,
                                    aggDsl
                            );

                            Map<String/*metric*/, List<MetricPointVO>> metricMap = esOpClient.performRequestWithRouting(
                                    String.valueOf(clusterPhyId),
                                    realIndex,
                                    dsl,
                                    s -> handleListESQueryResponse(s, metrics, aggType),
                                    DEFAULT_RETRY_TIME
                            );

                            synchronized (table){
                                for(Map.Entry<String/*metric*/, List<MetricPointVO>> entry : metricMap.entrySet()){
                                    table.put(entry.getKey(), clusterPhyId, entry.getValue());
                                }
                            }
                        });
            }catch (Exception e){
                LOGGER.error("method=listClusterMetricsByClusterIds||clusterPhyId={}||errMsg=exception!", clusterPhyId, e);
            }
        }

        esTPService.waitExecute();
        return table;
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
        Map<String, ESAggr> esAggrMap = this.checkBucketsAndHitsOfResponseAggs(response);
        if(null == esAggrMap) {
            return new HashMap<>();
        }

        Map<String, List<MetricPointVO>> metricMap = new HashMap<>();
        for(String metricName : metricNameList) {
            List<MetricPointVO> metricPoints = new ArrayList<>();

            esAggrMap.get(HIST).getBucketList().forEach( esBucket -> {
                try {
                    if (null != esBucket.getUnusedMap().get(KEY)) {
                        Long    timestamp = Long.valueOf(esBucket.getUnusedMap().get(KEY).toString());
                        Object  value     = esBucket.getAggrMap().get(metricName).getUnusedMap().get(VALUE);
                        if(null == value) {
                            return;
                        }

                        metricPoints.add(new MetricPointVO(metricName, timestamp, value.toString(), aggType));
                    }
                } catch (Exception e){
                    LOGGER.error("method=handleListESQueryResponse||metricName={}||errMsg=exception!", metricName, e);
                }
            } );

            metricMap.put(metricName, optimizeMetricPoints(metricPoints));
        }

        return metricMap;
    }
}
