package com.xiaojukeji.know.streaming.km.persistence.es.dao;

import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.PartitionMetricPO;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslConstant;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import java.util.List;

import static com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant.PARTITION_INDEX;

/**
 * @author didi
 */
@Component
public class PartitionMetricESDAO extends BaseMetricESDAO {

    @PostConstruct
    public void init() {
        super.indexName = PARTITION_INDEX;
        checkCurrentDayIndexExist();
        register(this);
    }

    public PartitionMetricPO getPartitionLatestMetrics(Long clusterPhyId, String topic,
                                                       Integer brokerId, Integer partitionId,
                                                       List<String> metricNames){
        Long endTime    = getLatestMetricTime();
        Long startTime  = endTime - FIVE_MIN;

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.GET_PARTITION_LATEST_METRICS, clusterPhyId, topic, brokerId, partitionId, startTime, endTime);

        PartitionMetricPO partitionMetricPO = esOpClient.performRequestAndTakeFirst(
                partitionId.toString(), realIndex(startTime, endTime), dsl, PartitionMetricPO.class);

        return (null == partitionMetricPO) ? new PartitionMetricPO(clusterPhyId, topic, brokerId, partitionId)
                                           : filterMetrics(partitionMetricPO, metricNames);
    }

    public List<PartitionMetricPO> listPartitionLatestMetricsByTopic(Long clusterPhyId, String topic, List<String> metricNames){
        Long endTime    = getLatestMetricTime();
        Long startTime  = endTime - FIVE_MIN;

        String dsl = dslLoaderUtil.getFormatDslByFileName(
                DslConstant.LIST_PARTITION_LATEST_METRICS_BY_TOPIC, clusterPhyId, topic, startTime, endTime);

        List<PartitionMetricPO> partitionMetricPOS = esOpClient.performRequest(
                realIndex(startTime, endTime), dsl, PartitionMetricPO.class);

        return filterMetrics(partitionMetricPOS, metricNames);
    }
}
