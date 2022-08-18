package com.xiaojukeji.know.streaming.km.collector.metric;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.*;
import com.xiaojukeji.know.streaming.km.common.bean.po.BaseESPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.*;
import com.xiaojukeji.know.streaming.km.common.enums.metric.KafkaMetricIndexEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import com.xiaojukeji.know.streaming.km.common.utils.NamedThreadFactory;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.BaseMetricESDAO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class MetricESSender implements ApplicationListener<BaseMetricEvent> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    private static final int    THRESHOLD = 100;

    private ThreadPoolExecutor esExecutor = new ThreadPoolExecutor(10, 20, 6000, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(1000),
            new NamedThreadFactory("KM-Collect-MetricESSender-ES"),
            (r, e) -> LOGGER.warn("class=MetricESSender||msg=KM-Collect-MetricESSender-ES Deque is blocked, taskCount:{}" + e.getTaskCount()));

    @PostConstruct
    public void init(){
        LOGGER.info("class=MetricESSender||method=init||msg=init finished");
    }

    @Override
    public void onApplicationEvent(BaseMetricEvent event) {
        if(event instanceof BrokerMetricEvent){
            BrokerMetricEvent brokerMetricEvent = (BrokerMetricEvent)event;
            send2es(KafkaMetricIndexEnum.BROKER_INFO,
                    ConvertUtil.list2List(brokerMetricEvent.getBrokerMetrics(), BrokerMetricPO.class));

        }else if(event instanceof ClusterMetricEvent){
            ClusterMetricEvent clusterMetricEvent = (ClusterMetricEvent)event;
            send2es(KafkaMetricIndexEnum.CLUSTER_INFO,
                    ConvertUtil.list2List(clusterMetricEvent.getClusterMetrics(), ClusterMetricPO.class));

        }else if(event instanceof TopicMetricEvent){
            TopicMetricEvent topicMetricEvent = (TopicMetricEvent)event;
            send2es(KafkaMetricIndexEnum.TOPIC_INFO,
                    ConvertUtil.list2List(topicMetricEvent.getTopicMetrics(), TopicMetricPO.class));

        }else if(event instanceof PartitionMetricEvent){
            PartitionMetricEvent partitionMetricEvent = (PartitionMetricEvent)event;
            send2es(KafkaMetricIndexEnum.PARTITION_INFO,
                    ConvertUtil.list2List(partitionMetricEvent.getPartitionMetrics(), PartitionMetricPO.class));

        }else if(event instanceof GroupMetricEvent){
            GroupMetricEvent groupMetricEvent = (GroupMetricEvent)event;
            send2es(KafkaMetricIndexEnum.GROUP_INFO,
                    ConvertUtil.list2List(groupMetricEvent.getGroupMetrics(), GroupMetricPO.class));

        }else if(event instanceof ReplicaMetricEvent){
            ReplicaMetricEvent replicaMetricEvent = (ReplicaMetricEvent)event;
            send2es(KafkaMetricIndexEnum.REPLICATION_INFO,
                    ConvertUtil.list2List(replicaMetricEvent.getReplicationMetrics(), ReplicationMetricPO.class));
        }
    }

    /**
     * 根据不同监控维度来发送
     *
     * @param stats
     * @param statsList
     * @return
     */
    private boolean send2es(KafkaMetricIndexEnum stats, List<? extends BaseESPO> statsList){
        if (CollectionUtils.isEmpty(statsList)) {
            return true;
        }

        if (!EnvUtil.isOnline()) {
            LOGGER.info("class=MetricESSender||method=send2es||ariusStats={}||size={}",
                    stats.getIndex(), statsList.size());
        }

        BaseMetricESDAO baseMetricESDao = BaseMetricESDAO.getByStatsType(stats);
        if (Objects.isNull( baseMetricESDao )) {
            LOGGER.error("class=MetricESSender||method=send2es||errMsg=fail to find {}", stats.getIndex());
            return false;
        }

        int size = statsList.size();
        int num  = (size) % THRESHOLD == 0 ? (size / THRESHOLD) : (size / THRESHOLD + 1);

        if (size < THRESHOLD) {
            esExecutor.execute(() ->
                    baseMetricESDao.batchInsertStats(statsList));
            return true;
        }

        for (int i = 1; i < num + 1; i++) {
            int end   = (i * THRESHOLD) > size ? size : (i * THRESHOLD);
            int start = (i - 1) * THRESHOLD;

            esExecutor.execute(() ->
                    baseMetricESDao.batchInsertStats(statsList.subList(start, end)));
        }

        return true;
    }
}
