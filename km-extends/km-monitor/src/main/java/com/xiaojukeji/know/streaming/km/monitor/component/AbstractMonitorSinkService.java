package com.xiaojukeji.know.streaming.km.monitor.component;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.*;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.*;
import com.xiaojukeji.know.streaming.km.common.utils.NamedThreadFactory;
import com.xiaojukeji.know.streaming.km.monitor.common.MetricSinkPoint;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xiaojukeji.know.streaming.km.monitor.common.MonitorSinkTagEnum.*;

public abstract class AbstractMonitorSinkService implements ApplicationListener<BaseMetricEvent> {
    protected static final ILog LOGGER = LogFactory.getLog(AbstractMonitorSinkService.class);

    private static final int    STEP       = 60;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 6000, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(1000),
            new NamedThreadFactory("KM-Monitor-Sink-" + monitorName()),
            (r, e) -> LOGGER.warn("class=AbstractMonitorSinkService||msg=Deque is blocked, taskCount:{}" + e.getTaskCount()));

    /**
     * monitor 服务的名称
     * @return
     */
    protected abstract String monitorName();

    @Override
    public void onApplicationEvent(BaseMetricEvent event) {
        executor.execute( () -> {
            if(event instanceof BrokerMetricEvent){
                BrokerMetricEvent brokerMetricEvent = (BrokerMetricEvent)event;
                sinkMetrics(brokerMetric2SinkPoint(brokerMetricEvent.getBrokerMetrics()));

            }else if(event instanceof ClusterMetricEvent){
                ClusterMetricEvent clusterMetricEvent = (ClusterMetricEvent)event;
                sinkMetrics(clusterMetric2SinkPoint(clusterMetricEvent.getClusterMetrics()));

            }else if(event instanceof TopicMetricEvent){
                TopicMetricEvent topicMetricEvent = (TopicMetricEvent)event;
                sinkMetrics(topicMetric2SinkPoint(topicMetricEvent.getTopicMetrics()));

            }else if(event instanceof PartitionMetricEvent){
                PartitionMetricEvent   partitionMetricEvent = (PartitionMetricEvent)event;
                sinkMetrics(partitionMetric2SinkPoint(partitionMetricEvent.getPartitionMetrics()));

            }else if(event instanceof GroupMetricEvent){
                GroupMetricEvent groupMetricEvent = (GroupMetricEvent)event;
                sinkMetrics(groupMetric2SinkPoint(groupMetricEvent.getGroupMetrics()));

            }else if(event instanceof ReplicaMetricEvent){
                ReplicaMetricEvent       replicaMetricEvent = (ReplicaMetricEvent)event;
                sinkMetrics(replicationMetric2SinkPoint(replicaMetricEvent.getReplicationMetrics()));
            }
        } );
    }

    /**
     * 监控指标的上报和查询
     * @param pointList
     * @return
     */
    public abstract Boolean sinkMetrics(List<MetricSinkPoint> pointList);

    /**************************************************** private method ****************************************************/
    private List<MetricSinkPoint> brokerMetric2SinkPoint(List<BrokerMetrics> brokerMetrics){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(BrokerMetrics b : brokerMetrics){
            Map<String, Object> tagsMap = new HashMap<>();
            tagsMap.put(CLUSTER_ID.getName(), b.getClusterPhyId());
            tagsMap.put(BROKER_ID.getName(),  b.getBrokerId());

            pointList.addAll(genSinkPoint("Broker", b.getMetrics(), b.getTimestamp(), tagsMap));
        }

        return pointList;
    }

    private List<MetricSinkPoint> clusterMetric2SinkPoint(List<ClusterMetrics> clusterMetrics){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(ClusterMetrics c : clusterMetrics){
            Map<String, Object> tagsMap = new HashMap<>();
            tagsMap.put(CLUSTER_ID.getName(), c.getClusterPhyId());

            pointList.addAll(genSinkPoint("Cluster", c.getMetrics(), c.getTimestamp(), tagsMap));
        }

        return pointList;
    }

    private List<MetricSinkPoint> topicMetric2SinkPoint(List<TopicMetrics> topicMetrics){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(TopicMetrics t : topicMetrics){
            if(t.isBBrokerAgg()){
                Map<String, Object> tagsMap = new HashMap<>();
                tagsMap.put(CLUSTER_ID.getName(), t.getClusterPhyId());
                tagsMap.put(TOPIC.getName(),      t.getTopic());

                pointList.addAll(genSinkPoint("Topic", t.getMetrics(), t.getTimestamp(), tagsMap));
            }
        }

        return pointList;
    }

    private List<MetricSinkPoint> partitionMetric2SinkPoint(List<PartitionMetrics> partitionMetrics){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(PartitionMetrics p : partitionMetrics){
            Map<String, Object> tagsMap = new HashMap<>();
            tagsMap.put(CLUSTER_ID.getName(),     p.getClusterPhyId());
            tagsMap.put(BROKER_ID.getName(),      p.getBrokerId());
            tagsMap.put(PARTITION_ID.getName(),   p.getPartitionId());

            pointList.addAll(genSinkPoint("Partition", p.getMetrics(), p.getTimestamp(), tagsMap));
        }

        return pointList;
    }

    private List<MetricSinkPoint> groupMetric2SinkPoint(List<GroupMetrics> groupMetrics){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(GroupMetrics g : groupMetrics){
            if(g.isBGroupMetric()){
                Map<String, Object> tagsMap = new HashMap<>();
                tagsMap.put(CLUSTER_ID.getName(),     g.getClusterPhyId());
                tagsMap.put(CONSUMER_GROUP.getName(), g.getGroup());

                pointList.addAll(genSinkPoint("Group", g.getMetrics(), g.getTimestamp(), tagsMap));
            }
        }

        return pointList;
    }

    private List<MetricSinkPoint> replicationMetric2SinkPoint(List<ReplicationMetrics> replicationMetrics){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(ReplicationMetrics r : replicationMetrics){
            Map<String, Object> tagsMap = new HashMap<>();
            tagsMap.put(CLUSTER_ID.getName(),     r.getClusterPhyId());
            tagsMap.put(BROKER_ID.getName(),      r.getBrokerId());
            tagsMap.put(PARTITION_ID.getName(),   r.getPartitionId());

            pointList.addAll(genSinkPoint("Replication", r.getMetrics(), r.getTimestamp(), tagsMap));
        }

        return pointList;
    }

    private List<MetricSinkPoint> genSinkPoint(String metricPre, Map<String, Float> metrics,
                                               long timeStamp, Map<String, Object> tagsMap){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(String metricName : metrics.keySet()){
            MetricSinkPoint metricSinkPoint = new MetricSinkPoint();
            metricSinkPoint.setName(metricPre + "_" + metricName);
            metricSinkPoint.setValue(metrics.get(metricName));
            metricSinkPoint.setTimestamp(timeStamp);
            metricSinkPoint.setStep(STEP);
            metricSinkPoint.setTagsMap(tagsMap);
            pointList.add(metricSinkPoint);
        }

        return pointList;
    }
}
