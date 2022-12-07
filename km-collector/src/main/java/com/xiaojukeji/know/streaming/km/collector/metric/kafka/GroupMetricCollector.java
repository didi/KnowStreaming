package com.xiaojukeji.know.streaming.km.collector.metric.kafka;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.GroupMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.GroupMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupMetricService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_GROUP;

/**
 * @author didi
 */
@Component
public class GroupMetricCollector extends AbstractKafkaMetricCollector<GroupMetrics> {
    protected static final ILog  LOGGER = LogFactory.getLog(GroupMetricCollector.class);

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private GroupMetricService groupMetricService;

    @Autowired
    private GroupService groupService;

    @Override
    public List<GroupMetrics> collectKafkaMetrics(ClusterPhy clusterPhy) {
        Long   clusterPhyId     = clusterPhy.getId();

        List<String> groupNameList = new ArrayList<>();
        try {
            groupNameList = groupService.listGroupsFromKafka(clusterPhy);
        } catch (Exception e) {
            LOGGER.error("method=collectKafkaMetrics||clusterPhyId={}||msg=exception!", clusterPhyId, e);
        }

        if(ValidateUtils.isEmptyList(groupNameList)) {
            return Collections.emptyList();
        }

        List<VersionControlItem> items = versionControlService.listVersionControlItem(this.getClusterVersion(clusterPhy), collectorType().getCode());

        FutureWaitUtil<Void> future = this.getFutureUtilByClusterPhyId(clusterPhyId);

        Map<String, List<GroupMetrics>> metricsMap = new ConcurrentHashMap<>();
        for(String groupName : groupNameList) {
            future.runnableTask(
                    String.format("class=GroupMetricCollector||clusterPhyId=%d||groupName=%s", clusterPhyId, groupName),
                    30000,
                    () -> collectMetrics(clusterPhyId, groupName, metricsMap, items));
        }

        future.waitResult(30000);

        List<GroupMetrics> metricsList = metricsMap.values().stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

        publishMetric(new GroupMetricEvent(this, metricsList));
        return metricsList;
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_GROUP;
    }

    /**************************************************** private method ****************************************************/

    private void collectMetrics(Long clusterPhyId, String groupName, Map<String, List<GroupMetrics>> metricsMap, List<VersionControlItem> items) {
        long startTime = System.currentTimeMillis();

        Map<TopicPartition, GroupMetrics> subMetricMap = new HashMap<>();

        GroupMetrics groupMetrics = new GroupMetrics(clusterPhyId, groupName, true);
        groupMetrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);

        for(VersionControlItem v : items) {
            try {
                String metricName = v.getName();

                Result<List<GroupMetrics>> ret = groupMetricService.collectGroupMetricsFromKafka(clusterPhyId, groupName, metricName);
                if(null == ret || ret.failed() || ValidateUtils.isEmptyList(ret.getData())) {
                    continue;
                }

                ret.getData().forEach(metrics -> {
                    if (metrics.isBGroupMetric()) {
                        groupMetrics.putMetric(metrics.getMetrics());
                        return;
                    }

                    TopicPartition tp  = new TopicPartition(metrics.getTopic(), metrics.getPartitionId());
                    subMetricMap.putIfAbsent(tp, new GroupMetrics(clusterPhyId, metrics.getPartitionId(), metrics.getTopic(), groupName, false));
                    subMetricMap.get(tp).putMetric(metrics.getMetrics());
                });
            } catch (Exception e) {
                LOGGER.error(
                        "method=collectMetrics||clusterPhyId={}||groupName={}||errMsg=exception!",
                        clusterPhyId, groupName, e
                );
            }
        }

        List<GroupMetrics> metricsList = new ArrayList<>();
        metricsList.add(groupMetrics);
        metricsList.addAll(subMetricMap.values());

        // 记录采集性能
        groupMetrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

        metricsMap.put(groupName, metricsList);
    }
}
