package com.xiaojukeji.know.streaming.km.core.service.group.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Table;
import com.xiaojukeji.know.streaming.km.common.bean.dto.metrices.MetricGroupPartitionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.group.GroupTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.GroupMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.offset.KSOffsetSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.metric.GroupMetricParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.search.SearchTerm;
import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.GroupMetricPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.BeanUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupMetricService;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.health.state.HealthStateService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseMetricService;
import com.xiaojukeji.know.streaming.km.persistence.es.dao.GroupMetricESDAO;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.GroupMetricVersionItems.*;

/**
 * @author didi
 */
@Service("groupMetricService")
public class GroupMetricServiceImpl extends BaseMetricService implements GroupMetricService {
    private static final ILog LOGGER       = LogFactory.getLog(GroupMetricServiceImpl.class);

    public static final String GROUP_METHOD_GET_JUST_FRO_TEST                       = "getMetricJustForTest";
    public static final String GROUP_METHOD_GET_HEALTH_SCORE                        = "getMetricHealthScore";
    public static final String GROUP_METHOD_GET_LAG_RELEVANT_FROM_ADMIN_CLIENT      = "getLagRelevantFromAdminClient";
    public static final String GROUP_METHOD_GET_STATE                               = "getGroupState";

    @Override
    protected List<String> listMetricPOFields(){
        return BeanUtil.listBeanFields(GroupMetricPO.class);
    }

    @Override
    protected void initRegisterVCHandler(){
        registerVCHandler( GROUP_METHOD_GET_JUST_FRO_TEST,                              this::getMetricJustForTest);
        registerVCHandler( GROUP_METHOD_GET_LAG_RELEVANT_FROM_ADMIN_CLIENT,             this::getLagRelevantFromAdminClient);
        registerVCHandler( GROUP_METHOD_GET_HEALTH_SCORE,                               this::getMetricHealthScore);
        registerVCHandler( GROUP_METHOD_GET_STATE,                                      this::getGroupState);
    }

    @Autowired
    private GroupService groupService;

    @Autowired
    private HealthStateService healthStateService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private GroupMetricESDAO groupMetricESDAO;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return VersionItemTypeEnum.METRIC_GROUP;
    }

    @Override
    public Result<List<GroupMetrics>> collectGroupMetricsFromKafka(Long clusterId, String groupName, String metric) {
        try {
            GroupMetricParam groupMetricParam = new GroupMetricParam(clusterId, groupName, metric);
            return (Result<List<GroupMetrics>>) doVCHandler(clusterId, metric, groupMetricParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<List<GroupMetrics>> collectGroupMetricsFromKafka(Long clusterId, String groupName, List<String> metrics) {
        List<GroupMetrics> allGroupMetrics = new ArrayList<>();
        Map<String, GroupMetrics> topicPartitionGroupMap = new HashMap<>();

        GroupMetrics groupMetrics = new GroupMetrics(clusterId, groupName, true);
        Set<String> existMetricSet = new HashSet<>();
        for (String metric : metrics) {
            if (existMetricSet.contains(metric)) {
                continue;
            }

            Result<List<GroupMetrics>> ret = collectGroupMetricsFromKafka(clusterId, groupName, metric);
            if (null != ret && ret.successful()) {
                List<GroupMetrics> groupMetricsList = ret.getData();

                for (GroupMetrics gm : groupMetricsList) {

                    //记录已存在的指标
                    existMetricSet.addAll(gm.getMetrics().keySet());

                    if (gm.isBGroupMetric()) {
                        groupMetrics.getMetrics().putAll(gm.getMetrics());
                    } else {
                        GroupMetrics topicGroupMetric = topicPartitionGroupMap.getOrDefault(
                                gm.getTopic() + gm.getPartitionId(),
                                new GroupMetrics(clusterId, gm.getPartitionId(), gm.getTopic(), groupName, false));

                        topicGroupMetric.getMetrics().putAll(gm.getMetrics());
                        topicPartitionGroupMap.put(gm.getTopic() + gm.getPartitionId(), topicGroupMetric);
                    }
                }
            }
        }

        allGroupMetrics.add(groupMetrics);
        allGroupMetrics.addAll(topicPartitionGroupMap.values());
        return Result.buildSuc(allGroupMetrics);
    }

    @Override
    public Result<List<MetricMultiLinesVO>> listGroupMetricsFromES(Long clusterId, MetricGroupPartitionDTO dto) {
        Table<String/*metric*/, String/*topic&partition*/, List<MetricPointVO>> retTable = groupMetricESDAO.listGroupMetrics(
                clusterId,
                dto.getGroup(),
                dto.getGroupTopics(),
                dto.getMetricsNames(),
                dto.getAggType(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        List<MetricMultiLinesVO> multiLinesVOS = metricMap2VO(clusterId, retTable.rowMap());
        return Result.buildSuc(multiLinesVOS);
    }

    @Override
    public Result<List<GroupMetrics>> listLatestMetricsAggByGroupTopicFromES(Long clusterPhyId, List<GroupTopic> groupTopicList,
                                                                             List<String> metricNames, AggTypeEnum aggType) {
        List<GroupMetricPO> groupMetricPOS = groupMetricESDAO.listLatestMetricsAggByGroupTopic(
                clusterPhyId,
                groupTopicList,
                metricNames,
                aggType
        );

        return Result.buildSuc( ConvertUtil.list2List(groupMetricPOS, GroupMetrics.class));
    }

    @Override
    public Result<List<GroupMetrics>> listPartitionLatestMetricsFromES(Long clusterPhyId, String groupName, String topicName,
                                                                       List<String> metricNames) {
        List<GroupMetricPO> groupMetricPOS = groupMetricESDAO.listPartitionLatestMetrics(
                clusterPhyId,
                groupName,
                topicName,
                metricNames
        );

        return Result.buildSuc( ConvertUtil.list2List(groupMetricPOS, GroupMetrics.class));
    }

    @Override
    public Result<Integer> countMetricValueOccurrencesFromES(Long clusterPhyId, String groupName,
                                                             SearchTerm term, Long startTime, Long endTime) {
        setQueryMetricFlag(term);
        int count = groupMetricESDAO.countMetricValue(clusterPhyId, groupName, term, startTime, endTime);
        if(count < 0){
            return Result.buildFail();
        }

        return Result.buildSuc(count);
    }

    /**************************************************** private method ****************************************************/

    /**
     * 获取Lag相关指标, 包括lag，consumedOffset partitionEndOffset
     */
    private Result<List<GroupMetrics>> getLagRelevantFromAdminClient(VersionItemParam param) {
        GroupMetricParam groupMetricParam = (GroupMetricParam)param;
        Long clusterId      = groupMetricParam.getClusterPhyId();
        String groupName    = groupMetricParam.getGroupName();
        String metric       = groupMetricParam.getMetric();

        List<GroupMetrics> metricsList = new ArrayList<>();
        try {
            Map<TopicPartition, Long> groupOffsetMap = groupService.getGroupOffsetFromKafka(clusterId, groupName);

            // 组织 GROUP_METRIC_OFFSET_CONSUMED 指标
            for (Map.Entry<TopicPartition, Long> entry: groupOffsetMap.entrySet()) {
                GroupMetrics metrics = new GroupMetrics(clusterId, entry.getKey().partition(), entry.getKey().topic(), groupName, false);
                metrics.putMetric(GROUP_METRIC_OFFSET_CONSUMED, entry.getValue().floatValue());

                metricsList.add(metrics);
            }

            Result<Map<TopicPartition, Long>> offsetMapResult = partitionService.getPartitionOffsetFromKafka(clusterId, new ArrayList<>(groupOffsetMap.keySet()), KSOffsetSpec.latest());
            if (!offsetMapResult.hasData()) {
                // 获取失败
                return Result.buildSuc(metricsList);
            }

            for (Map.Entry<TopicPartition, Long> entry: offsetMapResult.getData().entrySet()) {
                // 组织 GROUP_METRIC_LOG_END_OFFSET 指标
                GroupMetrics metrics = new GroupMetrics(clusterId, entry.getKey().partition(), entry.getKey().topic(), groupName, false);
                metrics.putMetric(GROUP_METRIC_LOG_END_OFFSET, entry.getValue().floatValue());
                metricsList.add(metrics);

                Long groupOffset = groupOffsetMap.get(entry.getKey());
                if (groupOffset == null) {
                    // 不存在，则直接跳过
                    continue;
                }

                // 组织 GROUP_METRIC_LAG 指标
                GroupMetrics groupMetrics = new GroupMetrics(clusterId, entry.getKey().partition(), entry.getKey().topic(), groupName, false);
                groupMetrics.putMetric(GROUP_METRIC_LAG, Math.max(0L, entry.getValue() - groupOffset) * 1.0f);

                metricsList.add(groupMetrics);
            }

            return Result.buildSuc(metricsList);
        } catch (Exception e) {
            LOGGER.error("method=getLagFromAdminClient||clusterPhyId={}||groupName={}||metrics={}||msg=exception", clusterId, groupName, metric, e);
            return Result.buildFailure(VC_KAFKA_CLIENT_ERROR);
        }
    }

    private Result<List<GroupMetrics>> getMetricJustForTest(VersionItemParam param) {
        GroupMetricParam groupMetricParam = (GroupMetricParam)param;
        Long clusterPhyId = groupMetricParam.getClusterPhyId();
        String groupName  = groupMetricParam.getGroupName();
        String metric     = groupMetricParam.getMetric();

        GroupMetrics groupMetrics = new GroupMetrics(clusterPhyId, groupName, true);
        groupMetrics.putMetric(metric, 123f);

        return Result.buildSuc( Arrays.asList(groupMetrics));
    }

    /**
     * 获取group的状态
     * @param param
     * @return
     */
    private Result<List<GroupMetrics>> getGroupState(VersionItemParam param) {
        GroupMetricParam groupMetricParam = (GroupMetricParam)param;
        Long clusterPhyId = groupMetricParam.getClusterPhyId();
        String groupName  = groupMetricParam.getGroupName();
        String metric     = groupMetricParam.getMetric();

        GroupMetrics metrics = new GroupMetrics(clusterPhyId, groupName, true);
        metrics.putMetric(metric,
                (float)groupService.getGroupStateFromDB(clusterPhyId, groupName).getCode()
        );

        return Result.buildSuc(Arrays.asList(metrics));
    }

    /**
     * 获取group的健康分指标
     * @param param
     * @return
     */
    private Result<List<GroupMetrics>> getMetricHealthScore(VersionItemParam param) {
        GroupMetricParam groupMetricParam = (GroupMetricParam)param;

        return Result.buildSuc(Arrays.asList(
                healthStateService.calGroupHealthMetrics(groupMetricParam.getClusterPhyId(), groupMetricParam.getGroupName()))
        );
    }
}
