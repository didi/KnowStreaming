package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.ReplicationMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.VALUE;
import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.REPLICATION_INDEX;

/**
 * @author didi
 */
@Component
public class ReplicationMetricESDAO extends BaseMetricESDAO {

    @PostConstruct
    public void init() {
        super.indexName = REPLICATION_INDEX;
        checkCurrentDayIndexExist();
        register(this);
    }

    /**
     * 获取集群 clusterId 中 brokerId 最新的统计指标
     */
    public ReplicationMetricPO getReplicationLatestMetrics(Long clusterPhyId, Integer brokerId, String topic,
                                                           Integer partitionId, List<String> metricNames){
        Long endTime   = getLatestMetricTime();
        Long startTime = endTime - FIVE_MIN;

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_REPLICATION_LATEST_METRICS, clusterPhyId, brokerId, topic, partitionId, startTime, endTime);

        ReplicationMetricPO replicationMetricPO = esOpClient.performRequestAndTakeFirst(
                realIndex(startTime, endTime), dsl, ReplicationMetricPO.class);

        return (null == replicationMetricPO) ? new ReplicationMetricPO(clusterPhyId, topic, brokerId, partitionId)
                                             : filterMetrics(replicationMetricPO, metricNames);
    }

    /**
     * 获取集群 clusterPhyId 中每个 metric 的指定 partitionId 在指定时间[startTime、endTime]区间内聚合计算(avg、max)之后的统计值
     */
    public Map<String/*metric*/, MetricPointVO> getReplicationMetricsPoint(Long clusterPhyId, String topic,
                                                                      Integer brokerId, Integer partitionId, List<String> metrics,
                                                                      String aggType, Long startTime, Long endTime){
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、构造agg查询条件
        String aggDsl   = buildAggsDSL(metrics, aggType);

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_REPLICATION_AGG_SINGLE_METRICS, clusterPhyId, brokerId,topic, partitionId, startTime, endTime, aggDsl);

        return esOpClient.performRequestWithRouting(String.valueOf(brokerId), realIndex, dsl,
                s -> handleSingleESQueryResponse(s, metrics, aggType), 3);
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
}
