package com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.job;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.BrokerSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.job.ClusterBalanceReassignDetail;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.job.detail.ClusterBalanceDetailDataGroupByTopic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.Job;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.JobStatus;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.entity.job.content.JobClusterBalanceContent;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.job.detail.JobModifyDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.po.ClusterBalanceJobPO;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.bean.po.ClusterBalanceReassignPO;
import com.xiaojukeji.know.streaming.km.common.bean.po.job.JobPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobPartitionDetailVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.JobConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.converter.ClusterBalanceConverter;
import com.xiaojukeji.know.streaming.km.common.enterprise.rebalance.converter.ClusterBalanceReassignConverter;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobActionEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerSpecService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service.ClusterBalanceJobService;
import com.xiaojukeji.know.streaming.km.core.enterprise.rebalance.service.ClusterBalanceReassignService;
import com.xiaojukeji.know.streaming.km.core.service.config.ConfigUtils;
import com.xiaojukeji.know.streaming.km.core.service.job.JobHandler;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.persistence.mysql.job.JobDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import com.xiaojukeji.know.streaming.km.rebalance.executor.ExecutionRebalance;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.BalanceParameter;
import com.xiaojukeji.know.streaming.km.rebalance.executor.common.OptimizerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@EnterpriseLoadReBalance
@Component(JobConstant.CLUSTER_BALANCE)
public class ClusterBalanceJobHandler implements JobHandler {

    private static final ILog logger = LogFactory.getLog(ClusterBalanceJobHandler.class);

    @Value("${es.client.address:}")
    private String                          esAddress;

    @Autowired
    private ClusterBalanceJobService clusterBalanceJobService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ClusterBalanceReassignService clusterBalanceReassignService;

    @Autowired
    private KafkaZKDAO kafkaZKDAO;

    @Autowired
    private JobDAO jobDAO;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private BrokerSpecService brokerSpecService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ConfigUtils configUtils;

    @Override
    public JobTypeEnum type() {
        return JobTypeEnum.CLUSTER_BALANCE;
    }

    @Override
    @Transactional
    public Result<Void> submit(Job job, String operator) {
        // 获取任务详情信息
        JobClusterBalanceContent dto = ConvertUtil.str2ObjByJson(job.getJobData(), JobClusterBalanceContent.class);
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(dto.getClusterId());
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        //获取broke规格信息
        Map<Integer, BrokerSpec> brokerSpecMap = brokerSpecService.getBrokerSpecMap(clusterPhy.getId());
        //获取集群所有broker信息
        List<Broker> brokers = brokerService.listAllBrokersFromDB(clusterPhy.getId());
        for(Broker broker:brokers){
            if (brokerSpecMap.get(broker.getBrokerId()) == null){
                return Result.buildFromRSAndMsg(ResultStatus.BROKER_SPEC_NOT_EXIST,String.format("Broker规格信息不存在:brokerId:%s", broker.getBrokerId()));
            }
        }

        //获取任务计划
        List<String> topicNames = topicService.listRecentUpdateTopicNamesFromDB(dto.getClusterId(), configUtils.getClusterBalanceIgnoredTopicsTimeSecond());
        BalanceParameter balanceParameter = ClusterBalanceConverter.convert2BalanceParameter(dto, brokers, brokerSpecMap, clusterPhy, esAddress, topicNames);
        try {
            ExecutionRebalance executionRebalance = new ExecutionRebalance();
            OptimizerResult optimizerResult = executionRebalance.optimizations(balanceParameter);
            Result cRs = checkOptimizerResult(optimizerResult, job.getId());
            if (cRs.failed()){
                return cRs;
            }

            Map<String, Topic> topicMap = topicService.listTopicsFromDB(clusterPhy.getId()).stream().collect(Collectors.toMap(Topic::getTopicName, Function.identity()));
            List<ClusterBalanceReassignPO> reassignPOS = ClusterBalanceConverter.convert2ListClusterBalanceReassignPO(
                    optimizerResult.resultTask(), topicMap, job.getId(), clusterPhy.getId());

            String generateReassignmentJson = optimizerResult.resultJsonTask();
            if (dto.getParallelNum() > 0){
                //根据执行策略生成迁移json
                Result<String> jResult = clusterBalanceJobService.generateReassignmentJson(job.getClusterId(),dto.getParallelNum(), dto.getExecutionStrategy(), Constant.NUM_ONE, reassignPOS);
                if (jResult.failed()){
                    return Result.buildFromIgnoreData(jResult);
                }
                generateReassignmentJson = jResult.getData();
            }

            //生成平衡job
            ClusterBalanceJobPO clusterBalanceJobPO = ClusterBalanceConverter.convert2ClusterBalanceJobPO(job.getId(), dto, optimizerResult, brokers, operator, generateReassignmentJson);
            Result<Void> result = clusterBalanceJobService.createClusterBalanceJob(clusterBalanceJobPO, operator);
            if (result.failed()){
                logger.error("method=clusterBalanceJobHandler.submit||job={}||errMsg={}!",
                        job, result.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return result;
            }

            //生成迁移明细
            Result<Void> cbrResult = clusterBalanceReassignService.addBatchBalanceReassign(reassignPOS);
            if (cbrResult.failed()){
                logger.error("method=clusterBalanceJobHandler.submit||job={}||errMsg={}!",
                        job, cbrResult.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return cbrResult;
            }

            //更新job执行对象
            job.setTarget(optimizerResult.resultOverview().getMoveTopics());
            int count = jobDAO.updateById(ConvertUtil.obj2Obj(job, JobPO.class));
            if (count < 0){
                logger.error("method=clusterBalanceJobHandler.submit||job={}||errMsg={}!",
                        job, result.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return Result.buildFrom(ResultStatus.MYSQL_OPERATE_FAILED);
            }

        }catch (Exception e){
            logger.error("method=clusterBalanceJobHandler.submit||job={}||errMsg=exception", job, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.buildFailure(e.getMessage());
        }
        return Result.buildSuc();
    }

    @Override
    @Transactional
    public Result<Void> delete(Job job, String operator) {
        //删除balanceJob
        Result<Void> balanceJobResult = clusterBalanceJobService.deleteByJobId(job.getId(), operator);
        if (balanceJobResult.failed()){
            logger.error("method=clusterBalanceJobHandler.delete||job={}||operator:{}||errMsg={}", job, operator, balanceJobResult);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return balanceJobResult;
        }
        return Result.buildSuc();
    }

    @Override
    public Result<Void> modify(Job job, String operator) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(job.getClusterId());
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }
        Result<ClusterBalanceJobPO> balanceJobPOResult = clusterBalanceJobService.getClusterBalanceJobById(job.getId());
        if (!balanceJobPOResult.hasData()){
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }

        List<Broker> brokers = brokerService.listAllBrokersFromDB(clusterPhy.getId());
        Map<Integer, BrokerSpec> brokerSpecMap = brokerSpecService.getBrokerSpecMap(clusterPhy.getId());

        List<String> topicNames = topicService.listRecentUpdateTopicNamesFromDB(job.getClusterId(), configUtils.getClusterBalanceIgnoredTopicsTimeSecond());
        JobClusterBalanceContent dto = ConvertUtil.str2ObjByJson(job.getJobData(), JobClusterBalanceContent.class);
        BalanceParameter balanceParameter = ClusterBalanceConverter.convert2BalanceParameter(dto, brokers, brokerSpecMap, clusterPhy, esAddress, topicNames);
        ExecutionRebalance executionRebalance = new ExecutionRebalance();
        try {
            OptimizerResult optimizerResult = executionRebalance.optimizations(balanceParameter);
            Result cRs = checkOptimizerResult(optimizerResult, job.getId());
            if (cRs.failed()){
                return cRs;
            }

            Map<String, Topic> topicMap = kafkaZKDAO.getAllTopicMetadata(clusterPhy.getId(), false).stream().collect(Collectors.toMap(Topic::getTopicName, Function.identity()));
            List<ClusterBalanceReassignPO> reassignPOS = ClusterBalanceConverter.convert2ListClusterBalanceReassignPO(optimizerResult.resultTask(),
                    topicMap, job.getId(), clusterPhy.getId());

            String generateReassignmentJson = optimizerResult.resultJsonTask();
            if (dto.getParallelNum() > 0){
                //根据执行策略生成迁移json
                Result<String> jResult = clusterBalanceJobService.generateReassignmentJson(job.getClusterId(),dto.getParallelNum(), dto.getExecutionStrategy(), Constant.NUM_ONE, reassignPOS);
                if (jResult.failed()){
                    return Result.buildFromIgnoreData(jResult);
                }
                generateReassignmentJson = jResult.getData();
            }
            //生成平衡job
            ClusterBalanceJobPO clusterBalanceJobPO = ClusterBalanceConverter.convert2ClusterBalanceJobPO(job.getId(), dto ,optimizerResult, brokers, operator, generateReassignmentJson);
            Result<Void> result = clusterBalanceJobService.modifyClusterBalanceJob(clusterBalanceJobPO, operator);
            if (result.failed()){
                return result;
            }

            //删除原迁移详情，生成新的迁移详情
            Result<Void> delReassignResult = clusterBalanceReassignService.delete(job.getId(), operator);
            if (delReassignResult.failed()){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return delReassignResult;
            }
            Result<Void> cbrResult = clusterBalanceReassignService.addBatchBalanceReassign(reassignPOS);
            if (cbrResult.failed()){
                logger.error("method=clusterBalanceJobHandler.submit||job={}||errMsg={}!",
                        job, cbrResult.getMessage());
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return cbrResult;
            }
        }catch (Exception e){
            logger.error("method=clusterBalanceJobHandler.modify||job={}||operator:{}||errMsg=exception", job, operator, e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
        return Result.buildSuc();
    }

    @Override
    public Result<Void> updateLimit(Job job, Long limit, String operator) {
        return clusterBalanceReassignService.modifyThrottle(job.getId(), limit, operator);
    }

    @Override
    public Result<Void> process(Job job, JobActionEnum action, String operator) {
        if (JobActionEnum.START.equals(action)) {
            return clusterBalanceReassignService.execute(job.getId());
        }

        if (JobActionEnum.CANCEL.equals(action)) {
            return clusterBalanceReassignService.cancel(job.getId());
        }

        // 迁移中，不支持该操作
        return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FORBIDDEN, String.format("不支持[%s]操作", action.getValue()));
    }

    @Override
    public Result<JobStatus> status(Job job) {
        // Topic下每个分区的状态
        Map<String, List<ClusterBalanceReassignPO>> topicJobsMap = new HashMap<>();

        // 获取子任务，并按照Topic进行聚合
        List<ClusterBalanceReassignPO> allSubJobPOList = clusterBalanceReassignService.getBalanceReassignsByJobId(job.getId());
        allSubJobPOList.forEach(elem -> {
            topicJobsMap.putIfAbsent(elem.getTopicName(), new ArrayList<>());
            topicJobsMap.get(elem.getTopicName()).add(elem);
        });

        // 获取每个Topic的状态
        List<Integer> topicStatusList = new ArrayList<>();
        for (List<ClusterBalanceReassignPO> topicJobPOList: topicJobsMap.values()) {
            topicStatusList.add(new JobStatus(
                    topicJobPOList.stream().map(elem -> elem.getStatus()).collect(Collectors.toList())
            ).getStatus());
        }

        // 聚合Topic的结果
        return Result.buildSuc(new JobStatus(topicStatusList));
    }

    @Override
    public Result<JobDetail> getTaskDetail(Job job) {
        Result<ClusterBalanceReassignDetail> detailResult = clusterBalanceReassignService.getJobDetailsGroupByTopic(job.getId());
        if (detailResult.failed()) {
            return Result.buildFromIgnoreData(detailResult);
        }

        return Result.buildSuc(ClusterBalanceReassignConverter.convert2JobDetail(job, detailResult.getData()));
    }

    @Override
    public Result<JobModifyDetail> getTaskModifyDetail(Job job) {
        // 获取任务详情信息
        JobClusterBalanceContent dto = ConvertUtil.str2ObjByJson(job.getJobData(), JobClusterBalanceContent.class);
        if (dto == null) {
            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "jobData格式错误");
        }

        JobModifyDetail detail = ConvertUtil.obj2Obj(job, JobModifyDetail.class);
        detail.setJobData(ConvertUtil.obj2Json(dto));
        return Result.buildSuc(detail);
    }

    @Override
    public Result<List<SubJobPartitionDetailVO>> getSubJobPartitionDetail(Job job, String topic) {
        Result<ClusterBalanceReassignDetail> detailResult = clusterBalanceReassignService.getJobDetailsGroupByTopic(job.getId());
        if (detailResult.failed()) {
            return Result.buildFromIgnoreData(detailResult);
        }

        List<ClusterBalanceDetailDataGroupByTopic> detailDataGroupByTopicList = detailResult.getData().getReassignTopicDetailsList()
                .stream()
                .filter(elem -> elem.getTopicName().equals(topic))
                .collect(Collectors.toList());

        if (detailDataGroupByTopicList.isEmpty()) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(job.getClusterId(), topic));
        }

        return Result.buildSuc(ClusterBalanceReassignConverter.convert2SubJobPartitionDetailVOList(detailDataGroupByTopicList.get(0)));
    }



    private Result<Void> checkOptimizerResult(OptimizerResult optimizerResult, Long jobId){
        if (optimizerResult == null){
            return Result.buildFrom(ResultStatus.KAFKA_OPERATE_FAILED);
        }
        if (optimizerResult.resultOverview().getMoveReplicas() == 0){
            logger.info("method=checkOptimizerResult||jobId:{}||msg=the cluster has reached equilibrium", jobId);
            return Result.buildFailure("该集群已达到均衡要求，不需要再执行均衡任务。");
        }
        return Result.buildSuc();
    }
}
