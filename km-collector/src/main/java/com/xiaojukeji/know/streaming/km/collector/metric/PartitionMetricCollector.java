package com.xiaojukeji.know.streaming.km.collector.metric;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.PartitionMetricEvent;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_PARTITION;

/**
 * @author didi
 */
@Component
public class PartitionMetricCollector extends AbstractMetricCollector<PartitionMetrics> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private PartitionMetricService partitionMetricService;

    @Autowired
    private TopicService topicService;

    @Override
    public void collectMetrics(ClusterPhy clusterPhy) {
        Long        startTime           =   System.currentTimeMillis();
        Long        clusterPhyId        =   clusterPhy.getId();
        List<Topic> topicList           =   topicService.listTopicsFromCacheFirst(clusterPhyId);
        List<VersionControlItem> items  =   versionControlService.listVersionControlItem(clusterPhyId, collectorType().getCode());

        // 获取集群所有分区

        FutureWaitUtil<Void> future = this.getFutureUtilByClusterPhyId(clusterPhyId);

        Map<String, Map<Integer, PartitionMetrics>> metricsMap = new ConcurrentHashMap<>();
        for (Topic topic : topicList) {
            metricsMap.put(topic.getTopicName(), new ConcurrentHashMap<>());

            future.runnableTask(
                    String.format("method=PartitionMetricCollector||clusterPhyId=%d||topicName=%s", clusterPhyId, topic.getTopicName()),
                    30000,
                    () -> collectMetrics(clusterPhyId, topic.getTopicName(), metricsMap.get(topic.getTopicName()), items)
            );
        }

        future.waitExecute(30000);

        List<PartitionMetrics> metricsList = new ArrayList<>();
        metricsMap.values().forEach(elem -> metricsList.addAll(elem.values()));

        this.publishMetric(new PartitionMetricEvent(this, metricsList));

        LOGGER.info(
                "method=PartitionMetricCollector||clusterPhyId={}||startTime={}||costTime={}||msg=collect finished.",
                clusterPhyId, startTime, System.currentTimeMillis() - startTime
        );
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_PARTITION;
    }

    /**************************************************** private method ****************************************************/

    private void collectMetrics(Long clusterPhyId, String topicName, Map<Integer, PartitionMetrics> metricsMap, List<VersionControlItem> items) {
        Set<String> collectedMetricsNameSet = new HashSet<>();
        for (VersionControlItem v : items) {
            try {
                if (collectedMetricsNameSet.contains(v.getName())) {
                    // 指标已存在
                    continue;
                }
                collectedMetricsNameSet.add(v.getName());

                Result<List<PartitionMetrics>> ret = partitionMetricService.collectPartitionsMetricsFromKafkaWithCache(
                        clusterPhyId,
                        topicName,
                        v.getName()
                );
                if (null == ret || ret.failed() || null == ret.getData() || ret.getData().isEmpty()) {
                    continue;
                }

                // 记录已经采集的指标
                collectedMetricsNameSet.addAll(ret.getData().get(0).getMetrics().keySet());

                // 放到map中
                for (PartitionMetrics subMetrics: ret.getData()) {
                    metricsMap.putIfAbsent(subMetrics.getPartitionId(), subMetrics);
                    PartitionMetrics allMetrics = metricsMap.get(subMetrics.getPartitionId());
                    allMetrics.putMetric(subMetrics.getMetrics());
                }

                if (!EnvUtil.isOnline()) {
                    LOGGER.info(
                            "class=PartitionMetricCollector||method=collectMetrics||clusterPhyId={}||topicName={}||metricName={}||metricValue={}!",
                            clusterPhyId, topicName, v.getName(), ConvertUtil.obj2Json(ret.getData())
                    );
                }

            } catch (Exception e) {
                LOGGER.info(
                        "class=PartitionMetricCollector||method=collectMetrics||clusterPhyId={}||topicName={}||metricName={}||errMsg=exception",
                        clusterPhyId, topicName, v.getName(), e
                );
            }
        }
    }
}
