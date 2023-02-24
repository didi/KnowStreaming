package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.didiglobal.logi.elasticsearch.client.response.query.query.ESQueryResponse;
import com.didiglobal.logi.elasticsearch.client.response.query.query.aggs.ESAggr;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.ESConstant;
import com.xiaojukeji.know.streaming.km.common.utils.MetricsUtils;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaojukeji.know.streaming.km.common.constant.ESConstant.*;
import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.ZOOKEEPER_INDEX;

@Component
public class ZookeeperMetricESDAO extends BaseMetricESDAO {

    @PostConstruct
    public void init() {
        super.indexName = ZOOKEEPER_INDEX;
        checkCurrentDayIndexExist();
        register(this);
    }

    /**
     * 获取指定集群，指定指标，一段时间内的值
     */
    public Map<String/*metricName*/, List<MetricPointVO>> listMetricsByClusterPhyId(Long clusterPhyId,
                                                                                    List<String> metricNameList,
                                                                                    String aggType,
                                                                                    Long startTime,
                                                                                    Long endTime) {
        //1、获取需要查下的索引
        String realIndex = realIndex(startTime, endTime);

        //2、根据查询的时间区间大小来确定指标点的聚合区间大小
        String interval = MetricsUtils.getInterval(endTime - startTime);

        //3、构造agg查询条件
        String aggDsl   = buildAggsDSL(metricNameList, aggType);

        //4、构造dsl查询条件，开始查询
        try {
            String dsl = dslLoaderUtil.getFormatDslByFileName(
                    DslConstant.GET_ZOOKEEPER_AGG_LIST_METRICS, clusterPhyId, startTime, endTime, interval, aggDsl);

            return esOpClient.performRequestWithRouting(
                    String.valueOf(clusterPhyId),
                    realIndex,
                    dsl,
                    s -> handleListESQueryResponse(s, metricNameList, aggType),
                    ESConstant.DEFAULT_RETRY_TIME
            );
        } catch (Exception e){
            LOGGER.error("method=listMetricsByClusterPhyId||clusterPhyId={}||errMsg=exception!",
                    clusterPhyId, e
            );
        }

        return new HashMap<>();
    }

    /**************************************************** private method ****************************************************/

    private Map<String, List<MetricPointVO>> handleListESQueryResponse(ESQueryResponse response, List<String> metrics, String aggType){
        Map<String, ESAggr> esAggrMap = checkBucketsAndHitsOfResponseAggs(response);
        if(null == esAggrMap) {
            return new HashMap<>();
        }

        Map<String, List<MetricPointVO>> metricMap = new HashMap<>();
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
                    }
                }catch (Exception e){
                    LOGGER.error("method=handleESQueryResponse||metric={}||errMsg=exception!", metric, e);
                }
            } );

            metricMap.put(metric, optimizeMetricPoints(metricPoints));
        }

        return metricMap;
    }
}
