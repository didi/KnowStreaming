package com.xiaojukeji.know.streaming.km.biz.reassign.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.reassign.ReassignManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.change.CreateChangeReplicasPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.move.CreateMoveReplicaPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.ReassignTopicOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignPlan;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.plan.ReassignPlanVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.ReassignTopicOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.plan.ReassignTopicPlanVO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.converter.ReassignVOConverter;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReassignManagerImpl implements ReassignManager {
    private static final ILog log = LogFactory.getLog(ReassignManagerImpl.class);

    @Autowired
    private ReassignService reassignService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicMetricService topicMetricService;

    @Override
    public Result<ReassignPlanVO> createReassignmentPlanJson(List<CreateMoveReplicaPlanDTO> dtoList) {
        if (ValidateUtils.isEmptyList(dtoList)) {
            return Result.buildSuc(new ReassignPlanVO(new ArrayList<>()));
        }

        List<ReassignTopicPlanVO> topicPlanList = new ArrayList<>();
        for (CreateMoveReplicaPlanDTO planDTO: dtoList) {
            Result<ReassignPlan> planResult = reassignService.generateReassignmentJson(
                    planDTO.getClusterId(),
                    planDTO.getTopicName(),
                    planDTO.getPartitionIdList(),
                    planDTO.getBrokerIdList(),
                    planDTO.getEnableRackAwareness()
            );
            if (planResult.failed()) {
                // 出错则直接返回错误
                return Result.buildFromIgnoreData(planResult);
            }

            // 转换格式
            topicPlanList.add(ReassignVOConverter.convert2ReassignTopicPlanVO(planResult.getData()));
        }

        return Result.buildSuc(new ReassignPlanVO(topicPlanList));
    }

    @Override
    public Result<ReassignPlanVO> createReplicaChangePlanJson(List<CreateChangeReplicasPlanDTO> dtoList) {
        if (ValidateUtils.isEmptyList(dtoList)) {
            return Result.buildSuc(new ReassignPlanVO(new ArrayList<>()));
        }

        List<ReassignTopicPlanVO> topicPlanList = new ArrayList<>();
        for (CreateChangeReplicasPlanDTO planDTO: dtoList) {
            Result<ReassignPlan> planResult = reassignService.generateReplicaChangeReassignmentJson(
                    planDTO.getClusterId(),
                    planDTO.getTopicName(),
                    planDTO.getNewReplicaNum(),
                    planDTO.getBrokerIdList()
            );
            if (planResult.failed()) {
                // 出错则直接返回错误
                return Result.buildFromIgnoreData(planResult);
            }

            // 转换格式
            topicPlanList.add(ReassignVOConverter.convert2ReassignTopicPlanVO(planResult.getData()));
        }

        return Result.buildSuc(new ReassignPlanVO(topicPlanList));
    }

    @Override
    public Result<List<ReassignTopicOverviewVO>> getReassignmentTopicsOverview(ReassignTopicOverviewDTO dto) {
        Map<String, Topic> topicMap = topicService.listTopicsFromDB(dto.getClusterId()).stream().collect(Collectors.toMap(Topic::getTopicName, Function.identity()));

        Map<String, ReassignTopicOverviewVO> voMap = new HashMap<>();
        for (String topicName: dto.getTopicNameList()) {
            Topic topic = topicMap.get(topicName);
            if (topic == null) {
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(dto.getClusterId(), topicName));
            }

            ReassignTopicOverviewVO vo = ConvertUtil.obj2Obj(topic, ReassignTopicOverviewVO.class);
            vo.setPartitionIdList(new ArrayList<>(topic.getPartitionMap().keySet()));
            vo.setRetentionMs(topic.getRetentionMs());
            vo.setLatestDaysAvgBytesInList(new ArrayList<>());
            vo.setLatestDaysMaxBytesInList(new ArrayList<>());
            voMap.put(topicName, vo);
        }

        Long now = System.currentTimeMillis();

        // 补充近三天指标
        for (int idx = 0; idx < 3; ++idx) {
            Long startTime = now - (idx + 1) * 24L * 60L * 60L * 1000L;
            Long endTime = now - idx * 24L * 60L * 60L * 1000L;

            // 查询avg指标
            Result<Map<String, MetricPointVO>> avgMetricMapResult = topicMetricService.getAggMetricPointFromES(
                    dto.getClusterId(),
                    dto.getTopicNameList(),
                    TopicMetricVersionItems.TOPIC_METRIC_BYTES_IN,
                    AggTypeEnum.AVG,
                    startTime,
                    endTime
            );
            Map<String, MetricPointVO> avgMetricMap = avgMetricMapResult.hasData()?  avgMetricMapResult.getData(): new HashMap<>();
            avgMetricMap.values().forEach(elem -> elem.setTimeStamp(endTime));

            // 查询max指标
            Result<Map<String, MetricPointVO>> maxMetricMapResult = topicMetricService.getAggMetricPointFromES(
                    dto.getClusterId(),
                    dto.getTopicNameList(),
                    TopicMetricVersionItems.TOPIC_METRIC_BYTES_IN,
                    AggTypeEnum.MAX,
                    startTime,
                    endTime
            );
            Map<String, MetricPointVO> maxMetricMap = maxMetricMapResult.hasData()?  maxMetricMapResult.getData(): new HashMap<>();

            // 补充到vo中
            this.supplyLatestMetrics(voMap, avgMetricMap, maxMetricMap);
        }

        return Result.buildSuc(new ArrayList<>(voMap.values()));
    }

    /**************************************************** private method ****************************************************/

    private void supplyLatestMetrics(Map<String, ReassignTopicOverviewVO> voMap,
                                     Map<String, MetricPointVO> avgMetricMap,
                                     Map<String, MetricPointVO> maxMetricMap) {
        for (Map.Entry<String, ReassignTopicOverviewVO> entry: voMap.entrySet()) {
            entry.getValue().getLatestDaysAvgBytesInList().add(avgMetricMap.get(entry.getKey()));
            entry.getValue().getLatestDaysMaxBytesInList().add(maxMetricMap.get(entry.getKey()));
        }
    }
}
