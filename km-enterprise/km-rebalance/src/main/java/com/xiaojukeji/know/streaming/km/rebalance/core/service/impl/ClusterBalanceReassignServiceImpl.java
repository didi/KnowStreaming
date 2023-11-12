package com.xiaojukeji.know.streaming.km.rebalance.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.ClusterBalanceReassignDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.reassign.ExecuteReassignParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReassignSubJobExtendData;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReassignTask;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceReassignPO;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.rebalance.common.converter.ClusterBalanceReassignConverter;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.partition.OpPartitionService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignService;
import com.xiaojukeji.know.streaming.km.core.service.replica.ReplicaMetricService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ReplicaMetricVersionItems;
import com.xiaojukeji.know.streaming.km.rebalance.persistence.ClusterBalanceJobDao;
import com.xiaojukeji.know.streaming.km.rebalance.persistence.ClusterBalanceReassignDao;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceReassignService;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
@EnterpriseLoadReBalance
public class ClusterBalanceReassignServiceImpl implements ClusterBalanceReassignService {
    private static final ILog logger = LogFactory.getLog(ClusterBalanceReassignServiceImpl.class);

    @Autowired
    private ClusterBalanceReassignDao clusterBalanceReassignDao;

    @Autowired
    private ClusterBalanceJobDao clusterBalanceJobDao;

    @Autowired
    private ReassignService reassignService;

    @Autowired
    private ReplicaMetricService replicationMetricService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Autowired
    private OpPartitionService opPartitionService;

    @Override
    public Result<Void> addBalanceReassign(ClusterBalanceReassignPO clusterBalanceReassignPO) {
        if (clusterBalanceReassignPO == null) {
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }
        try {
            int count = clusterBalanceReassignDao.insert(clusterBalanceReassignPO);
            if (count < 1) {
                logger.error("create cluster balance reassign detail failed! clusterBalanceReassignPO:{}", clusterBalanceReassignPO);
                return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
            }
        } catch (Exception e) {
            logger.error("create cluster balance reassign detail failed! clusterBalanceReassignPO:{}", clusterBalanceReassignPO, e);
            return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);

        }
        return Result.buildSuc();
    }

    @Override
    public Result<Void> addBatchBalanceReassign(List<ClusterBalanceReassignPO> reassignPOList) {
        try {
            int count = clusterBalanceReassignDao.addBatch(reassignPOList);
            if (count < 1) {
                logger.error("method=addBatchBalanceReassign||reassignPOList:{}||msg=create cluster balance reassign detail failed! ", reassignPOList);
                return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
            }
        } catch (Exception e) {
            logger.error("method=addBatchBalanceReassign||reassignPOList:{}||msg=create cluster balance reassign detail failed! ", reassignPOList, e);
            return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
        }
        return Result.buildSuc();
    }

    @Override
    public Result<Void> delete(Long jobId, String operator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        try {
            ClusterBalanceJobPO jobPO = clusterBalanceJobDao.selectById(jobId);
            if (jobPO == null) {
                // 任务不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
            }

            LambdaQueryWrapper<ClusterBalanceReassignPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ClusterBalanceReassignPO::getJobId, jobId);
            clusterBalanceReassignDao.delete(lambdaQueryWrapper);
            return Result.buildSuc();
        } catch (Exception e) {
            logger.error("method=delete||jobId={}||errMsg=exception", jobId, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<Void> execute(Long jobId) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        ClusterBalanceJobPO jobPO = clusterBalanceJobDao.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }

        if (!JobStatusEnum.canExecuteJob(jobPO.getStatus())) {
            // 状态错误，禁止执行
            return this.buildActionForbidden(jobId, jobPO.getStatus());
        }

        // 修改DB状态
        this.setJobInRunning(jobPO);

        // 执行任务
        Result<Void> rv = reassignService.executePartitionReassignments(new ExecuteReassignParam(jobPO.getClusterId(), jobPO.getReassignmentJson(), jobPO.getThrottleUnitB()));
        if (rv.failed()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return rv;
        }

        return Result.buildSuc();
    }

    @Override
    public Result<Void> cancel(Long jobId) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        try {
            ClusterBalanceJobPO jobPO = clusterBalanceJobDao.selectById(jobId);
            if (jobPO == null) {
                // 任务不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
            }

            if (!JobStatusEnum.canCancelJob(jobPO.getStatus())) {
                // 状态错误，禁止执行
                return this.buildActionForbidden(jobId, jobPO.getStatus());
            }

            this.setJobCanceled(jobPO);

            return Result.buildSuc();
        } catch (Exception e) {
            logger.error("method=cancel||jobId={}||errMsg=exception", jobId, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Boolean> verifyAndUpdateStatue(ClusterBalanceJobPO jobPO) {
        if (jobPO == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, "job not exist");
        }

        // 检查迁移的结果
        Result<ReassignResult> reassignResult = reassignService.verifyPartitionReassignments(
                new ExecuteReassignParam(jobPO.getClusterId(), jobPO.getReassignmentJson(), jobPO.getThrottleUnitB())
        );

        if (reassignResult.failed()) {
            return Result.buildFromIgnoreData(reassignResult);
        }

        // 更新任务状态
        return this.checkAndSetSuccessIfFinished(jobPO, reassignResult.getData());
    }

    @Override
    public Result<Void> modifyThrottle(Long jobId, Long throttleUnitB, String operator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        try {
            ClusterBalanceJobPO jobPO = clusterBalanceJobDao.selectById(jobId);
            if (jobPO == null) {
                // 任务不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
            }

            if (JobStatusEnum.isFinished(jobPO.getStatus())) {
                // 状态错误，禁止执行
                return this.buildActionForbidden(jobId, jobPO.getStatus());
            }

            // 修改限流值
            jobPO.setThrottleUnitB(throttleUnitB);
            clusterBalanceJobDao.updateById(jobPO);

            // 记录操作
            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.EDIT.getDesc(),
                    ModuleEnum.JOB_CLUSTER_BALANCE.getDesc(),
                    MsgConstant.getReassignJobBizStr(
                            jobId,
                            jobPO.getClusterId()
                    ),
                    String.format("新的限流值:[%d]", throttleUnitB)
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            logger.error("method=modifyThrottle||jobId={}||throttleUnitB={}||errMsg=exception", jobId, throttleUnitB, e);
        }

        return Result.buildSuc();
    }

    @Override
    public Result<Void> getAndUpdateSubJobExtendData(Long jobId) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        ClusterBalanceJobPO jobPO = clusterBalanceJobDao.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }

        List<ClusterBalanceReassignPO> reassigns = this.getBalanceReassignsByJobId(jobId);
        for (ClusterBalanceReassignPO reassignPO: reassigns) {
            Result<ReassignSubJobExtendData> extendDataResult = this.getReassignSubJobExtendData(reassignPO);
            if (extendDataResult.failed()) {
                continue;
            }

            reassignPO.setExtendData(ConvertUtil.obj2Json(extendDataResult.getData()));
            clusterBalanceReassignDao.updateById(reassignPO);
        }

        return Result.buildSuc();
    }

    @Override
    public List<ClusterBalanceReassignPO> getBalanceReassignsByJobId(Long jobId) {
        if (jobId == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<ClusterBalanceReassignPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ClusterBalanceReassignPO::getJobId, jobId);

        return clusterBalanceReassignDao.selectList(lambdaQueryWrapper);
    }

    @Override
    public Result<ClusterBalanceReassignDetail> getJobDetailsGroupByTopic(Long jobId) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        // 获取任务
        ClusterBalanceJobPO jobPO = clusterBalanceJobDao.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }

        // 获取子任务
        List<ClusterBalanceReassignPO> subJobPOList = this.getBalanceReassignsByJobId(jobId);

        // 数据组装并放回
        return Result.buildSuc(ClusterBalanceReassignConverter.convert2ClusterBalanceReassignDetail(jobPO, subJobPOList));
    }

    @Override
    public Result<Void> preferredReplicaElection(Long jobId) {
        // 获取任务
        ClusterBalanceJobPO jobPO = clusterBalanceJobDao.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }
        if (!JobStatusEnum.isFinished(jobPO.getStatus())){
            return Result.buildSuc();
        }

        // 获取子任务
        List<ClusterBalanceReassignPO> subJobPOList = this.getBalanceReassignsByJobId(jobId);
        List<TopicPartition> topicPartitions = new ArrayList<>();
        subJobPOList.stream().forEach(reassignPO -> {
            Integer targetLeader = CommonUtils.string2IntList(reassignPO.getReassignBrokerIds()).get(0);
            Integer originalLeader = CommonUtils.string2IntList(reassignPO.getOriginalBrokerIds()).get(0);
            //替换过leader的添加到优先副本选举任务列表
            if (!originalLeader.equals(targetLeader)){
                topicPartitions.add(new TopicPartition(reassignPO.getTopicName(), reassignPO.getPartitionId()));
            }
        });

        if (!topicPartitions.isEmpty()){
            return opPartitionService.preferredReplicaElection(jobPO.getClusterId(), topicPartitions);
        }

        return Result.buildSuc();
    }

    private Result<Void> buildActionForbidden(Long jobId, Integer jobStatus) {
        return Result.buildFromRSAndMsg(
                ResultStatus.OPERATION_FORBIDDEN,
                String.format("jobId:[%d] 当前 status:[%s], 不允许被执行", jobId, JobStatusEnum.valueOfStatus(jobStatus))
        );
    }

    private Result<Void> setJobInRunning(ClusterBalanceJobPO jobPO) {
        Map<String, Object> reassign = JSON.parseObject(jobPO.getReassignmentJson());
        if (reassign.isEmpty()){
            return Result.buildSuc();
        }

        List<ReassignTask> reassignTasks = JSON.parseArray(JSON.toJSONString(reassign.get(KafkaConstant.PARTITIONS)), ReassignTask.class);
        if (reassignTasks == null || reassignTasks.isEmpty()){
            return Result.buildSuc();
        }

        long now = System.currentTimeMillis();
        // 更新子任务状态
        List<ClusterBalanceReassignPO> reassignPOS = this.getBalanceReassignsByJobId(jobPO.getId());
        reassignTasks.forEach(reassignTask -> {
            for (ClusterBalanceReassignPO reassignPO: reassignPOS) {
                if (reassignTask.getTopic().equals(reassignPO.getTopicName())
                        && reassignTask.getPartition() == reassignPO.getPartitionId()) {
                    ClusterBalanceReassignPO newReassignPO = new ClusterBalanceReassignPO();
                    newReassignPO.setId(reassignPO.getId());
                    newReassignPO.setStatus(JobStatusEnum.RUNNING.getStatus());
                    newReassignPO.setStartTime(new Date(now));
                    newReassignPO.setUpdateTime(new Date(now));
                    clusterBalanceReassignDao.updateById(newReassignPO);
                    break;
                }
            }
        });

        // 更新父任务状态
        ClusterBalanceJobPO newJobPO = new ClusterBalanceJobPO();
        newJobPO.setId(jobPO.getId());
        newJobPO.setStatus(JobStatusEnum.RUNNING.getStatus());
        newJobPO.setStartTime(new Date(now));
        newJobPO.setUpdateTime(new Date(now));
        clusterBalanceJobDao.updateById(newJobPO);

        return Result.buildSuc();
    }

    private Result<Void> setJobCanceled(ClusterBalanceJobPO jobPO) {
        // 更新子任务状态
        List<ClusterBalanceReassignPO> reassignPOS = this.getBalanceReassignsByJobId(jobPO.getId());
        for (ClusterBalanceReassignPO reassignPO: reassignPOS) {
            ClusterBalanceReassignPO newReassignPO = new ClusterBalanceReassignPO();
            newReassignPO.setId(reassignPO.getId());
            newReassignPO.setStatus(JobStatusEnum.CANCELED.getStatus());
            clusterBalanceReassignDao.updateById(newReassignPO);
        }

        // 更新父任务状态
        ClusterBalanceJobPO newJobPO = new ClusterBalanceJobPO();
        newJobPO.setId(jobPO.getId());
        newJobPO.setStatus(JobStatusEnum.CANCELED.getStatus());
        clusterBalanceJobDao.updateById(newJobPO);

        return Result.buildSuc();
    }

    private Result<Boolean> checkAndSetSuccessIfFinished(ClusterBalanceJobPO jobPO, ReassignResult reassignmentResult) {
        long now = System.currentTimeMillis();
        List<ClusterBalanceReassignPO> reassignPOS = this.getBalanceReassignsByJobId(jobPO.getId());
        boolean existNotFinished = false;
        for (ClusterBalanceReassignPO balanceReassignPO: reassignPOS) {
            if (!reassignmentResult.checkPartitionFinished(balanceReassignPO.getTopicName(), balanceReassignPO.getPartitionId())) {
                existNotFinished = true;
                continue;
            }

            // 更新状态
            ClusterBalanceReassignPO newReassignPO = new ClusterBalanceReassignPO();
            newReassignPO.setId(balanceReassignPO.getId());
            newReassignPO.setStatus(JobStatusEnum.SUCCESS.getStatus());
            newReassignPO.setFinishedTime(new Date(now));
            clusterBalanceReassignDao.updateById(newReassignPO);
        }

        // 更新任务状态
        if (!existNotFinished && !reassignmentResult.isPartsOngoing()) {
            ClusterBalanceJobPO newBalanceJobPO = new ClusterBalanceJobPO();
            newBalanceJobPO.setId(jobPO.getId());
            newBalanceJobPO.setStatus(JobStatusEnum.SUCCESS.getStatus());
            newBalanceJobPO.setFinishedTime(new Date(now));
            clusterBalanceJobDao.updateById(newBalanceJobPO);
        }

        return Result.buildSuc(reassignmentResult.isPartsOngoing());
    }

    private Result<ReassignSubJobExtendData> getReassignSubJobExtendData(ClusterBalanceReassignPO subJobPO) {
        Partition partition = partitionService.getPartitionByTopicAndPartitionId(
                subJobPO.getClusterId(),
                subJobPO.getTopicName(),
                subJobPO.getPartitionId()
        );

        if (partition == null) {
            // 分区不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getPartitionNotExist(subJobPO.getClusterId(),
                    subJobPO.getTopicName(),
                    subJobPO.getPartitionId())
            );
        }

        // 获取leader副本
        Float leaderLogSize = this.getReplicaLogSize(subJobPO.getClusterId(),
                partition.getLeaderBrokerId(),
                subJobPO.getTopicName(),
                subJobPO.getPartitionId()
        );

        // 获取新增的副本
        Set<Integer> newReplicas = new HashSet<>(CommonUtils.string2IntList(subJobPO.getReassignBrokerIds()));
        newReplicas.removeAll(CommonUtils.string2IntList(subJobPO.getOriginalBrokerIds()));

        Long finishedLogSizeUnitB = 0L;
        for (Integer brokerId: newReplicas) {
            Float replicaLogSize = this.getReplicaLogSize(subJobPO.getClusterId(),
                    brokerId,
                    subJobPO.getTopicName(),
                    subJobPO.getPartitionId()
            );
            if (replicaLogSize == null) {
                continue;
            }

            finishedLogSizeUnitB += replicaLogSize.longValue();
        }

        ReassignSubJobExtendData extendData = ConvertUtil.str2ObjByJson(subJobPO.getExtendData(), ReassignSubJobExtendData.class);
        extendData.setFinishedReassignLogSizeUnitB(finishedLogSizeUnitB);
        if (leaderLogSize != null) {
            extendData.setNeedReassignLogSizeUnitB(leaderLogSize.longValue() * newReplicas.size());
        }
        //迁移任务已完成时，若分区指标未更新，已完成logSize等于需要迁移logSize
        if (JobStatusEnum.isFinished(subJobPO.getStatus()) && finishedLogSizeUnitB.equals(0L)){
            extendData.setFinishedReassignLogSizeUnitB(extendData.getNeedReassignLogSizeUnitB());
        }

        if (extendData.getNeedReassignLogSizeUnitB().equals(0L) || JobStatusEnum.isFinished(subJobPO.getStatus())) {
            extendData.setRemainTimeUnitMs(0L);
        } else if (extendData.getFinishedReassignLogSizeUnitB().equals(0L)) {
            // 未知
            extendData.setRemainTimeUnitMs(null);
        } else {
            Long usedTime = System.currentTimeMillis() - subJobPO.getStartTime().getTime();
            // (需迁移LogSize / 已迁移LogSize) = (总时间 / 已进行时间)
            extendData.setRemainTimeUnitMs(extendData.getNeedReassignLogSizeUnitB() * usedTime / extendData.getFinishedReassignLogSizeUnitB());
        }

        return Result.buildSuc(extendData);
    }

    private Float getReplicaLogSize(Long clusterPhyId, Integer brokerId, String topicName, Integer partitionId) {
        Result<ReplicationMetrics> replicaMetricsResult = replicationMetricService.collectReplicaMetricsFromKafka(
                clusterPhyId,
                topicName,
                partitionId,
                brokerId,
                ReplicaMetricVersionItems.REPLICATION_METRIC_LOG_SIZE
        );

        if (!replicaMetricsResult.hasData()) {
            return null;
        }

        return replicaMetricsResult.getData().getMetric(ReplicaMetricVersionItems.REPLICATION_METRIC_LOG_SIZE);
    }
}