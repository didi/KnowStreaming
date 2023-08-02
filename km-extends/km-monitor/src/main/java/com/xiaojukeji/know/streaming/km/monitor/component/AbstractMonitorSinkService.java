package com.xiaojukeji.know.streaming.km.monitor.component;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.*;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.connect.ConnectorMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.*;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.connect.ConnectorMetricEvent;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.monitor.common.MetricSinkPoint;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xiaojukeji.know.streaming.km.monitor.common.MonitorSinkTagEnum.*;

public abstract class AbstractMonitorSinkService implements ApplicationListener<BaseMetricEvent> {
    protected static final ILog LOGGER              = LogFactory.getLog(AbstractMonitorSinkService.class);

    private static final int    STEP                = 60;

    private static final FutureUtil<Void> sinkTP    = FutureUtil.init(
            "SinkMetricsTP",
            5,
            5,
            10000
    );

    /**
     * monitor 服务的名称
     * @return
     */
    protected abstract String monitorName();

    @Override
    public void onApplicationEvent(BaseMetricEvent event) {
        sinkTP.submitTask(() -> {
            if (event instanceof BrokerMetricEvent) {
                BrokerMetricEvent brokerMetricEvent = (BrokerMetricEvent)event;
                sinkMetrics(brokerMetric2SinkPoint(brokerMetricEvent.getBrokerMetrics()));

            } else if(event instanceof ClusterMetricEvent) {
                ClusterMetricEvent clusterMetricEvent = (ClusterMetricEvent)event;
                sinkMetrics(clusterMetric2SinkPoint(clusterMetricEvent.getClusterMetrics()));

            } else if(event instanceof TopicMetricEvent) {
                TopicMetricEvent topicMetricEvent = (TopicMetricEvent)event;
                sinkMetrics(topicMetric2SinkPoint(topicMetricEvent.getTopicMetrics()));

            } else if(event instanceof PartitionMetricEvent) {
                PartitionMetricEvent   partitionMetricEvent = (PartitionMetricEvent)event;
                sinkMetrics(partitionMetric2SinkPoint(partitionMetricEvent.getPartitionMetrics()));

            } else if(event instanceof GroupMetricEvent) {
                GroupMetricEvent groupMetricEvent = (GroupMetricEvent)event;
                sinkMetrics(groupMetric2SinkPoint(groupMetricEvent.getGroupMetrics()));

            } else if(event instanceof ZookeeperMetricEvent) {
                ZookeeperMetricEvent     zookeeperMetricEvent = (ZookeeperMetricEvent)event;
                sinkMetrics(zookeeperMetric2SinkPoint(zookeeperMetricEvent.getZookeeperMetrics()));

            } else if (event instanceof ConnectorMetricEvent) {
                ConnectorMetricEvent     connectorMetricEvent = (ConnectorMetricEvent)event;
                sinkMetrics(connectConnectorMetric2SinkPoint(connectorMetricEvent.getConnectorMetricsList()));
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
            tagsMap.put(TOPIC.getName(),          p.getTopic());

            pointList.addAll(genSinkPoint("Partition", p.getMetrics(), p.getTimestamp(), tagsMap));
        }

        return pointList;
    }

    private List<MetricSinkPoint> groupMetric2SinkPoint(List<GroupMetrics> groupMetrics){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(GroupMetrics g : groupMetrics){
            if(g.isBGroupMetric()){
                // Group 指标
                Map<String, Object> tagsMap = new HashMap<>();
                tagsMap.put(CLUSTER_ID.getName(),     g.getClusterPhyId());
                tagsMap.put(CONSUMER_GROUP.getName(), g.getGroup());

                pointList.addAll(genSinkPoint("Group", g.getMetrics(), g.getTimestamp(), tagsMap));
            } else {
                // Group + Topic + Partition指标
                Map<String, Object> tagsMap = new HashMap<>();
                tagsMap.put(CLUSTER_ID.getName(),       g.getClusterPhyId());
                tagsMap.put(CONSUMER_GROUP.getName(),   g.getGroup());
                tagsMap.put(TOPIC.getName(),            g.getTopic());
                tagsMap.put(PARTITION_ID.getName(),     g.getPartitionId());

                pointList.addAll(genSinkPoint("Group_Topic_Partition", g.getMetrics(), g.getTimestamp(), tagsMap));
            }
        }

        return pointList;
    }

    private List<MetricSinkPoint> zookeeperMetric2SinkPoint(List<ZookeeperMetrics> zookeeperMetricsList){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(ZookeeperMetrics z : zookeeperMetricsList){
            Map<String, Object> tagsMap = new HashMap<>();
            tagsMap.put(CLUSTER_ID.getName(),     z.getClusterPhyId());

            pointList.addAll(genSinkPoint("Zookeeper", z.getMetrics(), z.getTimestamp(), tagsMap));
        }

        return pointList;
    }

    private List<MetricSinkPoint> connectConnectorMetric2SinkPoint(List<ConnectorMetrics> connectorMetricsList){
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(ConnectorMetrics metrics : connectorMetricsList){
            Map<String, Object> tagsMap = new HashMap<>();
            tagsMap.put(CLUSTER_ID.getName(),               metrics.getClusterPhyId());
            tagsMap.put(CONNECT_CLUSTER_ID.getName(),       metrics.getConnectClusterId());
            tagsMap.put(CONNECT_CONNECTOR.getName(),        metrics.getConnectorName());

            pointList.addAll(genSinkPoint("ConnectConnector", metrics.getMetrics(), metrics.getTimestamp(), tagsMap));
        }

        return pointList;
    }

    private List<MetricSinkPoint> genSinkPoint(String metricPre,
                                               Map<String, Float> metrics,
                                               long timeStamp,
                                               Map<String, Object> tagsMap) {
        List<MetricSinkPoint> pointList = new ArrayList<>();

        for(Map.Entry<String, Float> entry: metrics.entrySet()){
            MetricSinkPoint metricSinkPoint = new MetricSinkPoint();
            metricSinkPoint.setName(metricPre + "_" + entry.getKey());
            metricSinkPoint.setValue(entry.getValue());
            metricSinkPoint.setTimestamp(timeStamp);
            metricSinkPoint.setStep(STEP);
            metricSinkPoint.setTagsMap(tagsMap);
            pointList.add(metricSinkPoint);
        }

        return pointList;
    }
}
