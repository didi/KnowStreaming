package com.xiaojukeji.kafka.manager.service.collector;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.MetricsType;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时采集监控broker数据任务的执行
 * @author limeng
 * @date 2018/5/6.
 */
public class CollectBrokerMetricsTask extends BaseCollectTask {
    private final static Logger logger = LoggerFactory.getLogger(Constant.COLLECTOR_METRICS_LOGGER);

    private JmxService jmxService;

    public CollectBrokerMetricsTask(Long clusterId, JmxService jmxService) {
        super(logger, clusterId);
        this.clusterId = clusterId;
        this.jmxService = jmxService;
    }

    @Override
    public void collect() {
        ClusterDO clusterDO = ClusterMetadataManager.getClusterFromCache(clusterId);
        if (clusterDO == null) {
            return;
        }
        List<BrokerMetrics> brokerMetricsList = new ArrayList<>();
        List<Integer> brokerIdList = ClusterMetadataManager.getBrokerIdList(clusterId);
        for (Integer brokerId: brokerIdList) {
            BrokerMetrics brokerMetrics = jmxService.getSpecifiedBrokerMetricsFromJmx(clusterId, brokerId, BrokerMetrics.getFieldNameList(MetricsType.BROKER_TO_DB_METRICS), true);
            if (brokerMetrics == null) {
                continue;
            }
            brokerMetricsList.add(brokerMetrics);
        }
        KafkaMetricsCache.putBrokerMetricsToCache(clusterId, brokerMetricsList);
    }
}
