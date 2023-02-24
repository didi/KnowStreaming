package com.xiaojukeji.know.streaming.km.rebalance.core.service.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.utils.*;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerMetricService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerSpecService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.config.ConfigUtils;
import com.xiaojukeji.know.streaming.km.core.service.job.JobService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.BrokerMetricVersionItems;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalanceIntervalDTO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalanceOverviewDTO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalancePreviewDTO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalanceStrategyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.job.JobDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.BrokerSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.ClusterBalanceItemState;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.content.JobClusterBalanceContent;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.BrokerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobConfigPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo.*;
import com.xiaojukeji.know.streaming.km.rebalance.common.converter.ClusterBalanceConverter;
import com.xiaojukeji.know.streaming.km.rebalance.common.enums.ClusterBalanceStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobHandleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.ExecutionRebalance;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.BalanceParameter;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.BrokerBalanceState;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.OptimizerResult;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceJobConfigService;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceJobService;
import com.xiaojukeji.know.streaming.km.rebalance.core.service.ClusterBalanceService;
import org.apache.logging.log4j.core.util.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@EnterpriseLoadReBalance
public class ClusterBalanceServiceImpl implements ClusterBalanceService {
    private static final ILog logger = LogFactory.getLog(ClusterBalanceServiceImpl.class);

    @Value("${es.client.address}")
    private String                          esAddress;

    @Autowired
    private JobService jobService;

    @Autowired
    private ClusterBalanceJobService clusterBalanceJobService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private BrokerSpecService brokerSpecService;

    @Autowired
    private ClusterBalanceJobConfigService clusterBalanceJobConfigService;

    @Autowired
    private BrokerMetricService brokerMetricService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ConfigUtils configUtils;

    private final Cache<Long, Result<ClusterBalanceItemState>> balanceStateCache = Caffeine.newBuilder()
            .expireAfterWrite(150, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();

    @PostConstruct
    @Scheduled(cron = "0 0/2 * * * ?")
    private void flushClusterLatestMetricsCache() {
        for (ClusterPhy clusterPhy: clusterPhyService.listAllClusters()) {
            FutureUtil.quickStartupFutureUtil.submitTask(() -> this.updateCacheAndGetMetrics(clusterPhy.getId()));
        }
    }

    @Override
    public Result<ClusterBalanceStateVO> state(Long clusterPhyId) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        Result<ClusterBalanceJobConfigPO> configPOResult = clusterBalanceJobConfigService.getByClusterId(clusterPhyId);
        if(!configPOResult.hasData()){
            return Result.buildFromIgnoreData(configPOResult);
        }

        Map<Integer, Broker> brokerMap = brokerService.listAllBrokersFromDB(clusterPhy.getId()).stream().collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));
        Map<Integer, BrokerSpec> brokerSpecMap = brokerSpecService.getBrokerSpecMap(clusterPhy.getId());

        ClusterBalanceStateVO clusterBalanceStateVO = new ClusterBalanceStateVO();
        try {
            CronExpression cronExpression = new CronExpression(configPOResult.getData().getTaskCron());
            //是否到满足周期时间
            clusterBalanceStateVO.setNext(cronExpression.getTimeAfter(new Date()));
        } catch (ParseException e) {
            logger.error("method=state||clusterId:{}||errMsg=exception", clusterPhyId, e);
        }
        List<String> topicNames = topicService.listRecentUpdateTopicNamesFromDB(clusterPhyId, configUtils.getClusterBalanceIgnoredTopicsTimeSecond());

        clusterBalanceStateVO.setEnable(configPOResult.getData().getStatus() == 1);
        Map<Resource, Double> resourceDoubleMap;
        Map<Integer, BrokerBalanceState> brokerBalanceStateMap;
        try {
            resourceDoubleMap = ExecutionRebalance.getClusterAvgResourcesState(ClusterBalanceConverter.convert2BalanceParameter(configPOResult.getData(), brokerMap, brokerSpecMap, clusterPhy, esAddress, topicNames));
            brokerBalanceStateMap = ExecutionRebalance
                    .getBrokerResourcesBalanceState(ClusterBalanceConverter.convert2BalanceParameter(configPOResult.getData(), brokerMap, brokerSpecMap, clusterPhy, esAddress, topicNames));
        }catch (Exception e){
            logger.error("method=state||clusterPhyId={}||errMsg=exception", clusterPhyId, e);
            return Result.buildFailure(e.getMessage());
        }
        //集群状态信息
        ArrayList<BrokerBalanceState> balanceStates = new ArrayList(brokerBalanceStateMap.values());
        clusterBalanceStateVO.setStatus(ClusterBalanceStateEnum.BALANCE.getState());
        balanceStates.forEach(brokerBalanceState ->{
            if ((brokerBalanceState.getDiskBalanceState() != null && !brokerBalanceState.getDiskBalanceState().equals(ClusterBalanceStateEnum.BALANCE.getState()))
                    || (brokerBalanceState.getCpuBalanceState() != null && !brokerBalanceState.getCpuBalanceState().equals(ClusterBalanceStateEnum.BALANCE.getState()))
                    || (brokerBalanceState.getBytesOutBalanceState() != null && !brokerBalanceState.getBytesOutBalanceState().equals(ClusterBalanceStateEnum.BALANCE.getState()))
                    || (brokerBalanceState.getBytesInBalanceState() != null && !brokerBalanceState.getBytesInBalanceState().equals(ClusterBalanceStateEnum.BALANCE.getState()))){
                clusterBalanceStateVO.setStatus(ClusterBalanceStateEnum.UNBALANCED.getState());
            }
        });
        clusterBalanceStateVO.setSub(getStateSubVOMap(resourceDoubleMap, balanceStates, clusterPhyId));
        return Result.buildSuc(clusterBalanceStateVO);
    }

    @Override
    public Result<ClusterBalanceJobConfigVO> config(Long clusterPhyId) {
        Result<ClusterBalanceJobConfigPO> configPOResult = clusterBalanceJobConfigService.getByClusterId(clusterPhyId);
        if (!configPOResult.hasData()){
            return Result.buildFromIgnoreData(configPOResult);
        }
        return Result.buildSuc(ClusterBalanceConverter.convert2ClusterBalanceJobConfigVO(configPOResult.getData()));
    }

    @Override
    public PaginationResult<ClusterBalanceOverviewVO> overview(Long clusterPhyId, ClusterBalanceOverviewDTO dto) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null){
            return PaginationResult.buildFailure(ResultStatus.CLUSTER_NOT_EXIST, dto);
        }
        Result<ClusterBalanceJobConfigPO> configPOResult = clusterBalanceJobConfigService.getByClusterId(clusterPhyId);
        if(configPOResult.failed()){
            return PaginationResult.buildFailure(configPOResult, dto);
        }

        //获取规格信息
        Map<Integer, BrokerSpec>  brokerSpecMap = brokerSpecService.getBrokerSpecMap(clusterPhyId);
        List<ClusterBalanceOverviewVO> clusterBalanceOverviewVOS = new ArrayList<>();
        List<Broker> brokerList = brokerService.listAllBrokersFromDB(clusterPhyId);
        Map<Integer, Broker> brokerMap = brokerList.stream().collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));
        Map<Integer, BrokerBalanceState> brokerBalanceStateMap = new HashMap<>();
        if (configPOResult.hasData()) {
            try {
                List<String> topicNames = topicService.listRecentUpdateTopicNamesFromDB(clusterPhyId, configUtils.getClusterBalanceIgnoredTopicsTimeSecond());
                brokerBalanceStateMap = ExecutionRebalance
                        .getBrokerResourcesBalanceState(ClusterBalanceConverter.convert2BalanceParameter(configPOResult.getData(), brokerMap, brokerSpecMap, clusterPhy, esAddress, topicNames));
            } catch (Exception e) {
                logger.error("method=overview||clusterBalanceOverviewDTO={}||errMsg=exception", dto, e);
                return PaginationResult.buildFailure(e.getMessage(), dto);
            }
        }

        // 获取指标
        Result<List<BrokerMetrics>> metricsResult = brokerMetricService.getLatestMetricsFromES(
                clusterPhyId,
                brokerList.stream().filter(elem1 -> elem1.alive()).map(elem2 -> elem2.getBrokerId()).collect(Collectors.toList())
        );
        if (metricsResult.failed()){
            return PaginationResult.buildFailure(metricsResult, dto);
        }
        Map<Integer, BrokerMetrics> brokerMetricsMap = new HashMap<>();
        if (metricsResult.hasData()){
            brokerMetricsMap = metricsResult.getData().stream().collect(Collectors.toMap(BrokerMetrics::getBrokerId, Function.identity()));
        }

        for(Map.Entry<Integer, Broker> entry : brokerMap.entrySet()){
            Broker broker = entry.getValue();
            if (broker == null){
                continue;
            }
            ClusterBalanceOverviewVO clusterBalanceOverviewVO = new ClusterBalanceOverviewVO();
            clusterBalanceOverviewVO.setBrokerId(entry.getKey());
            clusterBalanceOverviewVO.setHost(broker.getHost());
            clusterBalanceOverviewVO.setRack(broker.getRack());
            BrokerMetrics brokerMetrics = brokerMetricsMap.get(entry.getKey());
            if (brokerMetrics != null){
                clusterBalanceOverviewVO.setLeader(brokerMetrics.getMetric( BrokerMetricVersionItems.BROKER_METRIC_LEADERS)!=null
                        ?brokerMetrics.getMetric( BrokerMetricVersionItems.BROKER_METRIC_LEADERS).intValue():null);
                clusterBalanceOverviewVO.setReplicas(brokerMetrics.getMetric( BrokerMetricVersionItems.BROKER_METRIC_PARTITIONS)!=null
                        ?brokerMetrics.getMetric( BrokerMetricVersionItems.BROKER_METRIC_PARTITIONS).intValue():null);
            }
            clusterBalanceOverviewVO.setSub(ClusterBalanceConverter.convert2MapClusterBalanceOverviewSubVO(brokerSpecMap.get(entry.getKey()), brokerBalanceStateMap.get(entry.getKey())));
            clusterBalanceOverviewVOS.add(clusterBalanceOverviewVO);
        }

        //过滤status
        if (dto.getStateParam()!= null && dto.getStateParam().size()>0){
            clusterBalanceOverviewVOS = filterState(dto.getStateParam(), clusterBalanceOverviewVOS);
        }

        clusterBalanceOverviewVOS = PaginationUtil.pageByFuzzyFilter(ConvertUtil.list2List(clusterBalanceOverviewVOS, ClusterBalanceOverviewVO.class), dto.getSearchKeywords(), Arrays.asList("host"));

        return PaginationResult.buildSuc(clusterBalanceOverviewVOS, clusterBalanceOverviewVOS.size(), dto.getPageNo(), dto.getPageSize());
    }

    @Override
    public Result<ClusterBalanceItemState> getItemState(Long clusterPhyId) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.CLUSTER_NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        Result<ClusterBalanceJobConfigPO> configPOResult = clusterBalanceJobConfigService.getByClusterId(clusterPhyId);
        if(configPOResult == null || !configPOResult.hasData()) {
            ClusterBalanceItemState balanceState = new ClusterBalanceItemState();
            balanceState.setConfigureBalance(Boolean.FALSE);
            balanceState.setEnable(Boolean.FALSE);
            return Result.buildSuc(balanceState);
        }

        // Broker信息
        Map<Integer, Broker> brokerMap = brokerService.listAllBrokersFromDB(clusterPhyId)
                .stream()
                .collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));

        // Broker空间信息
        Map<Integer, BrokerSpec> brokerSpecMap = brokerSpecService.getBrokerSpecMap(clusterPhyId);

        // Topic信息
        List<String> recentTopicNameList = topicService.listRecentUpdateTopicNamesFromDB(
                clusterPhyId,
                configUtils.getClusterBalanceIgnoredTopicsTimeSecond()
        );

        ClusterBalanceItemState balanceState = new ClusterBalanceItemState();
        balanceState.setConfigureBalance(Boolean.TRUE);
        balanceState.setEnable(configPOResult.getData().getStatus().equals(Constant.YES));
        Map<Integer, BrokerBalanceState> brokerBalanceStateMap;
        try {
            brokerBalanceStateMap = ExecutionRebalance.getBrokerResourcesBalanceState(
                    ClusterBalanceConverter.convert2BalanceParameter(
                            configPOResult.getData(),
                            brokerMap,
                            brokerSpecMap,
                            clusterPhy,
                            esAddress,
                            recentTopicNameList
                    )
            );
        }catch (Exception e){
            logger.error("method=state||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);
            return Result.buildFailure(e.getMessage());
        }

        Map<String, Boolean> itemStateMap = new HashMap<>();
        //集群状态信息
        ArrayList<BrokerBalanceState> balanceStates = new ArrayList(brokerBalanceStateMap.values());
        List<ClusterBalanceIntervalDTO> intervalDTOS = ConvertUtil.str2ObjArrayByJson(configPOResult.getData().getBalanceIntervalJson(), ClusterBalanceIntervalDTO.class);
        intervalDTOS.forEach(intervalDTO->{
            if (Resource.CPU.resource().equals(intervalDTO.getType())){
                itemStateMap.put(Resource.CPU.resource(), balanceStates.stream()
                        .filter(status->ClusterBalanceStateEnum.BALANCE.getState().equals(status.getCpuBalanceState())).count()==brokerMap.size());
            }else if (Resource.NW_IN.resource().equals(intervalDTO.getType())){
                itemStateMap.put(Resource.NW_IN.resource(), balanceStates.stream()
                        .filter(status->ClusterBalanceStateEnum.BALANCE.getState().equals(status.getBytesInBalanceState())).count()==brokerMap.size());
            }else if (Resource.NW_OUT.resource().equals(intervalDTO.getType())){
                itemStateMap.put(Resource.NW_OUT.resource(), balanceStates.stream()
                        .filter(status->ClusterBalanceStateEnum.BALANCE.getState().equals(status.getBytesOutBalanceState())).count()==brokerMap.size());
            }else if (Resource.DISK.resource().equals(intervalDTO.getType())){
                itemStateMap.put(Resource.DISK.resource(), balanceStates.stream()
                        .filter(status->ClusterBalanceStateEnum.BALANCE.getState().equals(status.getDiskBalanceState())).count()==brokerMap.size());

            }
        });
        balanceState.setItemState(itemStateMap);

        return Result.buildSuc(balanceState);
    }

    @Override
    public Result<ClusterBalanceItemState> getItemStateFromCacheFirst(Long clusterPhyId) {
        Result<ClusterBalanceItemState> stateResult = balanceStateCache.getIfPresent(clusterPhyId);
        if (stateResult == null) {
            return this.updateCacheAndGetMetrics(clusterPhyId);
        }

        return stateResult;
    }

    @Override
    public PaginationResult<ClusterBalanceHistoryVO> history(Long clusterPhyId, PaginationBaseDTO dto) {
        return clusterBalanceJobService.page(clusterPhyId, dto);
    }

    @Override
    public Result<ClusterBalancePlanVO> plan(Long clusterPhyId, Long jobId) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        Result<ClusterBalanceJobPO> jobPOResult = clusterBalanceJobService.getClusterBalanceJobById(jobId);
        if (jobPOResult.failed()){
            return Result.buildFrom(ResultStatus.NOT_EXIST);
        }

        List<Broker> allBrokers= brokerService.listAllBrokersFromDB(clusterPhyId);

        ClusterBalancePlanVO planVO = new ClusterBalancePlanVO();
        ClusterBalanceJobPO jobPO = jobPOResult.getData();
        planVO.setMoveSize(jobPO.getTotalReassignSize());
        planVO.setBrokers(ClusterBalanceConverter.convert2HostList(allBrokers, jobPO.getBrokers()));
        planVO.setBlackTopics(CommonUtils.string2StrList(jobPO.getTopicBlackList()));
        planVO.setReplicas(jobPO.getTotalReassignReplicaNum());
        planVO.setType(jobPO.getType());
        planVO.setTopics(CommonUtils.string2StrList(jobPO.getMoveInTopicList()));
        planVO.setDetail(ConvertUtil.str2ObjArrayByJson(jobPO.getBrokerBalanceDetail(), ClusterBalancePlanDetailVO.class));
        planVO.setReassignmentJson(jobPO.getReassignmentJson());
        planVO.setClusterBalanceIntervalList(ConvertUtil.str2ObjArrayByJson(jobPO.getBalanceIntervalJson(), ClusterBalanceIntervalVO.class));
        return Result.buildSuc(planVO);
    }

    @Override
    public Result<ClusterBalancePlanVO> preview(Long clusterPhyId, ClusterBalancePreviewDTO clusterBalancePreviewDTO) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        List<Broker> allBrokers = brokerService.listAllBrokersFromDB(clusterPhy.getId());
        Map<Integer, BrokerSpec> brokerSpecMap = brokerSpecService.getBrokerSpecMap(clusterPhy.getId());
        for(Broker broker:allBrokers){
            if (brokerSpecMap.get(broker.getBrokerId()) == null){
                return Result.buildFromRSAndMsg(ResultStatus.BROKER_SPEC_NOT_EXIST,String.format("Broker规格信息不存在:brokerId:%s", broker.getBrokerId()));
            }
        }

        if (clusterBalancePreviewDTO.getBrokers() == null || clusterBalancePreviewDTO.getBrokers().isEmpty()){
            clusterBalancePreviewDTO.setBrokers(
                    allBrokers.stream().map(Broker::getBrokerId).collect(Collectors.toList()));
        }

        //获取任务计划
        Map<Integer, Broker> brokerMap = allBrokers.stream().collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));
        List<String> topicNames = topicService.listRecentUpdateTopicNamesFromDB(clusterPhyId, configUtils.getClusterBalanceIgnoredTopicsTimeSecond());
        BalanceParameter balanceParameter = ClusterBalanceConverter.convert2BalanceParameter(clusterBalancePreviewDTO, brokerMap, brokerSpecMap, clusterPhy, esAddress, topicNames);
        ExecutionRebalance executionRebalance = new ExecutionRebalance();
        try {
            OptimizerResult optimizerResult = executionRebalance.optimizations(balanceParameter);
            if (optimizerResult == null) {
                return Result.buildFrom(ResultStatus.KAFKA_OPERATE_FAILED);
            }

            //生成平衡job
            return Result.buildSuc(ClusterBalanceConverter.convert2ClusterBalancePlanVO(clusterBalancePreviewDTO, optimizerResult, allBrokers));
        } catch (Exception e){
            logger.error("method=preview||clusterBalancePreviewDTO:{}||errMsg=exception", clusterBalancePreviewDTO, e);
            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<ClusterBalancePlanVO> schedule(Long clusterPhyId, Long jobId) {
        Result<ClusterBalanceJobPO> rbr=  clusterBalanceJobService.getClusterBalanceJobById(jobId);
        if (!rbr.hasData()){
            return Result.buildFromIgnoreData(rbr);
        }

        return preview(clusterPhyId, ClusterBalanceConverter.convert2ClusterBalancePreviewDTO(rbr.getData()));
    }

    @Override
    public Result<Void> strategy(Long clusterPhyId, ClusterBalanceStrategyDTO dto, String operator) {
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null){
            return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
        }

        //如果不是周期任务，那么就直接往 jobService 中添加一个任务
        if(!dto.isScheduleJob()){
            JobDTO jobDTO = new JobDTO();
            jobDTO.setPlanTime(new Date());
            jobDTO.setJobStatus(JobStatusEnum.WAITING.getStatus());
            jobDTO.setCreator(operator);
            jobDTO.setJobType(JobHandleEnum.CLUSTER_BALANCE.getType());
            jobDTO.setTarget(JobHandleEnum.CLUSTER_BALANCE.getMessage());
            jobDTO.setJobData(ConvertUtil.obj2Json(dto));
            return jobService.addTask(clusterPhyId, jobDTO, operator);
        }else {
            return clusterBalanceJobConfigService.replaceClusterBalanceJobConfigByClusterId(ClusterBalanceConverter.convert2ClusterBalanceJobConfigPO(dto, operator));
        }
    }

    @Override
    public Result<Void> createScheduleJob(Long clusterPhyId, long triggerTimeUnitMs){
        //获取到 clusterPhyId 对应的周期任务策略
        Result<ClusterBalanceJobConfigPO> configPOResult = clusterBalanceJobConfigService.getByClusterId(clusterPhyId);
        if (!configPOResult.hasData() || configPOResult.getData().getStatus().equals(Constant.DOWN)){
            return Result.buildSuc();
        }

        try {
            CronExpression cronExpression = new CronExpression(configPOResult.getData().getTaskCron());
            //是否到满足周期时间
            if (!cronExpression.isSatisfiedBy(new Date(triggerTimeUnitMs))){
                return Result.buildSuc();
            }
        } catch (ParseException e) {
            logger.error("method=createScheduleJob||clusterId:{}||errMsg=exception", clusterPhyId, e);

            e.printStackTrace();
        }

        //满足周期时间新增job任务
        JobDTO jobDTO = new JobDTO();
        jobDTO.setPlanTime(new Date());
        jobDTO.setJobStatus(JobStatusEnum.WAITING.getStatus());
        jobDTO.setCreator(Constant.SYSTEM);
        jobDTO.setJobType(JobTypeEnum.CLUSTER_BALANCE.getType());
        jobDTO.setTarget(JobHandleEnum.CLUSTER_BALANCE.getMessage());
        JobClusterBalanceContent content = ClusterBalanceConverter.convert2JobClusterBalanceContent(configPOResult.getData());
        jobDTO.setJobData(ConvertUtil.obj2Json(content));
        return jobService.addTask(clusterPhyId, jobDTO, Constant.SYSTEM);
    }

    private Map<String, ClusterBalanceStateSubVO> getStateSubVOMap(Map<Resource, Double> resourceDoubleMap, ArrayList<BrokerBalanceState> balanceStates, Long clusterId){
        Map<String, ClusterBalanceStateSubVO> subVOMap = new HashMap<>();
        Map<String, Double> balanceInterval = clusterBalanceJobService.getBalanceInterval(clusterId);
        for (Map.Entry<Resource, Double> entry : resourceDoubleMap.entrySet()){
            Resource resource = entry.getKey();
            if (Resource.CPU.resource().equals(resource.resource())){
                ClusterBalanceStateSubVO cpuSubVo = new ClusterBalanceStateSubVO();
                cpuSubVo.setAvg(entry.getValue());
                cpuSubVo.setInterval(balanceInterval.get(Resource.CPU.resource()));
                cpuSubVo.setBetweenNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.BALANCE.getState().equals(status.getCpuBalanceState())).count());
                cpuSubVo.setBigNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.ABOVE_BALANCE.getState().equals(status.getCpuBalanceState())).count());
                cpuSubVo.setSmallNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.BELOW_BALANCE.getState().equals(status.getCpuBalanceState())).count());
                subVOMap.put(Resource.CPU.resource(), cpuSubVo);
            }else if (Resource.NW_IN.resource().equals(resource.resource())){
                ClusterBalanceStateSubVO cpuSubVo = new ClusterBalanceStateSubVO();
                cpuSubVo.setAvg(entry.getValue());
                cpuSubVo.setInterval(balanceInterval.get(Resource.NW_IN.resource()));
                cpuSubVo.setBetweenNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.BALANCE.getState().equals(status.getBytesInBalanceState())).count());
                cpuSubVo.setBigNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.ABOVE_BALANCE.getState().equals(status.getBytesInBalanceState())).count());
                cpuSubVo.setSmallNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.BELOW_BALANCE.getState().equals(status.getBytesInBalanceState())).count());
                subVOMap.put(Resource.NW_IN.resource(), cpuSubVo);
            }else if (Resource.NW_OUT.resource().equals(resource.resource())){
                ClusterBalanceStateSubVO cpuSubVo = new ClusterBalanceStateSubVO();
                cpuSubVo.setAvg(entry.getValue());
                cpuSubVo.setInterval(balanceInterval.get(Resource.NW_OUT.resource()));
                cpuSubVo.setBetweenNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.BALANCE.getState().equals(status.getBytesOutBalanceState())).count());
                cpuSubVo.setBigNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.ABOVE_BALANCE.getState().equals(status.getBytesOutBalanceState())).count());
                cpuSubVo.setSmallNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.BELOW_BALANCE.getState().equals(status.getBytesOutBalanceState())).count());
                subVOMap.put(Resource.NW_OUT.resource(), cpuSubVo);
            }else if (Resource.DISK.resource().equals(resource.resource())){
                ClusterBalanceStateSubVO cpuSubVo = new ClusterBalanceStateSubVO();
                cpuSubVo.setAvg(entry.getValue());
                cpuSubVo.setInterval(balanceInterval.get(Resource.DISK.resource()));
                cpuSubVo.setBetweenNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.BALANCE.getState().equals(status.getDiskBalanceState())).count());
                cpuSubVo.setBigNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.ABOVE_BALANCE.getState().equals(status.getDiskBalanceState())).count());
                cpuSubVo.setSmallNu(balanceStates.stream().filter(status->ClusterBalanceStateEnum.BELOW_BALANCE.getState().equals(status.getDiskBalanceState())).count());
                subVOMap.put(Resource.DISK.resource(), cpuSubVo);
            }
        }
        return subVOMap;
    }

    private Result<ClusterBalanceItemState> updateCacheAndGetMetrics(Long clusterPhyId) {
        try {
            Result<ClusterBalanceItemState> stateResult = this.getItemState(clusterPhyId);

            balanceStateCache.put(clusterPhyId, stateResult);

            return stateResult;
        } catch (Exception e) {
            logger.error("method=updateCacheAndGetMetrics||clusterPhyId={}||errMsg=exception!", clusterPhyId, e);

            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FAILED, e.getMessage());
        }
    }

    private List<ClusterBalanceOverviewVO> filterState(Map<String, Integer> stateParam, List<ClusterBalanceOverviewVO> oldVos){
        if (stateParam.isEmpty()){
            return oldVos;
        }
        List<ClusterBalanceOverviewVO> overviewVOS = new ArrayList<>();
        for(ClusterBalanceOverviewVO oldVo : oldVos){
            Boolean check = true;
            for(Map.Entry<String, Integer> paramEntry : stateParam.entrySet()){
                ClusterBalanceOverviewSubVO subVO = oldVo.getSub().get(paramEntry.getKey());
                if (subVO == null){
                    check = false;
                    continue;
                }
                if (subVO.getStatus()==null || !subVO.getStatus().equals(paramEntry.getValue())){
                    check = false;
                }
            }
            if (check){
                overviewVOS.add(oldVo);
            }
        }

        return overviewVOS;
    }
}
