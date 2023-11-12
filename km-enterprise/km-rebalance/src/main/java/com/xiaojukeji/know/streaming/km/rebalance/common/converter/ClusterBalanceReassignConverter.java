package com.xiaojukeji.know.streaming.km.rebalance.common.converter;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.ClusterBalanceReassignDetail;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.ClusterBalanceReassignExtendData;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.detail.ClusterBalanceDetailDataGroupByPartition;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.detail.ClusterBalanceDetailDataGroupByTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.Job;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.JobStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.SubJobReplicaMoveDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReplaceReassignSub;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceReassignPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobClusterBalanceReplicaMoveVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobPartitionDetailVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobVO;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;

import java.util.*;
import java.util.stream.Collectors;

@EnterpriseLoadReBalance
public class ClusterBalanceReassignConverter {

    private ClusterBalanceReassignConverter() {
    }

    public static JobDetail convert2JobDetail(Job job, ClusterBalanceReassignDetail reassignDetail) {
        JobDetail jobDetail = new JobDetail();
        jobDetail.setId(job.getId());
        jobDetail.setJobType(job.getJobType());
        jobDetail.setJobName(job.getJobName());
        jobDetail.setJobStatus(job.getJobStatus());
        jobDetail.setPlanTime(job.getPlanTime());

        jobDetail.setStartTime(reassignDetail.getStartTime());
        jobDetail.setEndTime(reassignDetail.getFinishedTime());
        jobDetail.setFlowLimit(reassignDetail.getThrottleUnitB().doubleValue());

        JobStatus jobStatus = new JobStatus(reassignDetail.getReassignTopicDetailsList().stream().map(elem -> elem.getStatus()).collect(Collectors.toList()));
        jobDetail.setTotal(jobStatus.getTotal());
        jobDetail.setSuccess(jobStatus.getSuccess());
        jobDetail.setFail(jobStatus.getFailed());
        jobDetail.setDoing(jobStatus.getDoing());

        List<SubJobVO> subJobDetailList = new ArrayList<>();
        subJobDetailList.addAll(
                ConvertUtil.list2List(convert2SubJobReplicaMoveDetailList(reassignDetail.getReassignTopicDetailsList()), SubJobClusterBalanceReplicaMoveVO.class)
        );
        jobDetail.setSubJobs(subJobDetailList);

        return jobDetail;
    }

    public static ClusterBalanceReassignDetail convert2ClusterBalanceReassignDetail(ClusterBalanceJobPO jobPO, List<ClusterBalanceReassignPO> reassignPOS) {
        // 按照Topic做聚合
        Map<String, List<ClusterBalanceReassignPO>> topicJobPOMap = new HashMap<>();
        reassignPOS.forEach(elem -> {
            topicJobPOMap.putIfAbsent(elem.getTopicName(), new ArrayList<>());
            topicJobPOMap.get(elem.getTopicName()).add(elem);
        });

        List<ClusterBalanceDetailDataGroupByTopic> reassignTopicDetailsList = new ArrayList<>();
        for (Map.Entry<String, List<ClusterBalanceReassignPO>> entry: topicJobPOMap.entrySet()) {
            reassignTopicDetailsList.add(convert2ClusterBalanceDetailDataGroupByTopic(entry.getValue()));
        }

        ClusterBalanceReassignDetail jobDetail = new ClusterBalanceReassignDetail();
        jobDetail.setThrottleUnitB(jobPO.getThrottleUnitB());
        jobDetail.setReassignTopicDetailsList(reassignTopicDetailsList);
        jobDetail.setStartTime(jobPO.getStartTime());
        if (JobStatusEnum.isFinished(jobPO.getStatus())) {
            jobDetail.setFinishedTime(jobPO.getFinishedTime());
        }
        return jobDetail;
    }

    private static ClusterBalanceDetailDataGroupByTopic convert2ClusterBalanceDetailDataGroupByTopic(List<ClusterBalanceReassignPO> reassingns) {
        Set<Integer> originalBrokerIdSet = new HashSet<>();
        Set<Integer> reassignBrokerIdSet = new HashSet<>();

        // 分区的信息
        List<ClusterBalanceDetailDataGroupByPartition> partitionDetailList = new ArrayList<>();
        for (ClusterBalanceReassignPO reassignPO : reassingns) {
            ClusterBalanceDetailDataGroupByPartition detail = new ClusterBalanceDetailDataGroupByPartition();
            detail.setPartitionId(reassignPO.getPartitionId());
            detail.setClusterPhyId(reassignPO.getClusterId());
            detail.setTopicName(reassignPO.getTopicName());
            detail.setOriginalBrokerIdList(CommonUtils.string2IntList(reassignPO.getOriginalBrokerIds()));
            detail.setReassignBrokerIdList(CommonUtils.string2IntList(reassignPO.getReassignBrokerIds()));
            detail.setStatus(reassignPO.getStatus());

            ClusterBalanceReassignExtendData extendData = ConvertUtil.str2ObjByJson(reassignPO.getExtendData(), ClusterBalanceReassignExtendData.class);
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
        ClusterBalanceDetailDataGroupByTopic topicDetail = new ClusterBalanceDetailDataGroupByTopic();
        topicDetail.setPartitionIdList(partitionDetailList.stream().map(elem -> elem.getPartitionId()).collect(Collectors.toList()));
        topicDetail.setReassignPartitionDetailsList(partitionDetailList);
        topicDetail.setClusterPhyId(reassingns.get(0).getClusterId());
        topicDetail.setTopicName(reassingns.get(0).getTopicName());

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
        topicDetail.setOriginalRetentionTimeUnitMs(partitionDetailList.get(0).getOriginalRetentionTimeUnitMs());
        topicDetail.setReassignRetentionTimeUnitMs(partitionDetailList.get(0).getReassignRetentionTimeUnitMs());

        topicDetail.setStatus(
                new JobStatus(
                        partitionDetailList.stream().map(elem -> elem.getStatus()).collect(Collectors.toList())
                ).getStatus()
        );

        return topicDetail;
    }

    public static List<SubJobPartitionDetailVO> convert2SubJobPartitionDetailVOList(ClusterBalanceDetailDataGroupByTopic detailDataGroupByTopic) {
        List<SubJobPartitionDetailVO> voList = new ArrayList<>();
        for (ClusterBalanceDetailDataGroupByPartition groupByPartition: detailDataGroupByTopic.getReassignPartitionDetailsList()) {
            SubJobPartitionDetailVO vo = new SubJobPartitionDetailVO();
            vo.setPartitionId(groupByPartition.getPartitionId());
            vo.setSourceBrokerIds(groupByPartition.getOriginalBrokerIdList());
            vo.setDesBrokerIds(groupByPartition.getReassignBrokerIdList());
            vo.setTotalSize(groupByPartition.getNeedReassignLogSizeUnitB() != null ? groupByPartition.getNeedReassignLogSizeUnitB().doubleValue(): null);
            vo.setMovedSize(groupByPartition.getFinishedReassignLogSizeUnitB() != null ? groupByPartition.getFinishedReassignLogSizeUnitB().doubleValue(): null);
            vo.setStatus(groupByPartition.getStatus());
            vo.setRemainTime(groupByPartition.getRemainTimeUnitMs());

            voList.add(vo);
        }

        return voList;
    }

    private static List<SubJobReplicaMoveDetail> convert2SubJobReplicaMoveDetailList(List<ClusterBalanceDetailDataGroupByTopic> reassignTopicDetailsList) {
        List<SubJobReplicaMoveDetail> detailList = new ArrayList<>();

        for (ClusterBalanceDetailDataGroupByTopic detailDataGroupByTopic: reassignTopicDetailsList) {
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
            JobStatus jobStatus = new JobStatus(detailDataGroupByTopic.getReassignPartitionDetailsList().stream().map(elem -> elem.getStatus()).collect(Collectors.toList()));            detail.setTotal(jobStatus.getTotal());
            detail.setSuccess(jobStatus.getSuccess());
            detail.setFail(jobStatus.getFailed());
            detail.setDoing(jobStatus.getDoing());
            detail.setRemainTime(detailDataGroupByTopic.getRemainTimeUnitMs());
            detailList.add(detail);
        }

        return detailList;
    }

    public static List<ReplaceReassignSub> convert2ReplaceReassignSubList(List<ClusterBalanceReassignPO> reassignPOList) {
        List<ReplaceReassignSub> voList = new ArrayList<>();
        for (ClusterBalanceReassignPO reassignPO: reassignPOList) {
            voList.add(convert2ReplaceReassignSub(reassignPO));
        }
        return voList;
    }

    public static ReplaceReassignSub convert2ReplaceReassignSub(ClusterBalanceReassignPO reassignPO) {
        ReplaceReassignSub reassignSub = new ReplaceReassignSub();
        reassignSub.setClusterPhyId(reassignPO.getClusterId());
        reassignSub.setOriginalBrokerIdList(CommonUtils.string2IntList(reassignPO.getOriginalBrokerIds()));
        reassignSub.setReassignBrokerIdList(CommonUtils.string2IntList(reassignPO.getReassignBrokerIds()));
        reassignSub.setPartitionId(reassignPO.getPartitionId());
        reassignSub.setTopicName(reassignPO.getTopicName());
        return reassignSub;
    }

}
