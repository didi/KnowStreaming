package com.xiaojukeji.know.streaming.km.collector.metric;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.GroupMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.version.VersionControlItem;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.GroupMetricEvent;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.EnvUtil;
import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupMetricService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.version.VersionControlService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.METRIC_GROUP;

/**
 * @author didi
 */
@Component
public class GroupMetricCollector extends AbstractMetricCollector<List<GroupMetrics>> {
    protected static final ILog  LOGGER = LogFactory.getLog("METRIC_LOGGER");

    @Autowired
    private VersionControlService versionControlService;

    @Autowired
    private GroupMetricService groupMetricService;

    @Autowired
    private GroupService groupService;

    @Override
    public void collectMetrics(ClusterPhy clusterPhy) {
        Long   startTime        = System.currentTimeMillis();
        Long   clusterPhyId     = clusterPhy.getId();

        List<String> groups = new ArrayList<>();
        try {
            groups = groupService.listGroupsFromKafka(clusterPhyId);
        } catch (Exception e) {
            LOGGER.error("method=GroupMetricCollector||clusterPhyId={}||msg=exception!", clusterPhyId, e);
        }

        if(CollectionUtils.isEmpty(groups)){return;}

        List<VersionControlItem> items = versionControlService.listVersionControlItem(clusterPhyId, collectorType().getCode());

        FutureWaitUtil<Void> future = getFutureUtilByClusterPhyId(clusterPhyId);

        Map<String, List<GroupMetrics>> metricsMap = new ConcurrentHashMap<>();
        for(String groupName : groups) {
            future.runnableTask(
                    String.format("method=GroupMetricCollector||clusterPhyId=%d||groupName=%s", clusterPhyId, groupName),
                    30000,
                    () -> collectMetrics(clusterPhyId, groupName, metricsMap, items));
        }

        future.waitResult(30000);

        List<GroupMetrics> metricsList = new ArrayList<>();
        metricsMap.values().forEach(elem -> metricsList.addAll(elem));

        publishMetric(new GroupMetricEvent(this, metricsList));

        LOGGER.info("method=GroupMetricCollector||clusterPhyId={}||startTime={}||cost={}||msg=collect finished.",
                clusterPhyId, startTime, System.currentTimeMillis() - startTime);
    }

    @Override
    public VersionItemTypeEnum collectorType() {
        return METRIC_GROUP;
    }

    /**************************************************** private method ****************************************************/

    private void collectMetrics(Long clusterPhyId, String groupName, Map<String, List<GroupMetrics>> metricsMap, List<VersionControlItem> items) {
        long startTime = System.currentTimeMillis();

        List<GroupMetrics> groupMetricsList = new ArrayList<>();

        Map<String, GroupMetrics> tpGroupPOMap = new HashMap<>();

        GroupMetrics groupMetrics = new GroupMetrics(clusterPhyId, groupName, true);
        groupMetrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, Constant.COLLECT_METRICS_ERROR_COST_TIME);

        for(VersionControlItem v : items) {
            try {
                String metricName = v.getName();

                Result<List<GroupMetrics>> ret = groupMetricService.collectGroupMetricsFromKafka(clusterPhyId, groupName, metricName);
                if(null == ret || ret.failed() || ValidateUtils.isEmptyList(ret.getData())) {
                    continue;
                }

                ret.getData().stream().forEach(metrics -> {
                    if (metrics.isBGroupMetric()) {
                        groupMetrics.putMetric(metrics.getMetrics());
                    } else {
                        String  topicName   = metrics.getTopic();
                        Integer partitionId = metrics.getPartitionId();
                        String  tpGroupKey  = genTopicPartitionGroupKey(topicName, partitionId);

                        tpGroupPOMap.putIfAbsent(tpGroupKey, new GroupMetrics(clusterPhyId, partitionId, topicName, groupName, false));
                        tpGroupPOMap.get(tpGroupKey).putMetric(metrics.getMetrics());
                    }
                });

                if(!EnvUtil.isOnline()){
                    LOGGER.info("method=GroupMetricCollector||clusterPhyId={}||groupName={}||metricName={}||metricValue={}",
                            clusterPhyId, groupName, metricName, JSON.toJSONString(ret.getData()));
                }
            }catch (Exception e){
                LOGGER.error("method=GroupMetricCollector||clusterPhyId={}||groupName={}||errMsg=exception!", clusterPhyId, groupName, e);
            }
        }

        groupMetricsList.add(groupMetrics);
        groupMetricsList.addAll(tpGroupPOMap.values());

        // 记录采集性能
        groupMetrics.putMetric(Constant.COLLECT_METRICS_COST_TIME_METRICS_NAME, (System.currentTimeMillis() - startTime) / 1000.0f);

        metricsMap.put(groupName, groupMetricsList);
    }

    private String genTopicPartitionGroupKey(String topic, Integer partitionId){
        return topic + "@" + partitionId;
    }
}
