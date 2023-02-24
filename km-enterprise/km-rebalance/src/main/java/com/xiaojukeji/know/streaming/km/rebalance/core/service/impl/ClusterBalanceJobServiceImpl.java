package com.xiaojukeji.know.streaming.km.rebalance.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.google.common.collect.Lists;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.BrokerSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.reassign.ExecuteReassignParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReassignExecutionStrategy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.strategy.ReassignTask;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.ClusterBalanceInterval;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.detail.ClusterBalancePlanDetail;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobConfigPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceReassignPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo.ClusterBalanceHistorySubVO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo.ClusterBalanceHistoryVO;
import com.xiaojukeji.know.streaming.km.rebalance.common.converter.ClusterBalanceConverter;
import com.xiaojukeji.know.streaming.km.rebalance.common.converter.ClusterBalanceReassignConverter;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerSpecService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.config.ConfigUtils;
import com.xiaojukeji.know.streaming.km.core.service.partition.OpPartitionService;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignService;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignStrategyService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.rebalance.persistence.ClusterBalanceJobDao;
import com.xiaojukeji.know.streaming.km.rebalance.persistence.ClusterBalanceReassignDao;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.ExecutionRebalance;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.BrokerBalanceState;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceJobConfigService;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceJobService;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceReassignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@EnterpriseLoadReBalance
public class ClusterBalanceJobServiceImpl implements ClusterBalanceJobService {
    private static final ILog logger = LogFactory.getLog(ClusterBalanceJobServiceImpl.class);

    @Value("${es.client.address}")
    private String                          esAddress;

    @Autowired
    private ClusterBalanceJobDao clusterBalanceJobDao;

    @Autowired
    private ClusterBalanceReassignDao clusterBalanceReassignDao;

    @Autowired
    private ClusterBalanceReassignService clusterBalanceReassignService;

    @Autowired
    private ClusterBalanceJobConfigService clusterBalanceJobConfigService;

    @Autowired
    private BrokerSpecService brokerSpecService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ConfigUtils configUtils;

    @Autowired
    private ReassignService reassignService;

    @Autowired
    private ReassignStrategyService reassignStrategyService;

    @Autowired
    private OpPartitionService opPartitionService;

    @Override
    public Result<Void> deleteByJobId(Long jobId, String operator) {
        if (jobId == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "jobId不允许为空");
        }

        try {
            ClusterBalanceJobPO jobPO = clusterBalanceJobDao.selectById(jobId);
            if (jobPO == null) {
                // 任务不存在
                return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, String.format("jobId:[%d] not exist", jobId));
            }

            if (JobStatusEnum.canNotDeleteJob(jobPO.getStatus())) {
                // 状态错误，禁止执行
                return this.buildActionForbidden(jobId, jobPO.getStatus());
            }

            clusterBalanceJobDao.deleteById(jobId);

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
    public Result<Void> createClusterBalanceJob(ClusterBalanceJobPO clusterBalanceJobPO, String operator) {
        if (ValidateUtils.isNull(clusterBalanceJobPO)){
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }
        try {
            clusterBalanceJobDao.addClusterBalanceJob(clusterBalanceJobPO);
        }catch (Exception e){
            logger.error("method=createClusterBalanceJob||clusterBalanceJobPO:{}||errMsg=exception", clusterBalanceJobPO, e);
            return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);

        }
        return Result.buildSuc();
    }

    @Override
    public Result<Void> modifyClusterBalanceJob(ClusterBalanceJobPO clusterBalanceJobPO, String operator) {

        ClusterBalanceJobPO oldJobPo = clusterBalanceJobDao.selectById(clusterBalanceJobPO.getId());
        if (oldJobPo == null){
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }
        try {
            int count = clusterBalanceJobDao.updateById(clusterBalanceJobPO);
            if (count < 1){
                logger.error("method=modifyClusterBalanceJob||clusterBalanceJobPO:{}||errMsg=modify clusterBalanceJob failed", clusterBalanceJobPO);
                return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
            }
        }catch (Exception e){
            logger.error("method=modifyClusterBalanceJob||clusterBalanceJobPO:{}||errMsg=exception", clusterBalanceJobPO, e);
            return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
        }
        return Result.buildSuc();
    }

    @Override
    public Result<ClusterBalanceJobPO> getClusterBalanceJobById(Long id) {
        return Result.buildSuc(clusterBalanceJobDao.selectById(id));
    }

    @Override
    public ClusterBalanceJobPO getLastOneByClusterId(Long clusterPhyId) {
        ClusterBalanceJobPO clusterBalanceJobPO = new ClusterBalanceJobPO();
        clusterBalanceJobPO.setClusterId(clusterPhyId);
        QueryWrapper<ClusterBalanceJobPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(clusterBalanceJobPO);
        queryWrapper.orderByDesc("id");
        List<ClusterBalanceJobPO> clusterBalanceJobPOS = clusterBalanceJobDao.selectList(queryWrapper);
        if (clusterBalanceJobPOS.isEmpty()){
            return null;
        }
        return clusterBalanceJobPOS.get(0);
    }

    @Override
    public Map<String, Double> getBalanceInterval(Long clusterPhyId) {
        Result<ClusterBalanceJobConfigPO>  configPOResult = clusterBalanceJobConfigService.getByClusterId(clusterPhyId);
        if (!configPOResult.hasData()){
            return new HashMap();
        }
        List<ClusterBalanceInterval> clusterBalanceIntervals = ConvertUtil.str2ObjArrayByJson(configPOResult.getData().getBalanceIntervalJson(), ClusterBalanceInterval.class);

        return clusterBalanceIntervals.stream().collect(Collectors.toMap(ClusterBalanceInterval::getType,ClusterBalanceInterval::getIntervalPercent));
    }

    @Override
    public PaginationResult<ClusterBalanceHistoryVO> page(Long clusterPhyId, PaginationBaseDTO dto) {
        List<ClusterBalanceHistoryVO> historyVOS = new ArrayList<>();

        LambdaQueryWrapper<ClusterBalanceJobPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClusterBalanceJobPO::getClusterId, clusterPhyId);
        List<Integer> status = Lists.newArrayList(JobStatusEnum.SUCCESS.getStatus(), JobStatusEnum.CANCELED.getStatus(), JobStatusEnum.FAILED.getStatus());
        queryWrapper.in(ClusterBalanceJobPO::getStatus, status);
        queryWrapper.orderByDesc(ClusterBalanceJobPO::getStartTime);

        IPage<ClusterBalanceJobPO> page = clusterBalanceJobDao.selectPage(new Page<>(dto.getPageNo(), dto.getPageSize()), queryWrapper);
        page.setTotal(clusterBalanceJobDao.selectCount(queryWrapper));

        for (ClusterBalanceJobPO clusterBalanceJobPO : page.getRecords()){
            ClusterBalanceHistoryVO clusterBalanceHistoryVO = new ClusterBalanceHistoryVO();
            clusterBalanceHistoryVO.setBegin(clusterBalanceJobPO.getStartTime());
            clusterBalanceHistoryVO.setEnd(clusterBalanceJobPO.getFinishedTime());
            clusterBalanceHistoryVO.setJobId(clusterBalanceJobPO.getId());

            List<ClusterBalancePlanDetail> detailVOS = ConvertUtil.str2ObjArrayByJson(clusterBalanceJobPO.getBrokerBalanceDetail(), ClusterBalancePlanDetail.class);
            Map<String, ClusterBalanceHistorySubVO> subMap = new HashMap<>();
            ClusterBalanceHistorySubVO diskSubVO = new ClusterBalanceHistorySubVO();
            diskSubVO.setSuccessNu(detailVOS.stream().filter(clusterBalancePlanDetail -> clusterBalancePlanDetail.getDiskStatus() != null && clusterBalancePlanDetail.getDiskStatus() == 0).count());
            diskSubVO.setFailedNu(detailVOS.stream().filter(clusterBalancePlanDetail -> clusterBalancePlanDetail.getDiskStatus() != null && clusterBalancePlanDetail.getDiskStatus() != 0).count());
            subMap.put(Resource.DISK.resource(), diskSubVO);

            ClusterBalanceHistorySubVO cupSubVO = new ClusterBalanceHistorySubVO();
            cupSubVO.setSuccessNu(detailVOS.stream().filter(clusterBalancePlanDetail -> clusterBalancePlanDetail.getCpuStatus() != null && clusterBalancePlanDetail.getCpuStatus() == 0).count());
            cupSubVO.setFailedNu(detailVOS.stream().filter(clusterBalancePlanDetail -> clusterBalancePlanDetail.getCpuStatus() != null && clusterBalancePlanDetail.getCpuStatus() != 0).count());
            subMap.put(Resource.CPU.resource(), cupSubVO);

            ClusterBalanceHistorySubVO bytesInSubVO = new ClusterBalanceHistorySubVO();
            bytesInSubVO.setSuccessNu(detailVOS.stream().filter(clusterBalancePlanDetail -> clusterBalancePlanDetail.getByteInStatus() != null && clusterBalancePlanDetail.getByteInStatus() == 0).count());
            bytesInSubVO.setFailedNu(detailVOS.stream().filter(clusterBalancePlanDetail -> clusterBalancePlanDetail.getByteInStatus() != null && clusterBalancePlanDetail.getByteInStatus() != 0).count());
            subMap.put(Resource.NW_IN.resource(), bytesInSubVO);

            ClusterBalanceHistorySubVO bytesOutSubVO = new ClusterBalanceHistorySubVO();
            bytesOutSubVO.setSuccessNu(detailVOS.stream().filter(clusterBalancePlanDetail -> clusterBalancePlanDetail.getByteOutStatus() != null && clusterBalancePlanDetail.getByteOutStatus() == 0).count());
            bytesOutSubVO.setFailedNu(detailVOS.stream().filter(clusterBalancePlanDetail -> clusterBalancePlanDetail.getByteOutStatus() != null && clusterBalancePlanDetail.getByteOutStatus() != 0).count());
            subMap.put(Resource.NW_OUT.resource(), bytesOutSubVO);

            clusterBalanceHistoryVO.setSub(subMap);

            historyVOS.add(clusterBalanceHistoryVO);
        }

        return PaginationResult.buildSuc(historyVOS, page);
    }

    @Override
    public Long getOneRunningJob(Long clusterPhyId) {
        LambdaQueryWrapper<ClusterBalanceJobPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ClusterBalanceJobPO::getClusterId, clusterPhyId);
        lambdaQueryWrapper.eq(ClusterBalanceJobPO::getStatus, JobStatusEnum.RUNNING.getStatus());

        List<ClusterBalanceJobPO> poList = clusterBalanceJobDao.selectList(lambdaQueryWrapper);
        if (!ValidateUtils.isEmptyList(poList)) {
            // 默认获取第一个
            return poList.get(0).getId();
        }

        // 获取子任务中待执行的任务，避免主任务和子任务状态不一致
        LambdaQueryWrapper<ClusterBalanceReassignPO> subLambdaQueryWrapper = new LambdaQueryWrapper<>();
        subLambdaQueryWrapper.eq(ClusterBalanceReassignPO::getClusterId, clusterPhyId);
        subLambdaQueryWrapper.eq(ClusterBalanceReassignPO::getStatus, JobStatusEnum.RUNNING.getStatus());
        List<ClusterBalanceReassignPO> subPOList = clusterBalanceReassignDao.selectList(subLambdaQueryWrapper);
        if (ValidateUtils.isEmptyList(subPOList)) {
            return null;
        }
        return subPOList.get(0).getJobId();
    }

    @Override
    @Transactional
    public Result<Void> verifyClusterBalanceAndUpdateStatue(Long jobId) {
        ClusterBalanceJobPO clusterBalanceJobPO = clusterBalanceJobDao.selectById(jobId);
        if (clusterBalanceJobPO == null) {
            // 任务不存在
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, String.format("jobId:[%d] not exist", jobId));
        }

        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterBalanceJobPO.getClusterId());
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        Result<Void> rv = reassignService.changReassignmentThrottles(
                new ExecuteReassignParam(clusterBalanceJobPO.getClusterId(), clusterBalanceJobPO.getReassignmentJson(), clusterBalanceJobPO.getThrottleUnitB())
        );
        if (rv.failed()) {
            logger.error("method=verifyClusterBalanceAndUpdateStatue||jobId={}||result={}||msg=change throttle failed", jobId, rv);
            return rv;
        }

        //获取规格信息
        Map<Integer, BrokerSpec>  brokerSpecMap = brokerSpecService.getBrokerSpecMap(clusterBalanceJobPO.getClusterId());
        //获取broker信息
        Map<Integer, Broker> brokerMap = brokerService.listAllBrokersFromDB(clusterBalanceJobPO.getClusterId()).stream().collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));

        //更新平衡任务状态信息
        List<String> topicNames = topicService.listRecentUpdateTopicNamesFromDB(clusterPhy.getId(), configUtils.getClusterBalanceIgnoredTopicsTimeSecond());
        Map<Integer, BrokerBalanceState> brokerBalanceStateMap = ExecutionRebalance
                .getBrokerResourcesBalanceState(ClusterBalanceConverter.convert2BalanceParameter(clusterBalanceJobPO, brokerMap, brokerSpecMap, clusterPhy, esAddress, topicNames));
        List<ClusterBalancePlanDetail> oldDetails = ConvertUtil.str2ObjArrayByJson(clusterBalanceJobPO.getBrokerBalanceDetail(), ClusterBalancePlanDetail.class);
        List<ClusterBalancePlanDetail> newDetails = ClusterBalanceConverter.convert2ClusterBalancePlanDetail(oldDetails, brokerBalanceStateMap);
        clusterBalanceJobPO.setBrokerBalanceDetail(ConvertUtil.obj2Json(newDetails));
        Result<Void> modifyResult = this.modifyClusterBalanceJob(clusterBalanceJobPO, Constant.SYSTEM);
        if (modifyResult.failed()){
            logger.error("method=verifyClusterBalanceAndUpdateStatue||jobId:{}||errMsg={}", jobId, modifyResult);
            return modifyResult;
        }

        //更新迁移任务状态信息
        Result<Boolean> result = clusterBalanceReassignService.verifyAndUpdateStatue(clusterBalanceJobPO);
        if (!result.hasData()){
            return Result.buildFromIgnoreData(result);
        }

        rv = clusterBalanceReassignService.preferredReplicaElection(jobId);
        if (rv.failed()){
            logger.error("method=verifyClusterBalanceAndUpdateStatue||jobId={}||result={}||msg=preferred replica election failed", jobId, rv);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return rv;
        }

        return Result.buildSuc();
    }

    @Override
    public Result<String> generateReassignmentJson(Long clusterId,
                                                   Integer parallelNum,
                                                   Integer executionStrategy,
                                                   Integer jsonVersion,
                                                   List<ClusterBalanceReassignPO> reassignPOList){
        Result<List<ReassignTask>> result = reassignStrategyService.generateReassignmentTask(
                new ReassignExecutionStrategy(
                        clusterId,
                        parallelNum,
                        executionStrategy,
                        ClusterBalanceReassignConverter.convert2ReplaceReassignSubList(reassignPOList)
                )
        );

        if (result.failed() || result.getData().isEmpty()){
            return Result.buildFromIgnoreData(result);
        }

        Map<String, Object> reassign = new HashMap<>();
        reassign.put(KafkaConstant.PARTITIONS, result.getData());
        reassign.put(KafkaConstant.VERSION, jsonVersion);
        String generateReassignmentJson = ConvertUtil.obj2Json(reassign);

        // 检查生成的迁移Json是否合法
        Result<Void> rv = reassignService.parseExecuteAssignmentArgs(clusterId, generateReassignmentJson);
        if (rv.failed()) {
            return Result.buildFromIgnoreData(rv);
        }
        return Result.buildSuc(generateReassignmentJson);
    }

    @Override
    @Transactional
    public Result<Void> generateReassignmentForStrategy(Long clusterPhyId, Long jobId) {
        ClusterBalanceJobPO job = clusterBalanceJobDao.selectById(jobId);
        if (job == null){
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST,
                    MsgConstant.getReassignJobBizStr(jobId, clusterPhyId));
        }
        if (!JobStatusEnum.isRunning(job.getStatus()) || job.getParallelNum() < 1){
            return Result.buildSuc();
        }

        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        List<ClusterBalanceReassignPO> reassignPOS = clusterBalanceReassignService.getBalanceReassignsByJobId(jobId);

        //2.4以内版本因为无法动态增加副本迁移，所以需要等一部分任务完成之后再根据并行度重新获下一部分迁移任务
        Double version = new Double(clusterPhy.getKafkaVersion().substring(0,3));
        if (version < 2.4 && reassignPOS.stream()
                .filter(reassignPO -> reassignPO.getStatus()==JobStatusEnum.RUNNING.getStatus()).count() > 0){
            return Result.buildSuc();
        }

        //过滤已完成子任务
        reassignPOS = reassignPOS.stream()
                .filter(reassignPO -> reassignPO.getStatus()==JobStatusEnum.RUNNING.getStatus()
                        ||reassignPO.getStatus()==JobStatusEnum.WAITING.getStatus())
                .collect(Collectors.toList());
        if (reassignPOS.isEmpty()){
            return Result.buildSuc();
        }

        Map<String, Object> reassign = JSON.parseObject(job.getBalanceIntervalJson());
        Result<String> r = this.generateReassignmentJson(job.getClusterId(),
                job.getParallelNum(),
                job.getExecutionStrategy(),
                reassign.get(KafkaConstant.VERSION)==null?1:(Integer)reassign.get(KafkaConstant.VERSION) + 1,
                reassignPOS);
        if (!r.hasData()){
            return Result.buildFromIgnoreData(r);
        }
        try {
            //更新任务json
            job.setReassignmentJson(r.getData());
            job.setUpdateTime(new Date());
            clusterBalanceJobDao.updateById(job);

            //更新任务状态
            modifyReassignStatus(r.getData(), reassignPOS);
        }catch (Exception e){
            logger.error("method=generateReassignmentForStrategy||jobId={}||errMsg=exception", jobId, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
        }

        return reassignService.executePartitionReassignments(
                new ExecuteReassignParam(job.getClusterId(), r.getData(), job.getThrottleUnitB()));

    }

    private Result<Void> buildActionForbidden(Long jobId, Integer jobStatus) {
        return Result.buildFromRSAndMsg(
                ResultStatus.OPERATION_FORBIDDEN,
                String.format("jobId:[%d] 当前 status:[%s], 不允许被执行", jobId, JobStatusEnum.valueOfStatus(jobStatus))
        );
    }

    private void modifyReassignStatus(String reassignmentJson, List<ClusterBalanceReassignPO> reassignPOS){
        Map<String, Object> reassign = JSON.parseObject(reassignmentJson);
        List<ReassignTask> reassignTasks = (List<ReassignTask>)reassign.get(KafkaConstant.PARTITIONS);
        if (reassignTasks == null || reassignTasks.isEmpty()){
            return;
        }

        // 更新子任务状态
        reassignTasks.forEach(reassignTask -> {
            for (ClusterBalanceReassignPO reassignPO: reassignPOS) {
                if (reassignPO.getStatus().equals(JobStatusEnum.WAITING.getStatus())
                        && reassignTask.getTopic().equals(reassignPO.getTopicName())
                        && reassignTask.getPartition() == reassignPO.getPartitionId()) {
                    ClusterBalanceReassignPO newReassignPO = new ClusterBalanceReassignPO();
                    newReassignPO.setId(reassignPO.getId());
                    newReassignPO.setStatus(JobStatusEnum.RUNNING.getStatus());
                    newReassignPO.setUpdateTime(new Date());
                    clusterBalanceReassignDao.updateById(newReassignPO);
                    break;
                }
            }
        });
    }

}
