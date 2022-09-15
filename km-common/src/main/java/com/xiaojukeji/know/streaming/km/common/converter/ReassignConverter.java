package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.plan.ReassignPartitionPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.reassign.plan.ReassignTopicPlanDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.Job;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.JobStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.content.JobCommunityReassignContent;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.SubJobReplicaScalaDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.SubJobReplicaMoveDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignState;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReassignJobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReassignSubJobExtendData;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReplaceReassignJob;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReplaceReassignSubJob;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.detail.ReassignJobDetailDataGroupByPartition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.detail.ReassignJobDetailDataGroupByTopic;
import com.xiaojukeji.know.streaming.km.common.bean.po.reassign.ReassignJobPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.reassign.ReassignSubJobPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobPartitionDetailVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobReplicaMoveVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobReplicaScalaVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.kafka.KafkaReassignUtil;
import kafka.admin.ReassignPartitionsCommand;
import org.apache.kafka.common.TopicPartition;
import scala.jdk.javaapi.CollectionConverters;

import java.util.*;
import java.util.stream.Collectors;

public class ReassignConverter {
    private ReassignConverter() {
    }

    public static ReassignResult convert2ReassignmentResult(ReassignPartitionsCommand.VerifyAssignmentResult assignmentResult) {
        Map<TopicPartition, ReassignState> reassignStateMap = new HashMap<>();
        CollectionConverters.asJava(assignmentResult.partStates()).entrySet().forEach(entry -> {
            ReassignState state = new ReassignState();
            state.setCurrentReplicas(CollectionConverters.asJava(entry.getValue().currentReplicas()).stream().map(elem -> (Integer)elem).collect(Collectors.toList()));
            state.setTargetReplicas(CollectionConverters.asJava(entry.getValue().targetReplicas()).stream().map(elem -> (Integer)elem).collect(Collectors.toList()));
            state.setDone(entry.getValue().done());
            reassignStateMap.put(entry.getKey(), state);
        });

        ReassignResult reassignmentResult = new ReassignResult();
        reassignmentResult.setReassignStateMap(reassignStateMap);
        reassignmentResult.setPartsOngoing(assignmentResult.partsOngoing());
        return reassignmentResult;
    }

    public static ReplaceReassignJob convert2ReplaceReassignJob(JobCommunityReassignContent jobDTO) {
        List<ReplaceReassignSubJob> subJobList = new ArrayList<>();
        for (ReassignTopicPlanDTO topicPlanDTO: jobDTO.getTopicPlanList()) {
            for (ReassignPartitionPlanDTO partitionPlanDTO: topicPlanDTO.getPartitionPlanList()) {
                ReplaceReassignSubJob subJob = new ReplaceReassignSubJob();

                subJob.setClusterPhyId(partitionPlanDTO.getClusterId());
                subJob.setTopicName(partitionPlanDTO.getTopicName());
                subJob.setPartitionId(partitionPlanDTO.getPartitionId());
                subJob.setOriginalBrokerIdList(partitionPlanDTO.getOriginalBrokerIdList());
                subJob.setOriginalBrokerIdList(partitionPlanDTO.getOriginalBrokerIdList());
                subJob.setReassignBrokerIdList(partitionPlanDTO.getReassignBrokerIdList());

                subJob.setOriginalRetentionTimeUnitMs(topicPlanDTO.getOriginalRetentionTimeUnitMs());
                subJob.setReassignRetentionTimeUnitMs(topicPlanDTO.getReassignRetentionTimeUnitMs());
                subJob.setOriginReplicaNum(topicPlanDTO.getPresentReplicaNum());
                subJob.setReassignReplicaNum(topicPlanDTO.getNewReplicaNum());

                subJob.setReassignBrokerIdList(partitionPlanDTO.getReassignBrokerIdList());

                subJobList.add(subJob);
            }
        }

        ReplaceReassignJob replaceReassignJob = new ReplaceReassignJob();
        replaceReassignJob.setClusterPhyId(jobDTO.getClusterId());
        replaceReassignJob.setThrottleUnitB(jobDTO.getThrottleUnitB());
        replaceReassignJob.setDescription(jobDTO.getDescription());
        replaceReassignJob.setSubJobList(subJobList);

        return replaceReassignJob;
    }

    public static List<ReassignSubJobPO> convert2ReassignSubJobPOList(Long jobId, List<ReplaceReassignSubJob> subJobList) {
        List<ReassignSubJobPO> poList = new ArrayList<>();

        subJobList.forEach(elem -> {
            ReassignSubJobPO po = ConvertUtil.obj2Obj(elem, ReassignSubJobPO.class);

            po.setJobId(jobId);
            po.setOriginalBrokerIds(ConvertUtil.list2String(elem.getOriginalBrokerIdList(), Constant.COMMA));
            po.setReassignBrokerIds(ConvertUtil.list2String(elem.getReassignBrokerIdList(), Constant.COMMA));
            po.setStatus(JobStatusEnum.WAITING.getStatus());

            ReassignSubJobExtendData extendData = new ReassignSubJobExtendData();
            extendData.setOriginalRetentionTimeUnitMs(elem.getOriginalRetentionTimeUnitMs());
            extendData.setReassignRetentionTimeUnitMs(elem.getReassignRetentionTimeUnitMs());
            extendData.setOriginReplicaNum(elem.getOriginReplicaNum());
            extendData.setReassignReplicaNum(elem.getReassignReplicaNum());
            po.setExtendData(ConvertUtil.obj2Json(extendData));

            poList.add(po);
        });

        return poList;
    }

    public static ReassignJobPO convert2ReassignJobPO(Long jobId, ReplaceReassignJob replaceReassignJob, String creator) {
        ReassignJobPO po = new ReassignJobPO();

        po.setId(jobId);
        po.setClusterPhyId(replaceReassignJob.getClusterPhyId());
        po.setThrottleUnitByte(replaceReassignJob.getThrottleUnitB());
        po.setDescription(replaceReassignJob.getDescription());
        po.setReassignmentJson(convert2ReassignmentJson(replaceReassignJob));
        po.setCreator(creator);
        po.setStatus(JobStatusEnum.WAITING.getStatus());

        return po;
    }

    public static String convert2ReassignmentJson(ReplaceReassignJob replaceReassignJob) {
        Map<TopicPartition, List<Integer>> assignMap = new HashMap<>();
        for (ReplaceReassignSubJob subJob: replaceReassignJob.getSubJobList()) {
            assignMap.put(new TopicPartition(subJob.getTopicName(), subJob.getPartitionId()), subJob.getReassignBrokerIdList());
        }

        return KafkaReassignUtil.formatAsReassignmentJson(assignMap);
    }

    public static ReassignJobDetail convert2ReassignJobDetail(ReassignJobPO jobPO, List<ReassignSubJobPO> subJobPOList) {
        // 按照Topic做聚合
        Map<String, List<ReassignSubJobPO>> topicJobPOMap = new HashMap<>();
        subJobPOList.forEach(elem -> {
            topicJobPOMap.putIfAbsent(elem.getTopicName(), new ArrayList<>());
            topicJobPOMap.get(elem.getTopicName()).add(elem);
        });

        List<ReassignJobDetailDataGroupByTopic> reassignTopicDetailsList = new ArrayList<>();
        for (Map.Entry<String, List<ReassignSubJobPO>> entry: topicJobPOMap.entrySet()) {
            reassignTopicDetailsList.add(convert2ReassignJobDetailDataGroupByTopic(entry.getValue()));
        }

        ReassignJobDetail jobDetail = new ReassignJobDetail();
        jobDetail.setFinishedTime(jobPO.getFinishedTime());
        jobDetail.setThrottleUnitB(jobPO.getThrottleUnitByte());
        jobDetail.setDescription(jobPO.getDescription());
        jobDetail.setReassignTopicDetailsList(reassignTopicDetailsList);
        return jobDetail;
    }

    private static ReassignJobDetailDataGroupByTopic convert2ReassignJobDetailDataGroupByTopic(List<ReassignSubJobPO> subJobPOList) {
        Set<Integer> originalBrokerIdSet = new HashSet<>();
        Set<Integer> reassignBrokerIdSet = new HashSet<>();

        // 分区的信息
        List<ReassignJobDetailDataGroupByPartition> partitionDetailList = new ArrayList<>();
        for (ReassignSubJobPO subJobPO: subJobPOList) {
            ReassignJobDetailDataGroupByPartition detail = new ReassignJobDetailDataGroupByPartition();
            detail.setPartitionId(subJobPO.getPartitionId());
            detail.setClusterPhyId(subJobPO.getClusterPhyId());
            detail.setTopicName(subJobPO.getTopicName());
            detail.setOriginalBrokerIdList(CommonUtils.string2IntList(subJobPO.getOriginalBrokerIds()));
            detail.setReassignBrokerIdList(CommonUtils.string2IntList(subJobPO.getReassignBrokerIds()));
            detail.setStatus(subJobPO.getStatus());
            detail.setOldReplicaNum(detail.getOriginalBrokerIdList().size());

            ReassignSubJobExtendData extendData = ConvertUtil.str2ObjByJson(subJobPO.getExtendData(), ReassignSubJobExtendData.class);
            if (extendData != null) {
                detail.setNeedReassignLogSizeUnitB(extendData.getNeedReassignLogSizeUnitB());
                detail.setFinishedReassignLogSizeUnitB(extendData.getFinishedReassignLogSizeUnitB());
                detail.setRemainTimeUnitMs(extendData.getRemainTimeUnitMs());
                detail.setPresentReplicaNum(extendData.getOriginReplicaNum());
                detail.setNewReplicaNum(extendData.getReassignReplicaNum());
                detail.setOriginalRetentionTimeUnitMs(extendData.getOriginalRetentionTimeUnitMs());
                detail.setReassignRetentionTimeUnitMs(extendData.getReassignRetentionTimeUnitMs());
            }

            originalBrokerIdSet.addAll(detail.getOriginalBrokerIdList());
            reassignBrokerIdSet.addAll(detail.getReassignBrokerIdList());
            partitionDetailList.add(detail);
        }

        // Topic的详细信息
        ReassignJobDetailDataGroupByTopic topicDetail = new ReassignJobDetailDataGroupByTopic();
        topicDetail.setPartitionIdList(partitionDetailList.stream().map(elem -> elem.getPartitionId()).collect(Collectors.toList()));
        topicDetail.setReassignPartitionDetailsList(partitionDetailList);
        topicDetail.setClusterPhyId(subJobPOList.get(0).getClusterPhyId());
        topicDetail.setTopicName(subJobPOList.get(0).getTopicName());

        topicDetail.setOriginalBrokerIdList(new ArrayList<>(originalBrokerIdSet));
        topicDetail.setReassignBrokerIdList(new ArrayList<>(reassignBrokerIdSet));

        List<Long> needSizeList = partitionDetailList
                .stream()
                .filter(elem -> elem.getNeedReassignLogSizeUnitB() != null)
                .map(item -> item.getNeedReassignLogSizeUnitB()).collect(Collectors.toList());
        topicDetail.setNeedReassignLogSizeUnitB(needSizeList.isEmpty()? null: needSizeList.stream().reduce(Long::sum).get());

        List<Long> finishedSizeList = partitionDetailList
                .stream()
                .filter(elem -> elem.getFinishedReassignLogSizeUnitB() != null)
                .map(item -> item.getFinishedReassignLogSizeUnitB()).collect(Collectors.toList());
        topicDetail.setFinishedReassignLogSizeUnitB(finishedSizeList.isEmpty()? null: finishedSizeList.stream().reduce(Long::sum).get());

        List<Long> remainList = partitionDetailList
                .stream()
                .filter(elem -> elem.getRemainTimeUnitMs() != null)
                .map(item -> item.getRemainTimeUnitMs()).collect(Collectors.toList());
        topicDetail.setRemainTimeUnitMs(remainList.isEmpty()? null: remainList.stream().reduce(Long::max).get());

        topicDetail.setPresentReplicaNum(partitionDetailList.get(0).getPresentReplicaNum());
        topicDetail.setNewReplicaNum(partitionDetailList.get(0).getNewReplicaNum());
        topicDetail.setOldReplicaNum(partitionDetailList.get(0).getOldReplicaNum());
        topicDetail.setOriginalRetentionTimeUnitMs(partitionDetailList.get(0).getOriginalRetentionTimeUnitMs());
        topicDetail.setReassignRetentionTimeUnitMs(partitionDetailList.get(0).getReassignRetentionTimeUnitMs());

        topicDetail.setStatus(
                new JobStatus(
                        partitionDetailList.stream().map(elem -> elem.getStatus()).collect(Collectors.toList())
                ).getStatus()
        );

        return topicDetail;
    }

    public static List<SubJobPartitionDetailVO> convert2SubJobPartitionDetailVOList(ReassignJobDetailDataGroupByTopic detailDataGroupByTopic) {
        List<SubJobPartitionDetailVO> voList = new ArrayList<>();
        for (ReassignJobDetailDataGroupByPartition jobDetailDataGroupByPartition: detailDataGroupByTopic.getReassignPartitionDetailsList()) {
            SubJobPartitionDetailVO vo = new SubJobPartitionDetailVO();
            vo.setPartitionId(jobDetailDataGroupByPartition.getPartitionId());
            vo.setSourceBrokerIds(jobDetailDataGroupByPartition.getOriginalBrokerIdList());
            vo.setDesBrokerIds(jobDetailDataGroupByPartition.getReassignBrokerIdList());
            vo.setTotalSize(jobDetailDataGroupByPartition.getNeedReassignLogSizeUnitB() != null ? jobDetailDataGroupByPartition.getNeedReassignLogSizeUnitB().doubleValue(): null);
            vo.setMovedSize(jobDetailDataGroupByPartition.getFinishedReassignLogSizeUnitB() != null ? jobDetailDataGroupByPartition.getFinishedReassignLogSizeUnitB().doubleValue(): null);
            vo.setStatus(jobDetailDataGroupByPartition.getStatus());
            vo.setRemainTime(jobDetailDataGroupByPartition.getRemainTimeUnitMs());

            voList.add(vo);
        }

        return voList;
    }

    public static JobDetail convert2JobDetail(Job job, ReassignJobDetail reassignJobDetail) {
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(job.getId());
        jobDetail.setJobType(job.getJobType());
        jobDetail.setJobName(job.getJobName());
        jobDetail.setJobDesc(job.getJobDesc());
        jobDetail.setJobStatus(job.getJobStatus());
        jobDetail.setPlanTime(job.getPlanTime());
        jobDetail.setStartTime(job.getStartTime());

        jobDetail.setEndTime(reassignJobDetail.getFinishedTime());
        jobDetail.setFlowLimit(reassignJobDetail.getThrottleUnitB().doubleValue());

        JobStatus jobStatus = new JobStatus(reassignJobDetail.getReassignTopicDetailsList().stream().map(elem -> elem.getStatus()).collect(Collectors.toList()));
        jobDetail.setTotal(jobStatus.getTotal());
        jobDetail.setSuccess(jobStatus.getSuccess());
        jobDetail.setFail(jobStatus.getFailed());
        jobDetail.setDoing(jobStatus.getDoing());

        List<SubJobVO> subJobDetailList = new ArrayList<>();
        if (JobTypeEnum.TOPIC_REPLICA_MOVE.getType().equals(job.getJobType())) {
            subJobDetailList.addAll(
                    ConvertUtil.list2List(
                            convert2SubJobReplicaMoveDetailList(reassignJobDetail.getReassignTopicDetailsList()),
                            SubJobReplicaMoveVO.class
                    )
            );
        } else if (JobTypeEnum.TOPIC_REPLICA_SCALA.getType().equals(job.getJobType())) {
            subJobDetailList.addAll(
                    ConvertUtil.list2List(
                            convert2SubJobReplicaScalaDetailList(reassignJobDetail.getReassignTopicDetailsList()),
                            SubJobReplicaScalaVO.class
                    )
            );
        }

        jobDetail.setSubJobs(subJobDetailList);

        return jobDetail;
    }

    private static List<SubJobReplicaScalaDetail> convert2SubJobReplicaScalaDetailList(List<ReassignJobDetailDataGroupByTopic> reassignTopicDetailsList) {
        List<SubJobReplicaScalaDetail> detailList = new ArrayList<>();

        for (ReassignJobDetailDataGroupByTopic detailDataGroupByTopic: reassignTopicDetailsList) {
            SubJobReplicaScalaDetail detail = new SubJobReplicaScalaDetail();
            detail.setTopicName(detailDataGroupByTopic.getTopicName());
            detail.setOldReplicaNu(detailDataGroupByTopic.getOldReplicaNum());
            detail.setNewReplicaNu(detailDataGroupByTopic.getNewReplicaNum());
            detail.setSourceBrokers(detailDataGroupByTopic.getOriginalBrokerIdList());
            detail.setDesBrokers(detailDataGroupByTopic.getReassignBrokerIdList());
            detail.setStatus(detailDataGroupByTopic.getStatus());
            detail.setTotalSize(detailDataGroupByTopic.getNeedReassignLogSizeUnitB() == null? null: detailDataGroupByTopic.getNeedReassignLogSizeUnitB().doubleValue());
            detail.setMovedSize(detailDataGroupByTopic.getFinishedReassignLogSizeUnitB() == null? null: detailDataGroupByTopic.getFinishedReassignLogSizeUnitB().doubleValue());
            detail.setRemainTime(detailDataGroupByTopic.getRemainTimeUnitMs());

            JobStatus jobStatus = new JobStatus(detailDataGroupByTopic.getReassignPartitionDetailsList().stream().map(elem -> elem.getStatus()).collect(Collectors.toList()));
            detail.setTotal(jobStatus.getTotal());
            detail.setSuccess(jobStatus.getSuccess());
            detail.setFail(jobStatus.getFailed());
            detail.setDoing(jobStatus.getDoing());

            detailList.add(detail);
        }

        return detailList;
    }

    private static List<SubJobReplicaMoveDetail> convert2SubJobReplicaMoveDetailList(List<ReassignJobDetailDataGroupByTopic> reassignTopicDetailsList) {
        List<SubJobReplicaMoveDetail> detailList = new ArrayList<>();

        for (ReassignJobDetailDataGroupByTopic detailDataGroupByTopic: reassignTopicDetailsList) {
            SubJobReplicaMoveDetail detail = new SubJobReplicaMoveDetail();
            detail.setTopicName(detailDataGroupByTopic.getTopicName());
            detail.setPartitions(detailDataGroupByTopic.getPartitionIdList());
            detail.setCurrentTimeSpent(detailDataGroupByTopic.getOriginalRetentionTimeUnitMs());
            detail.setMoveTimeSpent(detailDataGroupByTopic.getReassignRetentionTimeUnitMs());
            detail.setSourceBrokers(detailDataGroupByTopic.getOriginalBrokerIdList());
            detail.setDesBrokers(detailDataGroupByTopic.getReassignBrokerIdList());
            detail.setStatus(detailDataGroupByTopic.getStatus());
            if (detailDataGroupByTopic.getNeedReassignLogSizeUnitB() != null) {
                detail.setTotalSize(detailDataGroupByTopic.getNeedReassignLogSizeUnitB().doubleValue());
            }
            if (detailDataGroupByTopic.getFinishedReassignLogSizeUnitB() != null) {
                detail.setMovedSize(detailDataGroupByTopic.getFinishedReassignLogSizeUnitB().doubleValue());
            }
            detail.setRemainTime(detailDataGroupByTopic.getRemainTimeUnitMs());

            JobStatus jobStatus = new JobStatus(detailDataGroupByTopic.getReassignPartitionDetailsList().stream().map(elem -> elem.getStatus()).collect(Collectors.toList()));
            detail.setTotal(jobStatus.getTotal());
            detail.setSuccess(jobStatus.getSuccess());
            detail.setFail(jobStatus.getFailed());
            detail.setDoing(jobStatus.getDoing());

            detailList.add(detail);
        }

        return detailList;
    }
}
