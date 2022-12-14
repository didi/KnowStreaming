package com.xiaojukeji.know.streaming.km.core.service.reassign.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ReplicationMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaTopicConfigParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.reassign.ExecuteReassignParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReassignJobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReassignSubJobExtendData;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReplaceReassignJob;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.job.ReplaceReassignSubJob;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.po.reassign.ReassignJobPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.reassign.ReassignSubJobPO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.kafka.TopicConfig0100;
import com.xiaojukeji.know.streaming.km.common.converter.ReassignConverter;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.common.utils.kafka.KafkaReassignUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.partition.OpPartitionService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignJobService;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignService;
import com.xiaojukeji.know.streaming.km.core.service.replica.ReplicaMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicConfigService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.ReplicaMetricVersionItems;
import com.xiaojukeji.know.streaming.km.persistence.mysql.reassign.ReassignJobDAO;
import com.xiaojukeji.know.streaming.km.persistence.mysql.reassign.ReassignSubJobDAO;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ReassignJobServiceImpl implements ReassignJobService {
    private static final ILog log = LogFactory.getLog(ReassignJobServiceImpl.class);

    @Autowired
    private ReassignJobDAO reassignJobDAO;

    @Autowired
    private ReassignSubJobDAO reassignSubJobDAO;

    @Autowired
    private TopicService topicService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ReassignService reassignService;

    @Autowired
    private ReplicaMetricService replicationMetricService;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Autowired
    private TopicConfigService topicConfigService;

    @Autowired
    private OpPartitionService opPartitionService;

    @Override
    @Transactional
    public Result<Void> create(Long jobId, ReplaceReassignJob replaceReassignJob, String creator) {
        // 检查参数是否合法
        Result<Void> rv = checkParamLegalAndModifyOriginData(jobId, replaceReassignJob, creator);
        if (rv.failed()) {
            return rv;
        }

        try {
            // 数据写入DB
            reassignJobDAO.addAndSetId(ReassignConverter.convert2ReassignJobPO(jobId, replaceReassignJob, creator));

            List<ReassignSubJobPO> subJobPOList = ReassignConverter.convert2ReassignSubJobPOList(jobId, replaceReassignJob.getSubJobList());
            subJobPOList.forEach(elem -> reassignSubJobDAO.insert(elem));

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    creator,
                    OperationEnum.ADD.getDesc(),
                    ModuleEnum.JOB_KAFKA_REPLICA_REASSIGN.getDesc(),
                    MsgConstant.getReassignJobBizStr(
                            jobId,
                            replaceReassignJob.getClusterPhyId()
                    ),
                    ConvertUtil.obj2Json(replaceReassignJob)
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=create||jobId={}||replaceReassignJob={}||errMsg=exception", jobId, replaceReassignJob, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Void> delete(Long jobId, String operator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        try {
            ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
            if (jobPO == null) {
                // 任务不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
            }

            if (JobStatusEnum.canNotDeleteJob(jobPO.getStatus())) {
                // 状态错误，禁止执行
                return this.buildActionForbidden(jobId, jobPO.getStatus());
            }

            reassignJobDAO.deleteById(jobId);

            LambdaQueryWrapper<ReassignSubJobPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(ReassignSubJobPO::getJobId, jobId);
            reassignSubJobDAO.delete(lambdaQueryWrapper);

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.DELETE.getDesc(),
                    ModuleEnum.JOB_KAFKA_REPLICA_REASSIGN.getDesc(),
                    MsgConstant.getReassignJobBizStr(
                            jobId,
                            jobPO.getClusterPhyId()
                    )
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=delete||jobId={}||errMsg=exception", jobId, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Void> modify(Long jobId, ReplaceReassignJob replaceReassignJob, String operator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        try {
            ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
            if (jobPO == null) {
                // 任务不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
            }

            if (!JobStatusEnum.isWaiting(jobPO.getStatus())) {
                // 状态错误，禁止执行
                return this.buildActionForbidden(jobId, jobPO.getStatus());
            }

            // 如果任务处于待执行中，则可以修改任意数据
            Result<Void> rv = this.modifyAll(jobPO, replaceReassignJob);
            if (rv.failed()) {
                return rv;
            }

            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.EDIT.getDesc(),
                    ModuleEnum.JOB_KAFKA_REPLICA_REASSIGN.getDesc(),
                    MsgConstant.getReassignJobBizStr(
                            jobId,
                            jobPO.getClusterPhyId()
                    ),
                    ConvertUtil.obj2Json(replaceReassignJob)
            ));

            return rv;
        } catch (Exception e) {
            log.error("method=modify||jobId={}||replaceReassignJob={}||errMsg=exception", jobId, replaceReassignJob, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return Result.buildSuc();
    }

    @Override
    public Result<Void> modifyThrottle(Long jobId, Long throttleUnitB, String operator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        try {
            ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
            if (jobPO == null) {
                // 任务不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
            }

            if (JobStatusEnum.isFinished(jobPO.getStatus())) {
                // 状态错误，禁止执行
                return this.buildActionForbidden(jobId, jobPO.getStatus());
            }

            // 修改限流值
            jobPO.setThrottleUnitByte(throttleUnitB);
            reassignJobDAO.updateById(jobPO);

            // 记录操作
            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.EDIT.getDesc(),
                    ModuleEnum.JOB_KAFKA_REPLICA_REASSIGN.getDesc(),
                    MsgConstant.getReassignJobBizStr(
                            jobId,
                            jobPO.getClusterPhyId()
                    ),
                    String.format("新的限流值:[%d]", throttleUnitB)
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=modifyThrottle||jobId={}||throttleUnitB={}||errMsg=exception", jobId, throttleUnitB, e);
        }

        return Result.buildSuc();
    }

    @Override
    @Transactional
    public Result<Void> execute(Long jobId, String operator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }

        if (!JobStatusEnum.canExecuteJob(jobPO.getStatus())) {
            // 状态错误，禁止执行
            return this.buildActionForbidden(jobId, jobPO.getStatus());
        }

        // 修改DB状态
        Result<List<ReassignSubJobPO>> subJobPOListResult = this.setJobInRunning(jobPO);

        // 执行任务
        Result<Void> rv = reassignService.executePartitionReassignments(new ExecuteReassignParam(jobPO.getClusterPhyId(), jobPO.getReassignmentJson(), jobPO.getThrottleUnitByte()));
        if (rv.failed()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return rv;
        }

        // 修改保存时间
        rv = this.modifyRetentionTime(jobPO.getClusterPhyId(), subJobPOListResult.getData(), operator);
        if (rv.failed()) {
            log.error("method=execute||jobId={}||result={}||errMsg=modify retention time failed", jobId, rv);
            return rv;
        }

        // 记录操作
        opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                operator,
                OperationEnum.EXE.getDesc(),
                ModuleEnum.JOB_KAFKA_REPLICA_REASSIGN.getDesc(),
                MsgConstant.getReassignJobBizStr(
                        jobId,
                        jobPO.getClusterPhyId()
                )
        ));

        return Result.buildSuc();
    }

    @Override
    public Result<Void> cancel(Long jobId, String operator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        try {
            ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
            if (jobPO == null) {
                // 任务不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
            }

            if (!JobStatusEnum.canCancelJob(jobPO.getStatus())) {
                // 状态错误，禁止执行
                return this.buildActionForbidden(jobId, jobPO.getStatus());
            }

            this.setJobCanceled(jobPO);

            // 记录操作
            opLogWrapService.saveOplogAndIgnoreException(new OplogDTO(
                    operator,
                    OperationEnum.CANCEL.getDesc(),
                    ModuleEnum.JOB_KAFKA_REPLICA_REASSIGN.getDesc(),
                    MsgConstant.getReassignJobBizStr(
                            jobId,
                            jobPO.getClusterPhyId()
                    )
            ));

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=cancel||jobId={}||errMsg=exception", jobId, e);

            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result<Void> verifyAndUpdateStatue(Long jobId) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }

        Result<Void> rv = reassignService.changReassignmentThrottles(
                new ExecuteReassignParam(jobPO.getClusterPhyId(), jobPO.getReassignmentJson(), jobPO.getThrottleUnitByte())
        );
        if (rv.failed()) {
            log.error("method=verifyAndUpdateStatue||jobId={}||result={}||msg=change throttle failed", jobId, rv);
            return rv;
        }

        // 检查迁移的结果
        Result<ReassignResult> rrr = reassignService.verifyPartitionReassignments(
                new ExecuteReassignParam(jobPO.getClusterPhyId(), jobPO.getReassignmentJson(), jobPO.getThrottleUnitByte())
        );

        if (rrr.failed()) {
            log.error("method=verifyAndUpdateStatue||jobId={}||result={}||msg=verify reassignment failed", jobId, rrr);
            return Result.buildFromIgnoreData(rrr);
        }

        // 还原数据保存时间，后续可以优化为缩短一段时间后就还原为原来时间
        rv = this.recoveryRetentionTime(jobPO, rrr.getData());
        if (rv != null && rv.failed()) {
            log.error("method=verifyAndUpdateStatue||jobId={}||result={}||msg=recovery retention time failed", jobId, rv);
        }

        // 更新任务状态
        rv = this.checkAndSetSuccessIfFinished(jobPO, rrr.getData());

        //如果任务还未完成，先返回，不必考虑优先副本的重新选举。
        if (!rv.successful()) {
            return Result.buildFromIgnoreData(rv);
        }

        //任务已完成，检查是否需要重新选举，并进行选举。
        rv = this.preferredReplicaElection(jobId);


        if (rv.failed()){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }

        return Result.buildFromIgnoreData(rv);
    }

    @Override
    public Result<Void> getAndUpdateSubJobExtendData(Long jobId) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }

        List<ReassignSubJobPO> subJobPOList = this.getSubJobsByJobId(jobId);
        for (ReassignSubJobPO subJobPO: subJobPOList) {
            Result<ReassignSubJobExtendData> extendDataResult = this.getReassignSubJobExtendData(subJobPO);
            if (extendDataResult.failed()) {
                continue;
            }

            ReassignSubJobPO newSubJobPO = new ReassignSubJobPO();
            newSubJobPO.setId(subJobPO.getId());
            newSubJobPO.setExtendData(ConvertUtil.obj2Json(extendDataResult.getData()));
            reassignSubJobDAO.updateById(newSubJobPO);
        }

        return Result.buildSuc();
    }

    @Override
    public List<ReassignSubJobPO> getSubJobsByJobId(Long jobId) {
        if (jobId == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<ReassignSubJobPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReassignSubJobPO::getJobId, jobId);

        return reassignSubJobDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public Result<ReassignJobDetail> getJobDetailsGroupByTopic(Long jobId) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        // 获取任务
        ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }

        // 获取子任务
        List<ReassignSubJobPO> subJobPOList = this.getSubJobsByJobId(jobId);

        // 数据组装并放回
        return Result.buildSuc(ReassignConverter.convert2ReassignJobDetail(jobPO, subJobPOList));
    }

    @Override
    public Long getOneRunningJobId(Long clusterPhyId) {
        LambdaQueryWrapper<ReassignJobPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReassignJobPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(ReassignJobPO::getStatus, JobStatusEnum.RUNNING.getStatus());

        List<ReassignJobPO> poList = reassignJobDAO.selectList(lambdaQueryWrapper);
        if (!ValidateUtils.isEmptyList(poList)) {
            // 默认获取第一个
            return poList.get(0).getId();
        }

        // 获取子任务中待执行的任务，避免主任务和子任务状态不一致
        LambdaQueryWrapper<ReassignSubJobPO> subLambdaQueryWrapper = new LambdaQueryWrapper<>();
        subLambdaQueryWrapper.eq(ReassignSubJobPO::getClusterPhyId, clusterPhyId);
        subLambdaQueryWrapper.eq(ReassignSubJobPO::getStatus, JobStatusEnum.RUNNING.getStatus());
        List<ReassignSubJobPO> subPOList = reassignSubJobDAO.selectList(subLambdaQueryWrapper);
        if (ValidateUtils.isEmptyList(subPOList)) {
            return null;
        }

        return subPOList.get(0).getJobId();
    }

    @Override
    public Result<Void> preferredReplicaElection(Long jobId) {
        // 获取任务
        ReassignJobPO jobPO = reassignJobDAO.selectById(jobId);
        if (jobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getJobNotExist(jobId));
        }
        if (!JobStatusEnum.isFinished(jobPO.getStatus())){
            return Result.buildSuc();
        }

        // 获取子任务
        List<ReassignSubJobPO> subJobPOList = this.getSubJobsByJobId(jobId);
        List<TopicPartition> topicPartitions = new ArrayList<>();
        subJobPOList.stream().forEach(reassignPO -> {
            //替换过leader的添加到优先副本选举任务列表
            if (!CommonUtils.checkFirstElementIsEquals(reassignPO.getReassignBrokerIds(), reassignPO.getOriginalBrokerIds())) {
                topicPartitions.add(new TopicPartition(reassignPO.getTopicName(), reassignPO.getPartitionId()));
            }
        });

        //无论优先副本选举是否成功，都返回成功，以保证job的数据更新
        if (!topicPartitions.isEmpty()) {
            opPartitionService.preferredReplicaElection(jobPO.getClusterPhyId(), topicPartitions);
        }

        return Result.buildSuc();
    }


    /**************************************************** private method ****************************************************/


    private Result<Void> modifyAll(ReassignJobPO jobPO, ReplaceReassignJob replaceReassignJob) {
        // DB中的sub-job
        Map<TopicPartition, ReassignSubJobPO> tpSubJobMap = new HashMap<>();
        this.getSubJobsByJobId(jobPO.getId()).forEach(elem ->
            tpSubJobMap.put(new TopicPartition(elem.getTopicName(), elem.getPartitionId()), elem)
        );

        // 当前变更的sub-job
        List<ReassignSubJobPO> subJobPOList = ReassignConverter.convert2ReassignSubJobPOList(jobPO.getId(), replaceReassignJob.getSubJobList());
        subJobPOList.forEach(elem -> {
            ReassignSubJobPO dbSubPO = tpSubJobMap.remove(new TopicPartition(elem.getTopicName(), elem.getPartitionId()));
            if (dbSubPO == null) {
                // DB中不存在
                reassignSubJobDAO.insert(elem);
                return;
            }

            //补全缺失信息
            this.completeInfo(elem,dbSubPO);

            // 已存在则进行更新
            elem.setId(dbSubPO.getId());
            reassignSubJobDAO.updateById(elem);
        });

        // 移除被删除的tp
        for (ReassignSubJobPO subJobPO: tpSubJobMap.values()) {
            reassignSubJobDAO.deleteById(subJobPO.getId());
        }

        // 修改Job
        reassignJobDAO.updateById(ReassignConverter.convert2ReassignJobPO(jobPO.getId(), replaceReassignJob, jobPO.getCreator()));

        return Result.buildSuc();
    }

    private Result<Void> buildActionForbidden(Long jobId, Integer jobStatus) {
        return Result.buildFromRSAndMsg(
                ResultStatus.OPERATION_FORBIDDEN,
                String.format("jobId:[%d] 当前 status:[%s], 不允许被执行", jobId, JobStatusEnum.valueOfStatus(jobStatus))
        );
    }


    @Transactional
    public Result<Void> checkAndSetSuccessIfFinished(ReassignJobPO jobPO, ReassignResult reassignmentResult) {
        long now = System.currentTimeMillis();

        boolean existNotFinished = false;
        boolean jobSucceed = false;
        List<ReassignSubJobPO> subJobPOList = this.getSubJobsByJobId(jobPO.getId());

        for (ReassignSubJobPO subJobPO: subJobPOList) {

            if (!reassignmentResult.checkPartitionFinished(subJobPO.getTopicName(), subJobPO.getPartitionId())) {
                existNotFinished = true;
                continue;
            }

            // 更新状态
            ReassignSubJobPO newSubJobPO = new ReassignSubJobPO();
            newSubJobPO.setId(subJobPO.getId());
            newSubJobPO.setStatus(JobStatusEnum.SUCCESS.getStatus());
            newSubJobPO.setFinishedTime(new Date(now));
            reassignSubJobDAO.updateById(newSubJobPO);
        }

        // 更新任务状态
        if (!existNotFinished && !reassignmentResult.isPartsOngoing()) {
            // 当前没有分区处于迁移中， 并且没有任务并不处于执行中
            ReassignJobPO newJobPO = new ReassignJobPO();
            newJobPO.setId(jobPO.getId());
            jobSucceed = true;
            newJobPO.setStatus(JobStatusEnum.SUCCESS.getStatus());
            newJobPO.setFinishedTime(new Date(now));
            reassignJobDAO.updateById(newJobPO);
        }

        return Result.build(jobSucceed);
    }

    private Result<List<ReassignSubJobPO>> setJobInRunning(ReassignJobPO jobPO) {
        long now = System.currentTimeMillis();

        // 更新子任务状态
        List<ReassignSubJobPO> subJobPOList = this.getSubJobsByJobId(jobPO.getId());
        for (ReassignSubJobPO subJobPO: subJobPOList) {
            ReassignSubJobPO newSubJobPO = new ReassignSubJobPO();
            newSubJobPO.setId(subJobPO.getId());
            newSubJobPO.setStatus(JobStatusEnum.RUNNING.getStatus());
            newSubJobPO.setStartTime(new Date(now));

            reassignSubJobDAO.updateById(newSubJobPO);
        }

        // 更新父任务状态
        ReassignJobPO newJobPO = new ReassignJobPO();
        newJobPO.setId(jobPO.getId());
        newJobPO.setStatus(JobStatusEnum.RUNNING.getStatus());
        newJobPO.setStartTime(new Date(now));
        reassignJobDAO.updateById(newJobPO);

        return Result.buildSuc(subJobPOList);
    }


    private Result<Void> setJobCanceled(ReassignJobPO jobPO) {
        // 更新子任务状态
        List<ReassignSubJobPO> subJobPOList = this.getSubJobsByJobId(jobPO.getId());
        for (ReassignSubJobPO subJobPO: subJobPOList) {
            ReassignSubJobPO newSubJobPO = new ReassignSubJobPO();
            newSubJobPO.setId(subJobPO.getId());
            newSubJobPO.setStatus(JobStatusEnum.CANCELED.getStatus());
            reassignSubJobDAO.updateById(newSubJobPO);
        }

        // 更新父任务状态
        ReassignJobPO newJobPO = new ReassignJobPO();
        newJobPO.setId(jobPO.getId());
        newJobPO.setStatus(JobStatusEnum.CANCELED.getStatus());
        reassignJobDAO.updateById(newJobPO);

        return Result.buildSuc();
    }


    private Result<Void> checkParamLegalAndModifyOriginData(Long jobId, ReplaceReassignJob replaceReassignJob, String creator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, MsgConstant.getJobIdCanNotNull());
        }

        if (ValidateUtils.isBlank(creator)) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "creator不允许为空");
        }

        String reassignmentJson = ReassignConverter.convert2ReassignmentJson(replaceReassignJob);

        // 检查生成的迁移Json是否合法
        Result<Void> rv = reassignService.parseExecuteAssignmentArgs(replaceReassignJob.getClusterPhyId(), reassignmentJson);
        if (rv.failed()) {
            return rv;
        }

        // 检查集群是否存在
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(replaceReassignJob.getClusterPhyId());
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(replaceReassignJob.getClusterPhyId()));
        }

        // 集群Broker集合
        Set<Integer> brokerIdSet = brokerService.listAllBrokersFromDB(clusterPhy.getId()).stream().map(elem -> elem.getBrokerId()).collect(Collectors.toSet());

        // 集群Topic集合
        Map<String, Topic> topicMap = topicService.listTopicsFromDB(clusterPhy.getId()).stream().collect(Collectors.toMap(Topic::getTopicName, Function.identity()));

        for (ReplaceReassignSubJob subJob: replaceReassignJob.getSubJobList()) {
            if (!replaceReassignJob.getClusterPhyId().equals(subJob.getClusterPhyId())) {
                return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "一个任务中，不能同时存在不同集群的Topic同时进行迁移");
            }

            for (Integer brokerId: subJob.getReassignBrokerIdList()) {
                if (!brokerIdSet.contains(brokerId)) {
                    // Broker不存在
                    return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getBrokerNotExist(subJob.getClusterPhyId(), brokerId));
                }
            }

            Topic topic = topicMap.get(subJob.getTopicName());
            if (topic == null) {
                // Topic不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(subJob.getClusterPhyId(), subJob.getTopicName()));
            }

            if (!topic.getPartitionMap().containsKey(subJob.getPartitionId())) {
                // 分区不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getPartitionNotExist(subJob.getClusterPhyId(), subJob.getTopicName(), subJob.getPartitionId()));
            }

            subJob.setOriginReplicaNum(topic.getPartitionMap().get(subJob.getPartitionId()).size());
            subJob.setOriginalBrokerIdList(topic.getPartitionMap().get(subJob.getPartitionId()));
        }
        return Result.buildSuc();
    }

    private Result<ReassignSubJobExtendData> getReassignSubJobExtendData(ReassignSubJobPO subJobPO) {
        // 获取分区信息
        Partition partition = partitionService.getPartitionByTopicAndPartitionId(
                subJobPO.getClusterPhyId(),
                subJobPO.getTopicName(),
                subJobPO.getPartitionId()
        );

        if (partition == null) {
            // 分区不存在，直接返回错误
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getPartitionNotExist(subJobPO.getClusterPhyId(),
                    subJobPO.getTopicName(),
                    subJobPO.getPartitionId())
            );
        }

        // 获取leader副本
        Float leaderLogSize = this.getReplicaLogSize(subJobPO.getClusterPhyId(),
                partition.getLeaderBrokerId(),
                subJobPO.getTopicName(),
                subJobPO.getPartitionId()
        );

        // 获取新增的副本
        Set<Integer> newReplicas = new HashSet<>(CommonUtils.string2IntList(subJobPO.getReassignBrokerIds()));
        newReplicas.removeAll(CommonUtils.string2IntList(subJobPO.getOriginalBrokerIds()));

        // 遍历新增的副本，计算当前已经完成的迁移Size
        Long finishedLogSizeUnitB = 0L;
        for (Integer brokerId: newReplicas) {
            Float replicaLogSize = this.getReplicaLogSize(subJobPO.getClusterPhyId(),
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

        // 计算剩余时间
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

        // 数据修正
        if (JobStatusEnum.isFinished(subJobPO.getStatus())) {
            // 如果任务已经完成了：
            // 1、则将需迁移LogSize直接设置为需要迁移的LogSize
            // 2. 将剩余时间设置为0
            extendData.setFinishedReassignLogSizeUnitB(extendData.getNeedReassignLogSizeUnitB());
            extendData.setRemainTimeUnitMs(0L);
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

    /**
     * 还原保存时间
     */
    private Result<Void> recoveryRetentionTime(ReassignJobPO jobPO, ReassignResult reassignmentResult) {
        Map<String, Long> finishedTopicRetentionTimeMap = new HashMap<>();

        List<ReassignSubJobPO> subJobPOList = this.getSubJobsByJobId(jobPO.getId());
        for (ReassignSubJobPO subJobPO: subJobPOList) {
            ReassignSubJobExtendData extendData = ConvertUtil.str2ObjByJson(subJobPO.getExtendData(), ReassignSubJobExtendData.class);
            if (extendData == null
                    || extendData.getOriginalRetentionTimeUnitMs() == null
                    || extendData.getReassignRetentionTimeUnitMs() == null
                    || extendData.getOriginalRetentionTimeUnitMs().equals(extendData.getReassignRetentionTimeUnitMs())) {
                // 不存在扩展数据，或者这个时间是不需要调整的，则直接跳过
                continue;
            }

            finishedTopicRetentionTimeMap.put(subJobPO.getTopicName(), extendData.getOriginalRetentionTimeUnitMs());
        }

        // 仅保留已经迁移完成的Topic
        for (ReassignSubJobPO subJobPO: subJobPOList) {
            if (!reassignmentResult.checkPartitionFinished(subJobPO.getTopicName(), subJobPO.getPartitionId())) {
                // 移除未完成的Topic
                finishedTopicRetentionTimeMap.remove(subJobPO.getTopicName());
            }
        }

        // 还原迁移完成的Topic的保存时间
        for (Map.Entry<String, Long> entry: finishedTopicRetentionTimeMap.entrySet()) {
            Map<String, String> changedProps = new HashMap<>();
            changedProps.put(TopicConfig0100.RETENTION_MS_CONFIG, String.valueOf(entry.getValue()));

            Result<Void> rv = topicConfigService.modifyTopicConfig(new KafkaTopicConfigParam(jobPO.getClusterPhyId(), entry.getKey(), changedProps), jobPO.getCreator());
            if (rv == null || rv.failed()) {
                return rv;
            }
        }

        return Result.buildSuc();
    }

    private Result<Void> modifyRetentionTime(Long clusterPhyId, List<ReassignSubJobPO> subJobPOList, String operator) {
        Map<String, Long> needModifyTopicRetentionTimeMap = new HashMap<>();
        for (ReassignSubJobPO subJobPO: subJobPOList) {
            ReassignSubJobExtendData extendData = ConvertUtil.str2ObjByJson(subJobPO.getExtendData(), ReassignSubJobExtendData.class);
            if (extendData == null
                    || extendData.getOriginalRetentionTimeUnitMs() == null
                    || extendData.getReassignRetentionTimeUnitMs() == null
                    || extendData.getOriginalRetentionTimeUnitMs().equals(extendData.getReassignRetentionTimeUnitMs())) {
                // 不存在扩展数据，或者这个时间是不需要调整的，则直接跳过
                continue;
            }

            needModifyTopicRetentionTimeMap.put(subJobPO.getTopicName(), extendData.getReassignRetentionTimeUnitMs());
        }

        // 修改Topic的保存时间
        Result<Void> returnRV = Result.buildSuc();
        for (Map.Entry<String, Long> entry: needModifyTopicRetentionTimeMap.entrySet()) {
            Map<String, String> changedProps = new HashMap<>();
            changedProps.put(TopicConfig0100.RETENTION_MS_CONFIG, String.valueOf(entry.getValue()));

            Result<Void> rv = topicConfigService.modifyTopicConfig(new KafkaTopicConfigParam(clusterPhyId, entry.getKey(), changedProps), operator);
            if (rv == null || rv.failed()) {
                returnRV = rv;
            }
        }

        return returnRV;
    }

    private void completeInfo(ReassignSubJobPO newPO, ReassignSubJobPO dbPO) {
        if (newPO.getJobId() == null) {
            newPO.setJobId(dbPO.getJobId());
        }
        if (newPO.getTopicName() == null) {
            newPO.setTopicName(dbPO.getTopicName());
        }
        if (newPO.getClusterPhyId() == null) {
            newPO.setClusterPhyId(dbPO.getClusterPhyId());
        }
        if (newPO.getPartitionId() == null) {
            newPO.setPartitionId(dbPO.getPartitionId());
        }
        if (newPO.getOriginalBrokerIds() == null || newPO.getOriginalBrokerIds().isEmpty()) {
            newPO.setOriginalBrokerIds(dbPO.getOriginalBrokerIds());
        }
        if (newPO.getReassignBrokerIds() == null || newPO.getReassignBrokerIds().isEmpty()) {
            newPO.setReassignBrokerIds(dbPO.getReassignBrokerIds());
        }
    }
}
