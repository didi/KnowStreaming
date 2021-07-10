package com.xiaojukeji.kafka.manager.task.dispatch.metrics.collect;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ClusterService;
import com.xiaojukeji.kafka.manager.service.service.ThrottleService;
import com.xiaojukeji.kafka.manager.task.common.TopicThrottledMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.task.component.AbstractScheduledTask;
import com.xiaojukeji.kafka.manager.task.component.CustomScheduled;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author zengqiao
 * @date 2019-05-10
 */
@CustomScheduled(name = "collectAndPublishTopicThrottledMetrics", cron = "11 0/1 * * * ?", threadNum = 5)
public class CollectAndPublishTopicThrottledMetrics extends AbstractScheduledTask<ClusterDO> {
    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ThrottleService throttleService;

    @Override
    protected List<ClusterDO> listAllTasks() {
        return clusterService.list();
    }

    @Override
    public void processTask(ClusterDO clusterDO) {
        long startTime = System.currentTimeMillis();

        List<TopicThrottledMetrics> metricsList = throttleService.getThrottledTopicsFromJmx(
                clusterDO.getId(),
                new HashSet<>(PhysicalClusterMetadataManager.getBrokerIdList(clusterDO.getId())),
                Arrays.asList(KafkaClientEnum.values())
        );
        if (ValidateUtils.isNull(metricsList)) {
            return;
        }

        SpringTool.publish(new TopicThrottledMetricsCollectedEvent(this, startTime, metricsList));
    }
}
