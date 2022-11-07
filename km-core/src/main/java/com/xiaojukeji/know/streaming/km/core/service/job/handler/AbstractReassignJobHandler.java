package com.xiaojukeji.know.streaming.km.core.service.job.handler;

import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.plan.ReassignTopicPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.Job;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.JobStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.content.JobCommunityReassignContent;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobModifyDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReassignJobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.detail.ReassignJobDetailDataGroupByTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.reassign.ReassignSubJobPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobPartitionDetailVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.point.MetricPointVO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.converter.ReassignConverter;
import com.xiaojukeji.know.streaming.km.common.enums.AggTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobActionEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.job.JobHandler;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignJobService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用Job 和 具体的ReassignJob 的中间层，将 job 的相关操作，转到 reassignJob 中
 */
public abstract class AbstractReassignJobHandler implements JobHandler {
    @Autowired
    private ReassignJobService reassignJobService;

    @Autowired
    private TopicMetricService topicMetricService;

    @Override
    public Result<Void> submit(Job job, String operator) {
        // 获取任务详情信息
        JobCommunityReassignContent dto = ConvertUtil.str2ObjByJson(job.getJobData(), JobCommunityReassignContent.class);
        if (dto == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "jobData格式错误");
        }

        // 转换格式，并创建任务
        return reassignJobService.create(job.getId(), ReassignConverter.convert2ReplaceReassignJob(dto), operator);
    }

    @Override
    public Result<Void> delete(Job job, String operator) {
        return reassignJobService.delete(job.getId(), operator);
    }

    @Override
    public Result<Void> modify(Job job, String operator) {
        // 获取任务详情信息
        JobCommunityReassignContent dto = ConvertUtil.str2ObjByJson(job.getJobData(), JobCommunityReassignContent.class);

        // 修改任务
        return reassignJobService.modify(job.getId(), ReassignConverter.convert2ReplaceReassignJob(dto), operator);
    }

    @Override
    public Result<JobModifyDetail> getTaskModifyDetail(Job job) {
        // 获取任务详情信息
        JobCommunityReassignContent dto = ConvertUtil.str2ObjByJson(job.getJobData(), JobCommunityReassignContent.class);
        if (dto == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "jobData格式错误");
        }

        JobModifyDetail detail = ConvertUtil.obj2Obj(job, JobModifyDetail.class);
        detail.setJobData(ConvertUtil.obj2JsonWithIgnoreCircularReferenceDetect(this.updateLatestMetrics(dto)));

        return Result.buildSuc(detail);
    }

    @Override
    public Result<Void> updateLimit(Job job, Long limit, String operator){
        return reassignJobService.modifyThrottle(job.getId(), limit, operator);
    }

    @Override
    public Result<Void> process(Job job, JobActionEnum action, String operator) {
        if (JobActionEnum.START.equals(action)) {
            return reassignJobService.execute(job.getId(), operator);
        }

        if (JobActionEnum.CANCEL.equals(action)) {
            return reassignJobService.cancel(job.getId(), operator);
        }

        // 迁移中，不支持该操作
        return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FORBIDDEN, String.format("不支持[%s]操作", action.getValue()));
    }

    @Override
    public Result<JobStatus> status(Job job) {
        // Topic下每个分区的状态
        Map<String, List<ReassignSubJobPO>> topicJobsMap = new HashMap<>();

        // 获取子任务，并按照Topic进行聚合
        List<ReassignSubJobPO> allSubJobPOList = reassignJobService.getSubJobsByJobId(job.getId());
        allSubJobPOList.forEach(elem -> {
            topicJobsMap.putIfAbsent(elem.getTopicName(), new ArrayList<>());
            topicJobsMap.get(elem.getTopicName()).add(elem);
        });

        // 获取每个Topic的状态
        List<Integer> topicStatusList = new ArrayList<>();
        for (List<ReassignSubJobPO> topicJobPOList: topicJobsMap.values()) {
            topicStatusList.add(new JobStatus(
                    topicJobPOList.stream().map(elem -> elem.getStatus()).collect(Collectors.toList())
            ).getStatus());
        }

        // 聚合Topic的结果
        return Result.buildSuc(new JobStatus(topicStatusList));
    }

    @Override
    public Result<JobDetail> getTaskDetail(Job job) {
        Result<ReassignJobDetail> detailResult = reassignJobService.getJobDetailsGroupByTopic(job.getId());
        if (detailResult.failed()) {
            return Result.buildFromIgnoreData(detailResult);
        }

        return Result.buildSuc(ReassignConverter.convert2JobDetail(job, detailResult.getData()));
    }

    @Override
    public Result<List<SubJobPartitionDetailVO>> getSubJobPartitionDetail(Job job, String topic) {
        Result<ReassignJobDetail> detailResult = reassignJobService.getJobDetailsGroupByTopic(job.getId());
        if (detailResult.failed()) {
            return Result.buildFromIgnoreData(detailResult);
        }

        List<ReassignJobDetailDataGroupByTopic> detailDataGroupByTopicList = detailResult.getData().getReassignTopicDetailsList()
                .stream()
                .filter(elem -> elem.getTopicName().equals(topic))
                .collect(Collectors.toList());

        if (detailDataGroupByTopicList.isEmpty()) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(job.getClusterId(), topic));
        }

        return Result.buildSuc(ReassignConverter.convert2SubJobPartitionDetailVOList(detailDataGroupByTopicList.get(0)));
    }


    /**************************************************** private method ****************************************************/

    private JobCommunityReassignContent updateLatestMetrics(JobCommunityReassignContent dto) {
        List<String> topicNameList = dto.getTopicPlanList().stream().map(elem -> elem.getTopicName()).collect(Collectors.toList());

        // 清空历史数据
        for (ReassignTopicPlanDTO elem: dto.getTopicPlanList()) {
            elem.setLatestDaysAvgBytesInList(new ArrayList<>());
            elem.setLatestDaysMaxBytesInList(new ArrayList<>());
        }

        Long now = System.currentTimeMillis();
        // 补充近三天指标
        for (int idx = 0; idx < 3; ++idx) {
            Long startTime =  now - (idx + 1) * 24L * 60L * 60L * 1000L;
            Long endTime = now - idx * 24L * 60L * 60L * 1000L;

            // 查询avg指标
            Result<Map<String, MetricPointVO>> avgMetricMapResult = topicMetricService.getAggMetricPointFromES(
                    dto.getClusterId(),
                    topicNameList,
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
                    topicNameList,
                    TopicMetricVersionItems.TOPIC_METRIC_BYTES_IN,
                    AggTypeEnum.MAX,
                    startTime,
                    endTime
            );
            Map<String, MetricPointVO> maxMetricMap = maxMetricMapResult.hasData()?  maxMetricMapResult.getData(): new HashMap<>();

            // 补充指标信息
            for (ReassignTopicPlanDTO elem: dto.getTopicPlanList()) {
                // 设置avg
                elem.getLatestDaysAvgBytesInList().add(avgMetricMap.get(elem.getTopicName()));

                // 设置max
                elem.getLatestDaysMaxBytesInList().add(maxMetricMap.get(elem.getTopicName()));
            }
        }

        return dto;
    }
}
