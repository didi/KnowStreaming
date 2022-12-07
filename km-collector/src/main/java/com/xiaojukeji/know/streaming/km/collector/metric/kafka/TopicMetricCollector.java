package com.xiaojukeji.know.streaming.km.collector.metric.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.TopicMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_TOPIC;

/**
 * @author didi
 */
@Component
public class TopicMetricCollector extends AbstractKafkaMetricCollector<TopicMetrics> {
    protected static final ILog  LOGGER = LogFactory.getLog(TopicMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicMetricService topicMetricService;

    private static final Integer AGG_METRICS_BROKER_ID = -10000;

    @Override
    public List<TopicMetrics> collectKafkaMetrics(ClusterPhy clusterPhy) {
        Long        clusterPhyId        =   clusterPhy.getId();
        List<Topic> topics              =   topicService.listTopicsFromCacheFirst(clusterPhyId);
        List<VersionControlItem> items  =   versionControlService.listVersionControlItem(this.getClusterVersion(clusterPhy), collectorType().getCode());

        FutureWaitUtil<Void> future = this.getFutureUtilByClusterPhyId(clusterPhyId);

        Map<String/*Topic名称*/, Map<Integer/*BrokerId*/, TopicMetrics/*metrics*/>> allMetricsMap = new ConcurrentHashMap<>();

        for(Topic topic : topics) {
            Map<Integer, TopicMetrics> metricsMap = new ConcurrentHashMap<>();
            metricsMap.put(AGG_METRICS_BROKER_ID, new TopicMetrics(topic.getTopicName(), clusterPhyId));
            metricsMap.get(AGG_METRICS_BROKER_ID).putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);

            allMetricsMap.put(topic.getTopicName(), metricsMap);

            future.runnableTask(
                    String.format("class=TopicMetricCollector||clusterPhyId=%d||topicName=%s", clusterPhyId, topic.getTopicName()),
                    30000,
                    () -> collectMetrics(clusterPhyId, topic.getTopicName(), metricsMap, items)
            );
        }

        future.waitExecute(30000);

        List<TopicMetrics> metricsList = new ArrayList<>();
        allMetricsMap.values().forEach(elem -> metricsList.addAll(elem.values()));

        this.publishMetric(new TopicMetricEvent(this, metricsList));

        return metricsList;
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_TOPIC;
    }

    /**************************************************** private method ****************************************************/

    private void collectMetrics(Long clusterPhyId, String topicName, Map<Integer, TopicMetrics> metricsMap, List<VersionControlItem> items) {
        long startTime = System.currentTimeMillis();

        TopicMetrics aggMetrics = metricsMap.get(AGG_METRICS_BROKER_ID);
        for (VersionControlItem v : items) {
            try {
                if (aggMetrics.getMetrics().containsKey(v.getName())) {
                    // 如果已经有该指标，则直接continue
                    continue;
                }

                Result<List<TopicMetrics>> ret = topicMetricService.collectTopicMetricsFromKafkaWithCacheFirst(clusterPhyId, topicName, v.getName());
                if (null == ret || ret.failed() || ValidateUtils.isEmptyList(ret.getData())) {
                    // 返回为空、错误、无数据的情况下，直接跳过
                    continue;
                }

                // 记录数据
                ret.getData().stream().forEach(metrics -> {
                    if (metrics.isBBrokerAgg()) {
                        aggMetrics.putMetric(metrics.getMetrics());
                    } else {
                        metricsMap.putIfAbsent(
                                metrics.getBrokerId(),
                                new TopicMetrics(topicName, clusterPhyId, metrics.getBrokerId(), false)
                        );

                        metricsMap.get(metrics.getBrokerId()).putMetric(metrics.getMetrics());
                    }
                });
            } catch (Exception e) {
                LOGGER.error(
                        "method=collectMetrics||clusterPhyId={}||topicName={}||metricName={}||errMsg=exception!",
                        clusterPhyId, topicName, v.getName(), e
                );
            }
        }

        // 记录采集性能
        aggMetrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);
    }
}
