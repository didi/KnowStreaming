package com.xiaojukeji.kafka.manager.task.dispatch.metrics.store;

import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.events.TopicMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * Topic社区指标存储
 * @author zengqiao
 * @date 20/7/21
 */
@CustomScheduled(name = "storeCommunityTopicMetrics", cron = "31 0/1 * * * ?", threadNum = 5)
public class StoreCommunityTopicMetrics extends AbstractScheduledTask<ClusterDO> {
    @Autowired
    private JmxService jmxService;

    @Autowired
    private ClusterService clusterService;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        List<TopicMetrics> metricsList = getTopicMetrics(clusterDO.getId());
        SpringTool.publish(new TopicMetricsCollectedEvent(this, clusterDO.getId(), metricsList));
    }

    private List<TopicMetrics> getTopicMetrics(Long clusterId) {
        List<TopicMetrics> metricsList =
                jmxService.getTopicMetrics(clusterId, KafkaMetricsCollections.TOPIC_METRICS_TO_DB, true);
        if (ValidateUtils.isEmptyList(metricsList)) {
            KafkaMetricsCache.putTopicMetricsToCache(clusterId, new ArrayList<>());
            return new ArrayList<>();
        }
        KafkaMetricsCache.putTopicMetricsToCache(clusterId, metricsList);
        return metricsList;
    }
}
