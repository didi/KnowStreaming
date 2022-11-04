package com.xiaojukeji.know.streaming.km.core.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.dto.job.JobDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.job.JobPaginationDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.Job;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.JobStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobModifyDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.job.JobPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.*;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubBrokerJobVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobPartitionDetailVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobVO;
import com.xiaojukeji.know.streaming.km.common.component.HandleFactory;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobActionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobHandleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.job.JobHandler;
import com.xiaojukeji.know.streaming.km.core.service.job.JobService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.job.JobDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.*;
import static com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.BrokerMetricVersionItems.*;

@Service
public class JobServiceImpl implements JobService {
    private static final ILog LOGGER = LogFactory.getLog(JobServiceImpl.class);

    @Autowired
    private JobDAO jobDao;

    @Autowired
    private HandleFactory handleFactory;

    @Autowired
    private BrokerMetricService brokerMetricService;

    @Autowired
    private BrokerService brokerService;

    @Override
    @Transactional
    public Result<Void> addTask(Long clusterPhyId, JobDTO jobDTO, String operator) {
        JobTypeEnum typeEnum = JobTypeEnum.valueOfType(jobDTO.getJobType());
        if(JobTypeEnum.UNKNOWN.equals(typeEnum)){
            return Result.buildFailure("任务类型不存在");
        }

        Job job = ConvertUtil.obj2Obj(jobDTO, Job.class);
        job.setJobName(job.getJobName() == null? job.getJobDesc(): job.getJobName());
        job.setCreator(operator);
        job.setClusterId(clusterPhyId);
        job.setJobDesc(job.getJobDesc()== null ? "" : job.getJobDesc());
        job.setJobName(job.getJobName()== null ? job.getJobDesc() : job.getJobName());

        try {
            // 写入job表
            this.insert(job);

            JobHandler   handler    =  getJobHandlerByType(jobDTO.getJobType());
            Result<Void> handlerRet =  handler.submit(job, operator);
            if (handlerRet.failed()) {
                // 如果这一步出错了，则对上一步进行手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }

            return handlerRet;
        } catch (Exception e) {
            LOGGER.error("method=addTask||clusterPhyId={}||jobDTO={}||operator={}||errMsg=exception.", clusterPhyId, jobDTO, operator, e);

            return Result.buildFromRSAndMsg(OPERATION_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteById(Long clusterPhyId, Long jobId, String operator) {
        JobPO jobPO = jobDao.selectById(jobId);
        if(null == jobPO){return Result.buildFailure(NOT_EXIST);}

        JobHandler handler = getJobHandlerByType(jobPO.getJobType());

        Result<Void> ret = handler.delete(ConvertUtil.obj2Obj(jobPO, Job.class), operator);
        if(null != ret && ret.successful()){
            return Result.build(jobDao.deleteById(jobId) > 0);
        }

        return Result.buildFromIgnoreData(ret);
    }

    @Override
    public Result<Void> updateTask(Long clusterPhyId, JobDTO task, String operator) {
        Job job = getById(clusterPhyId, task.getId());
        if(null == job){return Result.buildFailure(NOT_EXIST);}

        if(JobStatusEnum.RUNNING.getStatus() == job.getJobStatus()){
            return Result.buildFailure("正在运行中的任务无法编辑！");
        }

        if(!StringUtils.isEmpty(task.getJobDesc())){
            job.setJobDesc(task.getJobDesc());
        }
        if(!StringUtils.isEmpty(task.getJobData())){
            job.setJobData(task.getJobData());
        }
        if(null != task.getPlanTime()){
            job.setPlanTime(task.getPlanTime());
        }

        JobHandler    jobHandler = getJobHandlerByType(job.getJobType());
        Result<Void>  handlerRet = jobHandler.modify(job, operator);
        if(null != handlerRet && handlerRet.successful()){
            JobPO jobPO = ConvertUtil.obj2Obj(task, JobPO.class);
            jobPO.setUpdateTime(new Date());
            return Result.build(jobDao.updateById(jobPO) > 0);
        }

        return Result.buildFailure(OPERATION_FAILED);
    }

    @Override
    public Job getById(Long clusterPhyId, Long id) {
        JobPO jobPO = jobDao.selectById(id);
        return ConvertUtil.obj2Obj(jobPO, Job.class);
    }

    @Override
    public Job getByClusterIdAndType(Long clusterPhyId, Integer type) {
        JobPO queryParam = new JobPO();
        queryParam.setClusterId(clusterPhyId);
        queryParam.setJobType(type);
        JobPO jobPO = jobDao.selectOne(new QueryWrapper(queryParam));
        if (jobPO == null){
            return null;
        }
        return ConvertUtil.obj2Obj(jobPO, Job.class);
    }

    @Override
    public Result<JobDetailVO> getJobDetail(Long clusterPhyId, Long jobId) {
        Job job = this.getById(clusterPhyId, jobId);
        if(null == job){
            return Result.buildFailure(NOT_EXIST);
        }

        JobHandler        jobHandler      = this.getJobHandlerByType(job.getJobType());
        Result<JobDetail> jobDetailResult = jobHandler.getTaskDetail(job);
        if (jobDetailResult.failed()) {
            return Result.buildFromIgnoreData(jobDetailResult);
        }

        return Result.buildSuc(ConvertUtil.obj2Obj(jobDetailResult.getData(), JobDetailVO.class));
    }

    @Override
    public Result<JobModifyDetailVO> getJobModifyDetail(Long clusterPhyId, Long jobId) {
        Job job = getById(clusterPhyId, jobId);
        if(null == job){
            return Result.buildFailure(NOT_EXIST);
        }

        JobHandler jobHandler = getJobHandlerByType(job.getJobType());
        Result<JobModifyDetail> jobDetailResult = jobHandler.getTaskModifyDetail(job);
        if (jobDetailResult.failed()) {
            return Result.buildFromIgnoreData(jobDetailResult);
        }

        return Result.buildSuc(ConvertUtil.obj2Obj(jobDetailResult.getData(), JobModifyDetailVO.class));
    }

    @Override
    public Result<List<SubJobPartitionDetailVO>> getSubJobPartitionDetail(Long clusterPhyId, Long jobId, String topic) {
        Job job = this.getById(clusterPhyId, jobId);
        if(null == job){
            return Result.buildFailure(NOT_EXIST);
        }

        JobHandler  jobHandler = getJobHandlerByType(job.getJobType());
        Result<List<SubJobPartitionDetailVO>> subJobPartitionDetailRet = jobHandler.getSubJobPartitionDetail(job, topic);
        if (subJobPartitionDetailRet.failed()) {
            return Result.buildFromIgnoreData(subJobPartitionDetailRet);
        }

        return Result.buildSuc(ConvertUtil.list2List(subJobPartitionDetailRet.getData(), SubJobPartitionDetailVO.class));
    }

    @Override
    public Result<List<JobTrafficBrokerVO>> getJobNodeTraffic(Long clusterPhyId, Long jobId) {
        Result<JobDetailVO> jobDetailVORet = this.getJobDetail(clusterPhyId, jobId);
        if(null == jobDetailVORet || jobDetailVORet.failed()){
            return Result.buildFail();
        }

        //1、获取任务的详情
        JobDetailVO jobDetailVO = jobDetailVORet.getData();
        if(null == jobDetailVO || CollectionUtils.isEmpty(jobDetailVO.getSubJobs())){
            return Result.buildFail();
        }

        Map<Integer, Tuple<List<Integer>/*source*/, List<Integer>/*des*/>> brokerSourceDesMap = new HashMap<>();
        Set<Integer> allBrokerIdSet = new HashSet<>();

        //2、从任务详情中获取到某个 broker 的流入流出目标 broker 列表
        for(SubJobVO subJobVO : jobDetailVO.getSubJobs()){
            SubBrokerJobVO subBrokerJobVO = (SubBrokerJobVO)subJobVO;

            List<Integer> desBrokerIds    = subBrokerJobVO.getDesBrokers();
            List<Integer> sourceBrokerIds = subBrokerJobVO.getSourceBrokers();

            allBrokerIdSet.addAll(desBrokerIds);
            allBrokerIdSet.addAll(sourceBrokerIds);

            brokerSourceDesMap = genBrokerSourceDesInfo(brokerSourceDesMap, desBrokerIds, sourceBrokerIds);
        }

        //3、获取brokerId、brokerHost
        Map<Integer, String> brokerIdHostMap = genBrokerIdHostMap(clusterPhyId, allBrokerIdSet);

        //4、获取所有 allBrokerIdSet 的指标信息
        Result<List<BrokerMetrics>> brokerMetricsRet = brokerMetricService.getLatestMetricsFromES(
                clusterPhyId, new ArrayList<>(allBrokerIdSet));

        if(null == brokerMetricsRet || brokerMetricsRet.failed() || CollectionUtils.isEmpty(brokerMetricsRet.getData())){
            return Result.buildFail();
        }

        //5、构建 JobTrafficBrokerVO 列表
        Map<Integer, Tuple<List<Integer>, List<Integer>>> finalBrokerSourceDesMap = brokerSourceDesMap;
        List<JobTrafficBrokerVO> jobTrafficBrokerVOS = brokerMetricsRet.getData().stream().map( b -> {
            JobTrafficBrokerVO jobTrafficBrokerVO = new JobTrafficBrokerVO();
            Integer brokerId = b.getBrokerId();
            jobTrafficBrokerVO.setId(jobId);
            jobTrafficBrokerVO.setBrokerId(brokerId);
            jobTrafficBrokerVO.setBrokerHost(brokerIdHostMap.get(brokerId));
            jobTrafficBrokerVO.setByteInTotal(b.getMetrics().get(BROKER_METRIC_BYTES_IN) != null?Double.valueOf(b.getMetrics().get(BROKER_METRIC_BYTES_IN)):0D);
            jobTrafficBrokerVO.setByteOutTotal(b.getMetrics().get(BROKER_METRIC_BYTES_OUT) != null?Double.valueOf(b.getMetrics().get(BROKER_METRIC_BYTES_OUT)):0D);
            jobTrafficBrokerVO.setByteInJob(b.getMetrics().get(BROKER_METRIC_REASSIGNMENT_BYTES_IN) != null?Double.valueOf(b.getMetrics().get(BROKER_METRIC_REASSIGNMENT_BYTES_IN)):0D);
            jobTrafficBrokerVO.setByteOutJob(b.getMetrics().get(BROKER_METRIC_REASSIGNMENT_BYTES_OUT) !=null?Double.valueOf(b.getMetrics().get(BROKER_METRIC_REASSIGNMENT_BYTES_OUT)):0D);
            jobTrafficBrokerVO.setInBrokers(getBrokerIdHostMap(brokerIdHostMap, finalBrokerSourceDesMap.get(brokerId).getV1()));
            jobTrafficBrokerVO.setOutBrokers(getBrokerIdHostMap(brokerIdHostMap, finalBrokerSourceDesMap.get(brokerId).getV2()));
            return jobTrafficBrokerVO;
        } ).collect( Collectors.toList());

        return Result.buildSuc(jobTrafficBrokerVOS);
    }

    @Override
    public PaginationResult<JobOverViewVO> pagingJobs(Long clusterPhyId, JobPaginationDTO dto) {
        LambdaQueryWrapper<JobPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobPO::getClusterId, clusterPhyId);
        queryWrapper.eq(-1 != dto.getType(), JobPO::getJobType, dto.getType());
        queryWrapper.like(!StringUtils.isEmpty(dto.getJobTarget()), JobPO::getTarget, dto.getJobTarget());
        queryWrapper.like(!StringUtils.isEmpty(dto.getCreator()), JobPO::getCreator, dto.getCreator());
        queryWrapper.in(!CollectionUtils.isEmpty(dto.getStatus()), JobPO::getJobStatus, dto.getStatus());
        queryWrapper.orderByAsc(JobPO::getJobStatus);
        queryWrapper.orderByDesc(JobPO::getPlanTime);

        IPage<JobPO> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        page.setTotal(jobDao.selectCount(queryWrapper));

        jobDao.selectPage(page, queryWrapper);

        return PaginationResult.buildSuc(jobPOS2JobVOS(page.getRecords()), page);
    }

    @Override
    public Result<JobStateVO> state(Long clusterPhyId) {
        List<Job> jobs = listAllJobByClusterId(clusterPhyId);

        int allNus  = jobs.size();
        int doingNu = 0;
        int waitingNu = 0;
        int successNu = 0;
        int failedNu = 0;

        for(Job job : jobs){
            if(JobStatusEnum.SUCCESS.getStatus() == job.getJobStatus()){
                successNu++;
            }else if(JobStatusEnum.RUNNING.getStatus() == job.getJobStatus()){
                doingNu++;
            }else if(JobStatusEnum.WAITING.getStatus() == job.getJobStatus()){
                waitingNu++;
            }else {
                failedNu++;
            }
        }

        return Result.buildSuc(new JobStateVO(allNus, doingNu, waitingNu, successNu, failedNu));
    }

    @Override
    public Result<Void> updateJobTrafficLimit(Long clusterPhyId, Long jobId, Long limit, String operator) {
        Job job = getById(clusterPhyId, jobId);
        if(null == job){
            return Result.buildFailure(NOT_EXIST);
        }

        JobHandler jobHandler = getJobHandlerByType(job.getJobType());
        return jobHandler.updateLimit(job, limit, operator);
    }

    @Override
    public void scheduleJobByClusterId(Long clusterPhyId) {
        startJob(clusterPhyId);
        updateStatusJob(clusterPhyId);
    }

    @Override
    public Integer countJobsByCluster(Long clusterPhyId) {
        LambdaQueryWrapper<JobPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(JobPO::getClusterId, clusterPhyId);
        return jobDao.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Integer countJobsByClusterAndJobStatus(Long clusterPhyId, Integer jobStatus) {
        LambdaQueryWrapper<JobPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(JobPO::getClusterId, clusterPhyId);
        lambdaQueryWrapper.eq(JobPO::getJobStatus, jobStatus);
        return jobDao.selectCount(lambdaQueryWrapper);
    }

    /**************************************************** private method ****************************************************/
    private void startJob(Long clusterPhyId){
        try {
            //1、该集群还有在执行的任务则不能继续执行
            List<Job> runningJobs = this.listJobByClusterIdAndStatusSortByPlanTime(clusterPhyId, JobStatusEnum.RUNNING.getStatus());
            if(!CollectionUtils.isEmpty(runningJobs)){
                LOGGER.debug("method=startJob||clusterPhyId={}||errMsg=no running job", clusterPhyId);
                return;
            }

            //2、获取所有等待需要执行的任务
            List<Job> waitingJobs = this.listJobByClusterIdAndStatusSortByPlanTime(clusterPhyId, JobStatusEnum.WAITING.getStatus());
            if(CollectionUtils.isEmpty(waitingJobs)){
                LOGGER.debug("method=startJob||clusterPhyId={}||errMsg=no waiting job", clusterPhyId);
                return;
            }

            //3、获取到 planTime 最近的一个任务，是否到达了任务执行时间
            Job job = waitingJobs.get(0);
            if(job.getPlanTime().after(new Date())){
                // 计划时间 > 当前时间，则直接返回
                return;
            }

            // 计划时间 < 当前时间，则触发任务的执行
            JobHandler jobHandler = getJobHandlerByType(job.getJobType());
            Result<Void> rv = jobHandler.process(job, JobActionEnum.START, Constant.DEFAULT_USER_NAME);
            if (rv.failed()) {
                LOGGER.error("method=startJob||clusterPhyId={}||errMsg=start job failed, {}!", clusterPhyId, rv.getMessage());
                return;
            }

            // 调整任务状态为运行中
            this.setJobStartRunning(job);
        }catch (Exception e){
            LOGGER.error("method=startJob||clusterPhyId={}||errMsg=exception!", clusterPhyId, e);
        }
    }

    private void setJobStartRunning(Job job) {
        JobHandler        jobHandler   = getJobHandlerByType(job.getJobType());
        Result<JobStatus> jobStatusRet = jobHandler.status(job);

        if (jobStatusRet == null
                || !jobStatusRet.successful()
                || JobStatusEnum.isWaiting(jobStatusRet.getData().getStatus())){
            return;
        }
        job.setJobStatus(jobStatusRet.getData().getStatus());
        job.setStartTime(new Date());

        job.setRunningStatus(JSON.toJSONString(jobStatusRet.getData()));
        jobDao.updateById(ConvertUtil.obj2Obj(job, JobPO.class));
    }

    private void updateStatusJob(Long clusterPhyId){
        List<Job> runningJobs = this.listJobByClusterIdAndStatusSortByPlanTime(clusterPhyId, JobStatusEnum.RUNNING.getStatus());
        List<Job> waitingJobs = this.listJobByClusterIdAndStatusSortByPlanTime(clusterPhyId, JobStatusEnum.WAITING.getStatus());

        List<Job> allJobs = new ArrayList<>();
        allJobs.addAll(runningJobs);
        allJobs.addAll(waitingJobs);

        if(CollectionUtils.isEmpty(allJobs)){
            // 当前无任务，则直接返回
            return;
        }

        for(Job job : allJobs){
            JobHandler        jobHandler   = getJobHandlerByType(job.getJobType());
            Result<JobStatus> jobStatusRet = jobHandler.status(job);

            if(null != jobStatusRet && jobStatusRet.successful()) {
                job.setJobStatus(jobStatusRet.getData().getStatus());
                job.setRunningStatus(JSON.toJSONString(jobStatusRet.getData()));
                jobDao.updateById(ConvertUtil.obj2Obj(job, JobPO.class));
            }
        }
    }

    private List<Job> listAllJobByClusterId(Long clusterPhyId){
        LambdaQueryWrapper<JobPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobPO::getClusterId, clusterPhyId);

        return ConvertUtil.list2List(jobDao.selectList(queryWrapper), Job.class);
    }

    private List<Job> listJobByClusterIdAndStatusSortByPlanTime(Long clusterPhyId, int status){
        LambdaQueryWrapper<JobPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobPO::getClusterId, clusterPhyId);
        queryWrapper.eq(JobPO::getJobStatus, status);
        queryWrapper.orderByAsc(JobPO::getPlanTime);

        return ConvertUtil.list2List(jobDao.selectList(queryWrapper), Job.class);
    }

    private JobHandler getJobHandlerByType(int type){
        JobHandleEnum taskHandleEnum = JobHandleEnum.valueOfType(type);
        JobHandler handler = (JobHandler) handleFactory.getByHandlerNamePer(taskHandleEnum.getMessage());

        return handler;
    }

    private boolean insert(Job task) {
        try {
            JobPO  jobPO = ConvertUtil.obj2Obj(task, JobPO.class);
            if (jobDao.addAndSetId( jobPO ) > 0) {
                task.setId(jobPO.getId());
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("method=insert||taskType={}||errMsg={}", task.getJobType(), e.getStackTrace(), e);
        }
        return false;
    }

    private Map<Integer, String> getBrokerIdHostMap(Map<Integer, String> brokerIdHostMap, List<Integer> brokerIds){
        Map<Integer, String> ret = new HashMap<>();

        for(Integer brokerId : brokerIds){
            ret.put(brokerId, brokerIdHostMap.get(brokerId));
        }

        return ret;
    }

    private Map<Integer, String> genBrokerIdHostMap(Long clusterPhyId, Set<Integer> brokerIds){
        Map<Integer, String> brokerIdHostMap = new HashMap<>();
        for(Integer brokerId : brokerIds){
            Broker broker = brokerService.getBroker(clusterPhyId, brokerId);
            if(null != broker){
                brokerIdHostMap.put(brokerId, broker.getHost());
            }
        }

        return brokerIdHostMap;
    }

    private Map<Integer, Tuple<List<Integer>/*source*/, List<Integer>/*des*/>> genBrokerSourceDesInfo(
            Map<Integer, Tuple<List<Integer>, List<Integer>>> brokerSourceDesMap,
            List<Integer> desBrokerIds, List<Integer> sourceBrokerIds){

        for(Integer desBrokerId : desBrokerIds){
            brokerSourceDesMap.putIfAbsent(desBrokerId, new Tuple<>(new ArrayList<>(), new ArrayList<>()));
            brokerSourceDesMap.get(desBrokerId).getV1().addAll(sourceBrokerIds);
        }

        for(Integer sourceBrokerId : sourceBrokerIds){
            brokerSourceDesMap.putIfAbsent(sourceBrokerId, new Tuple<>(new ArrayList<>(), new ArrayList<>()));
            brokerSourceDesMap.get(sourceBrokerId).getV2().addAll(desBrokerIds);
        }

        return brokerSourceDesMap;
    }

    private  List<JobOverViewVO> jobPOS2JobVOS(List<JobPO> jobPOS){
        if(CollectionUtils.isEmpty(jobPOS)){return new ArrayList<>();}

        return jobPOS.stream().map(j -> jobPO2JobVO(j)).collect(Collectors.toList());
    }

    private JobOverViewVO jobPO2JobVO(JobPO jobPO){
        JobOverViewVO jobOverViewVO = ConvertUtil.obj2Obj(jobPO, JobOverViewVO.class);
        JobStatus jobStatus = JSON.parseObject(jobPO.getRunningStatus(), JobStatus.class);
        if(null != jobStatus){
            jobOverViewVO.setDoing(jobStatus.getDoing());
            jobOverViewVO.setSuccess(jobStatus.getSuccess());
            jobOverViewVO.setFail(jobStatus.getFailed());
            jobOverViewVO.setTotal(jobStatus.getTotal());
            jobOverViewVO.setWaiting(jobStatus.getWaiting());
        }

        return jobOverViewVO;
    }

}
