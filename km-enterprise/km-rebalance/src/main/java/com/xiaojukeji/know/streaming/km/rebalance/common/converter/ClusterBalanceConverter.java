package com.xiaojukeji.know.streaming.km.rebalance.common.converter;

import com.xiaojukeji.know.streaming.km.common.annotations.enterprise.EnterpriseLoadReBalance;
import com.xiaojukeji.know.streaming.km.persistence.es.template.TemplateConstant;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalanceIntervalDTO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalancePreviewDTO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.dto.ClusterBalanceStrategyDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.BrokerSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.ClusterBalanceInterval;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.ClusterBalanceReassignExtendData;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.detail.ClusterBalancePlanDetail;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.entity.job.content.JobClusterBalanceContent;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobConfigPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceJobPO;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.po.ClusterBalanceReassignPO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.rebalance.common.bean.vo.*;
import com.xiaojukeji.know.streaming.km.rebalance.common.enums.ClusterBalanceStateEnum;
import com.xiaojukeji.know.streaming.km.rebalance.common.enums.ClusterBalanceTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum;
import com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.executor.common.*;
import com.xiaojukeji.know.streaming.km.rebalance.algorithm.model.Resource;
import org.apache.kafka.clients.CommonClientConfigs;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@EnterpriseLoadReBalance
public class ClusterBalanceConverter {
    private ClusterBalanceConverter() {
    }

    public static BalanceParameter convert2BalanceParameter(ClusterBalanceJobConfigPO configPO,
                                                            Map<Integer, Broker> brokerMap,
                                                            Map<Integer, BrokerSpec> brokerSpecMap,
                                                            ClusterPhy clusterPhy,
                                                            String esUrl,
                                                            String esPassword,
                                                            List<String> topicNames) {
        BalanceParameter balanceParameter = new BalanceParameter();
        List<ClusterBalanceIntervalDTO> clusterBalanceIntervalDTOS = ConvertUtil.str2ObjArrayByJson(configPO.getBalanceIntervalJson(), ClusterBalanceIntervalDTO.class);

        List<String> goals = new ArrayList<>();
        for(ClusterBalanceIntervalDTO clusterBalanceIntervalDTO : clusterBalanceIntervalDTOS){
            if (Resource.DISK.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setDiskThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.DISK.goal());
            }else if (Resource.CPU.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setCpuThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                // todo cpu底层暂未实现，先不加goal
            }else if (Resource.NW_IN.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setNetworkInThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.NW_IN.goal());
            }else if (Resource.NW_OUT.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setNetworkOutThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.NW_OUT.goal());
            }
        }
        balanceParameter.setGoals(goals);
        balanceParameter.setCluster(clusterPhy.getId().toString());
        balanceParameter.setExcludedTopics(configPO.getTopicBlackList());
        balanceParameter.setEsInfo(esUrl, esPassword, TemplateConstant.PARTITION_INDEX + "_");
        balanceParameter.setBalanceBrokers(CommonUtils.intSet2String(brokerMap.keySet()));
        balanceParameter.setHardwareEnv(convert2ListHostEnv(brokerMap, brokerSpecMap));
        balanceParameter.setBeforeSeconds(configPO.getMetricCalculationPeriod());
        balanceParameter.setIgnoredTopics(CommonUtils.strList2String(topicNames));

        Properties kafkaConfig = new Properties();
        kafkaConfig.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
        kafkaConfig.putAll(ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class));
        balanceParameter.setKafkaConfig(kafkaConfig);
        return balanceParameter;

    }

    public static BalanceParameter convert2BalanceParameter(ClusterBalanceJobPO clusterBalanceJobPO,
                                                            Map<Integer, Broker> brokerMap,
                                                            Map<Integer, BrokerSpec> brokerSpecMap,
                                                            ClusterPhy clusterPhy,
                                                            String esUrl,
                                                            String esPassword,
                                                            List<String> topicNames) {
        BalanceParameter balanceParameter = new BalanceParameter();
        List<ClusterBalanceIntervalDTO> clusterBalanceIntervalDTOS = ConvertUtil.str2ObjArrayByJson(clusterBalanceJobPO.getBalanceIntervalJson(), ClusterBalanceIntervalDTO.class);

        List<String> goals = new ArrayList<>();
        for(ClusterBalanceIntervalDTO clusterBalanceIntervalDTO : clusterBalanceIntervalDTOS){
            if (Resource.DISK.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setDiskThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.DISK.goal());
            }else if (Resource.CPU.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setCpuThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                // todo cpu底层暂未实现，先不加goal
            }else if (Resource.NW_IN.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setNetworkInThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.NW_IN.goal());
            }else if (Resource.NW_OUT.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setNetworkOutThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.NW_OUT.goal());
            }
        }
        balanceParameter.setGoals(goals);
        balanceParameter.setCluster(clusterPhy.getId().toString());
        balanceParameter.setExcludedTopics(clusterBalanceJobPO.getTopicBlackList());
        balanceParameter.setEsInfo(esUrl, esPassword, TemplateConstant.PARTITION_INDEX + "_");
        balanceParameter.setBalanceBrokers(clusterBalanceJobPO.getBrokers());
        balanceParameter.setHardwareEnv(convert2ListHostEnv(brokerMap, brokerSpecMap));
        balanceParameter.setBeforeSeconds(clusterBalanceJobPO.getMetricCalculationPeriod());
        balanceParameter.setIgnoredTopics(CommonUtils.strList2String(topicNames));

        Properties kafkaConfig = new Properties();
        kafkaConfig.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
        kafkaConfig.putAll(ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class));
        balanceParameter.setKafkaConfig(kafkaConfig);
        return balanceParameter;

    }

    public static BalanceParameter convert2BalanceParameter(JobClusterBalanceContent dto,
                                                            List<Broker> brokers,
                                                            Map<Integer, BrokerSpec> brokerSpecMap,
                                                            ClusterPhy clusterPhy,
                                                            String esUrl,
                                                            String esPassword,
                                                            List<String> topicNames) {
        BalanceParameter balanceParameter = new BalanceParameter();
        List<ClusterBalanceIntervalDTO> clusterBalanceIntervalDTOS =  dto.getClusterBalanceIntervalList().stream()
                .sorted(Comparator.comparing(ClusterBalanceIntervalDTO::getPriority)).collect(Collectors.toList());
        List<String> goals = new ArrayList<>();
        for(ClusterBalanceIntervalDTO clusterBalanceIntervalDTO : clusterBalanceIntervalDTOS){
            if (Resource.DISK.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setDiskThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.DISK.goal());
            }else if (Resource.CPU.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setCpuThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                // todo cpu底层暂未实现，先不加goal
            }else if (Resource.NW_IN.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setNetworkInThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.NW_IN.goal());
            }else if (Resource.NW_OUT.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setNetworkOutThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.NW_OUT.goal());
            }
        }

        Map<Integer, Broker> brokerMap = brokers.stream().collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));
        balanceParameter.setGoals(goals);
        balanceParameter.setCluster(clusterPhy.getId().toString());
        balanceParameter.setExcludedTopics(CommonUtils.strList2String(dto.getTopicBlackList()));
        balanceParameter.setEsInfo(esUrl, esPassword, TemplateConstant.PARTITION_INDEX + "_");
        balanceParameter.setBalanceBrokers(CommonUtils.intSet2String(brokerMap.keySet()));
        balanceParameter.setHardwareEnv(convert2ListHostEnv(brokerMap, brokerSpecMap));
        balanceParameter.setBeforeSeconds(dto.getMetricCalculationPeriod());
        balanceParameter.setIgnoredTopics(CommonUtils.strList2String(topicNames));

        Properties kafkaConfig = new Properties();
        kafkaConfig.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
        kafkaConfig.putAll(ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class));
        balanceParameter.setKafkaConfig(kafkaConfig);
        return balanceParameter;

    }

    public static BalanceParameter convert2BalanceParameter(ClusterBalancePreviewDTO dto,
                                                            Map<Integer, Broker> brokerMap,
                                                            Map<Integer, BrokerSpec> brokerSpecMap,
                                                            ClusterPhy clusterPhy,
                                                            String esUrl,
                                                            String esPassword,
                                                            List<String> topicNames) {
        BalanceParameter balanceParameter = new BalanceParameter();
        List<ClusterBalanceIntervalDTO> clusterBalanceIntervalDTOS =  dto.getClusterBalanceIntervalList().stream()
                .sorted(Comparator.comparing(ClusterBalanceIntervalDTO::getPriority)).collect(Collectors.toList());
        List<String> goals = new ArrayList<>();
        for(ClusterBalanceIntervalDTO clusterBalanceIntervalDTO : clusterBalanceIntervalDTOS){
            if (Resource.DISK.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setDiskThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.DISK.goal());
            }else if (Resource.CPU.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setCpuThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                // todo cpu底层暂未实现，先不加goal
            }else if (Resource.NW_IN.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setNetworkInThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.NW_IN.goal());
            }else if (Resource.NW_OUT.resource().equals(clusterBalanceIntervalDTO.getType())){
                balanceParameter.setNetworkOutThreshold(clusterBalanceIntervalDTO.getIntervalPercent()/100);
                goals.add(BalanceGoal.NW_OUT.goal());
            }
        }
        balanceParameter.setGoals(goals);
        balanceParameter.setCluster(clusterPhy.getId().toString());
        balanceParameter.setExcludedTopics(CommonUtils.strList2String(dto.getTopicBlackList()));
        balanceParameter.setEsInfo(esUrl, esPassword, TemplateConstant.PARTITION_INDEX + "_");
        balanceParameter.setBalanceBrokers(CommonUtils.intList2String(dto.getBrokers()));
        balanceParameter.setHardwareEnv(convert2ListHostEnv(brokerMap, brokerSpecMap));
        balanceParameter.setBeforeSeconds(dto.getMetricCalculationPeriod());
        balanceParameter.setIgnoredTopics(CommonUtils.strList2String(topicNames));

        Properties kafkaConfig = new Properties();
        kafkaConfig.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
        kafkaConfig.putAll(ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class));
        balanceParameter.setKafkaConfig(kafkaConfig);
        return balanceParameter;

    }


    public static ClusterBalanceJobPO convert2ClusterBalanceJobPO(Long jobId, JobClusterBalanceContent jobDTO, OptimizerResult optimizerResult, List<Broker> brokers, String operator, String json) {
        if (ValidateUtils.anyNull(jobDTO, optimizerResult, optimizerResult.resultJsonOverview(),
                optimizerResult.resultJsonDetailed(), optimizerResult.resultDetailed(), optimizerResult.resultJsonTask())){
            return null;
        }

        ClusterBalanceJobPO clusterBalanceJobPO = new ClusterBalanceJobPO();
        clusterBalanceJobPO.setId(jobId);
        clusterBalanceJobPO.setType(jobDTO.isScheduleJob()?
                ClusterBalanceTypeEnum.CYCLE.getType():ClusterBalanceTypeEnum.IMMEDIATELY.getType());
        clusterBalanceJobPO.setStatus(JobStatusEnum.WAITING.getStatus());
        clusterBalanceJobPO.setCreator(operator);
        clusterBalanceJobPO.setParallelNum(jobDTO.getParallelNum());
        clusterBalanceJobPO.setThrottleUnitB(jobDTO.getThrottleUnitB());
        clusterBalanceJobPO.setDescription(jobDTO.getDescription());
        clusterBalanceJobPO.setBrokers(CommonUtils.intList2String(brokers.stream().map(Broker::getBrokerId).collect(Collectors.toList())));
        clusterBalanceJobPO.setClusterId(jobDTO.getClusterId());
        clusterBalanceJobPO.setTopicBlackList(CommonUtils.strList2String(jobDTO.getTopicBlackList()));
        clusterBalanceJobPO.setMoveInTopicList(optimizerResult.resultOverview().getMoveTopics());
        clusterBalanceJobPO.setExecutionStrategy(jobDTO.getExecutionStrategy());
        clusterBalanceJobPO.setBalanceIntervalJson(ConvertUtil.obj2Json(jobDTO.getClusterBalanceIntervalList()));
        clusterBalanceJobPO.setBrokerBalanceDetail(ConvertUtil.obj2Json(convert2ClusterBalancePlanDetail(optimizerResult.resultDetailed())));
        clusterBalanceJobPO.setMetricCalculationPeriod(jobDTO.getMetricCalculationPeriod());
        clusterBalanceJobPO.setReassignmentJson(json);
        clusterBalanceJobPO.setTotalReassignSize(optimizerResult.resultOverview().getTotalMoveSize());
        clusterBalanceJobPO.setTotalReassignReplicaNum(optimizerResult.resultOverview().getMoveReplicas());
        clusterBalanceJobPO.setDescription(optimizerResult.resultJsonBalanceActionHistory());
        return clusterBalanceJobPO;
    }

    public static ClusterBalanceReassignPO convert2ClusterBalanceReassignPO(BalanceTask balanceTask, Topic topic, Long jobId, Long clusterId) {
        ClusterBalanceReassignPO reassignPO = new ClusterBalanceReassignPO();
        reassignPO.setClusterId(clusterId);
        reassignPO.setJobId(jobId);
        reassignPO.setPartitionId(balanceTask.getPartition());
        reassignPO.setOriginalBrokerIds(CommonUtils.intList2String(topic.getPartitionMap().get(balanceTask.getPartition())));
        reassignPO.setReassignBrokerIds(CommonUtils.intList2String(balanceTask.getReplicas()));
        reassignPO.setTopicName(balanceTask.getTopic());
        ClusterBalanceReassignExtendData extendData = new ClusterBalanceReassignExtendData();
        extendData.setOriginalRetentionTimeUnitMs(topic.getRetentionMs());
        extendData.setReassignRetentionTimeUnitMs(topic.getRetentionMs());
        extendData.setOriginReplicaNum(topic.getReplicaNum());
        extendData.setReassignReplicaNum(balanceTask.getReplicas().size());
        reassignPO.setExtendData(ConvertUtil.obj2Json(extendData));
        reassignPO.setStatus(JobStatusEnum.WAITING.getStatus());
        return reassignPO;
    }

    public static List<ClusterBalanceReassignPO> convert2ListClusterBalanceReassignPO(List<BalanceTask> balanceTasks, Map<String, Topic> topicMap, Long jobId, Long clusterId) {
        List<ClusterBalanceReassignPO> reassignPOs = new ArrayList<>();
        //生成迁移详情
        Map<String, List<BalanceTask>> balanceTaskMap = balanceTasks.stream().collect(Collectors.groupingBy(BalanceTask::getTopic));
        for (Map.Entry<String, List<BalanceTask>> entry : balanceTaskMap.entrySet()){
            Topic topic = topicMap.get(entry.getKey());
            if (topic == null || topic.getPartitionMap() == null){
                continue;
            }
            for (BalanceTask balanceTask : entry.getValue()){
                reassignPOs.add(ClusterBalanceConverter.convert2ClusterBalanceReassignPO(balanceTask, topic, jobId, clusterId));
            }
        }
        return reassignPOs;
    }

    public static ClusterBalanceJobConfigPO convert2ClusterBalanceJobConfigPO(ClusterBalanceStrategyDTO dto, String operator) {
        ClusterBalanceJobConfigPO jobConfigPO = new ClusterBalanceJobConfigPO();
        jobConfigPO.setCreator(operator);
        jobConfigPO.setParallelNum(dto.getParallelNum());
        jobConfigPO.setThrottleUnitB(dto.getThrottleUnitB());
        jobConfigPO.setClusterId(dto.getClusterId());
        jobConfigPO.setExecutionStrategy(dto.getExecutionStrategy());
        jobConfigPO.setBalanceIntervalJson(ConvertUtil.obj2Json(dto.getClusterBalanceIntervalList()));
        jobConfigPO.setTaskCron(dto.getScheduleCron());
        jobConfigPO.setMetricCalculationPeriod(dto.getMetricCalculationPeriod());
        jobConfigPO.setStatus(dto.getStatus());
        return jobConfigPO;
    }

    public static JobClusterBalanceContent convert2JobClusterBalanceContent(ClusterBalanceJobConfigPO configPO) {
        JobClusterBalanceContent content = new JobClusterBalanceContent();
        content.setType(JobTypeEnum.CLUSTER_BALANCE.getType());
        content.setParallelNum(configPO.getParallelNum());
        content.setThrottleUnitB(configPO.getThrottleUnitB());
        content.setClusterId(configPO.getClusterId());
        content.setExecutionStrategy(configPO.getExecutionStrategy());
        content.setClusterBalanceIntervalList(ConvertUtil.str2ObjArrayByJson(configPO.getBalanceIntervalJson(), ClusterBalanceIntervalDTO.class));
        content.setMetricCalculationPeriod(configPO.getMetricCalculationPeriod());
        content.setTopicBlackList(CommonUtils.string2StrList(configPO.getTopicBlackList()));
        content.setScheduleJob(Boolean.TRUE);
        return content;
    }

    public static List<ClusterBalancePlanDetail> convert2ClusterBalancePlanDetail(Map<Integer, BalanceDetailed> detailedMap) {
        List<ClusterBalancePlanDetail> details = new ArrayList<>();
        for(Map.Entry<Integer, BalanceDetailed> entry : detailedMap.entrySet()){
            BalanceDetailed balanceDetailed = entry.getValue();
            if (balanceDetailed == null){
                continue ;
            }
            ClusterBalancePlanDetail planDetail = new ClusterBalancePlanDetail();
            planDetail.setStatus(balanceDetailed.getBalanceState()==ClusterBalanceStateEnum.BALANCE.getState()?ClusterBalanceStateEnum.BALANCE.getState():ClusterBalanceStateEnum.UNBALANCED.getState());
            planDetail.setHost(balanceDetailed.getHost());
            planDetail.setBrokerId(entry.getKey());
            planDetail.setCpuBefore(balanceDetailed.getCurrentCPUUtilization()*Constant.ONE_HUNDRED);
            planDetail.setCpuAfter(balanceDetailed.getLastCPUUtilization()*Constant.ONE_HUNDRED);
            planDetail.setDiskBefore(balanceDetailed.getCurrentDiskUtilization()*Constant.ONE_HUNDRED);
            planDetail.setDiskAfter(balanceDetailed.getLastDiskUtilization()*Constant.ONE_HUNDRED);
            planDetail.setByteInBefore(balanceDetailed.getCurrentNetworkInUtilization()*Constant.ONE_HUNDRED);
            planDetail.setByteInAfter(balanceDetailed.getLastNetworkInUtilization()*Constant.ONE_HUNDRED);
            planDetail.setByteOutBefore(balanceDetailed.getCurrentNetworkOutUtilization()*Constant.ONE_HUNDRED);
            planDetail.setByteOutAfter(balanceDetailed.getLastNetworkOutUtilization()*Constant.ONE_HUNDRED);
            planDetail.setInReplica(balanceDetailed.getMoveInReplicas());
            planDetail.setOutReplica(balanceDetailed.getMoveOutReplicas());
            planDetail.setInSize(balanceDetailed.getMoveInDiskSize());
            planDetail.setOutSize(balanceDetailed.getMoveOutDiskSize());
            details.add(planDetail);
        }
        return details;
    }

    //更新平衡任务完成后的集群均衡状态
    public static List<ClusterBalancePlanDetail> convert2ClusterBalancePlanDetail(List<ClusterBalancePlanDetail> details, Map<Integer, BrokerBalanceState> stateMap) {
        details.forEach(planDetail ->{
            BrokerBalanceState state = stateMap.get(planDetail.getBrokerId());
            if (state == null){
                return;
            }
            planDetail.setCpuStatus(state.getCpuBalanceState());
            planDetail.setDiskStatus(state.getDiskBalanceState());
            planDetail.setByteInStatus(state.getBytesInBalanceState());
            planDetail.setByteOutStatus(state.getBytesOutBalanceState());
            if ((state.getCpuBalanceState() == null || ClusterBalanceStateEnum.BALANCE.getState().equals(state.getCpuBalanceState()))
                    && (state.getDiskBalanceState() == null || ClusterBalanceStateEnum.BALANCE.getState().equals(state.getDiskBalanceState()))
                    && (state.getBytesInBalanceState() == null || ClusterBalanceStateEnum.BALANCE.getState().equals(state.getBytesInBalanceState()))
                    && (state.getBytesOutBalanceState() == null || ClusterBalanceStateEnum.BALANCE.getState().equals(state.getBytesOutBalanceState()))) {
                planDetail.setStatus(ClusterBalanceStateEnum.BALANCE.getState());
            }else {
                planDetail.setStatus(ClusterBalanceStateEnum.UNBALANCED.getState());
            }
        });
        return details;
    }

    public static List<ClusterBalancePlanDetailVO> convert2ClusterBalancePlanDetailVO(List<Integer> balanceBrokerIds, Map<Integer, BalanceDetailed> detailedMap) {
        List<ClusterBalancePlanDetailVO> detailVOS = new ArrayList<>();
        for(Map.Entry<Integer, BalanceDetailed> entry : detailedMap.entrySet()){
            BalanceDetailed value = entry.getValue();
            if (value == null || !balanceBrokerIds.contains(entry.getKey())){
                continue ;
            }
            ClusterBalancePlanDetailVO planDetailVO = new ClusterBalancePlanDetailVO();
            planDetailVO.setStatus(value.getBalanceState()==ClusterBalanceStateEnum.BALANCE.getState()?ClusterBalanceStateEnum.BALANCE.getState():ClusterBalanceStateEnum.UNBALANCED.getState());
            planDetailVO.setHost(value.getHost());
            planDetailVO.setBrokerId(entry.getKey());
            planDetailVO.setCpuBefore(value.getCurrentCPUUtilization()*Constant.ONE_HUNDRED);
            planDetailVO.setCpuAfter(value.getLastCPUUtilization()*Constant.ONE_HUNDRED);
            planDetailVO.setDiskBefore(value.getCurrentDiskUtilization()*Constant.ONE_HUNDRED);
            planDetailVO.setDiskAfter(value.getLastDiskUtilization()*Constant.ONE_HUNDRED);
            planDetailVO.setByteInBefore(value.getCurrentNetworkInUtilization()*Constant.ONE_HUNDRED);
            planDetailVO.setByteInAfter(value.getLastNetworkInUtilization()*Constant.ONE_HUNDRED);
            planDetailVO.setByteOutBefore(value.getCurrentNetworkOutUtilization()*Constant.ONE_HUNDRED);
            planDetailVO.setByteOutAfter(value.getLastNetworkOutUtilization()*Constant.ONE_HUNDRED);
            planDetailVO.setInReplica(value.getMoveInReplicas());
            planDetailVO.setOutReplica(value.getMoveOutReplicas());
            planDetailVO.setInSize(value.getMoveInDiskSize());
            planDetailVO.setOutSize(value.getMoveOutDiskSize());
            detailVOS.add(planDetailVO);
        }
        return detailVOS;
    }

    public static ClusterBalancePlanVO convert2ClusterBalancePlanVO(ClusterBalancePreviewDTO jobDTO, OptimizerResult optimizerResult, List<Broker> allBrokers) {
        if (ValidateUtils.anyNull(jobDTO, optimizerResult, optimizerResult.resultJsonOverview(),
                optimizerResult.resultJsonDetailed(), optimizerResult.resultDetailed(), optimizerResult.resultJsonTask())){
            return null;
        }
        ClusterBalancePlanVO planVO = new ClusterBalancePlanVO();
        planVO.setTopics(CommonUtils.string2StrList(optimizerResult.resultOverview().getMoveTopics()));
        planVO.setType(ClusterBalanceTypeEnum.IMMEDIATELY.getType());
        planVO.setReplicas(optimizerResult.resultOverview().getMoveReplicas());
        planVO.setBlackTopics(jobDTO.getTopicBlackList());
        planVO.setMoveSize(optimizerResult.resultOverview().getTotalMoveSize());
        planVO.setThreshold(ConvertUtil.obj2Json(jobDTO.getClusterBalanceIntervalList()));
        planVO.setBrokers(convert2HostList(allBrokers, optimizerResult.resultOverview().getNodeRange()));
        planVO.setDetail(convert2ClusterBalancePlanDetailVO(jobDTO.getBrokers(), optimizerResult.resultDetailed()));
        planVO.setClusterBalanceIntervalList(ConvertUtil.list2List(jobDTO.getClusterBalanceIntervalList(), ClusterBalanceIntervalVO.class));
        planVO.setReassignmentJson(optimizerResult.resultJsonTask());
        return planVO;
    }

    public static ClusterBalancePreviewDTO convert2ClusterBalancePreviewDTO(ClusterBalanceJobPO clusterBalanceJobPO) {

        ClusterBalancePreviewDTO planVO = new ClusterBalancePreviewDTO();
        planVO.setBrokers(CommonUtils.string2IntList(clusterBalanceJobPO.getBrokers()));
        planVO.setClusterBalanceIntervalList(ConvertUtil.str2ObjArrayByJson(clusterBalanceJobPO.getBalanceIntervalJson(), ClusterBalanceIntervalDTO.class));
        planVO.setClusterId(clusterBalanceJobPO.getClusterId());
        planVO.setExecutionStrategy(clusterBalanceJobPO.getExecutionStrategy());
        planVO.setParallelNum(clusterBalanceJobPO.getParallelNum());
        planVO.setThrottleUnitB(clusterBalanceJobPO.getThrottleUnitB());
        planVO.setMetricCalculationPeriod(clusterBalanceJobPO.getMetricCalculationPeriod());
        planVO.setTopicBlackList(CommonUtils.string2StrList(clusterBalanceJobPO.getTopicBlackList()));
        return planVO;
    }

    public static  Map<String, ClusterBalanceOverviewSubVO> convert2MapClusterBalanceOverviewSubVO(BrokerSpec brokerSpec, BrokerBalanceState state) {
        Map<String, ClusterBalanceOverviewSubVO> subVOMap = new HashMap<>();
        if (brokerSpec == null){
            brokerSpec = new BrokerSpec();
        }
        if (state == null){
            state = new BrokerBalanceState();
        }
        Double cpuSpec = brokerSpec.getCpu()!=null?brokerSpec.getCpu()*Constant.ONE_HUNDRED:null;//转成基础单位
        subVOMap.put(Resource.DISK.resource(),
                new ClusterBalanceOverviewSubVO(
                        state.getDiskAvgResource(), brokerSpec.getDisk(),
                        state.getDiskBalanceState() == null || state.getDiskBalanceState().equals(ClusterBalanceStateEnum.BALANCE.getState())?state.getDiskBalanceState():ClusterBalanceStateEnum.UNBALANCED.getState()));
        subVOMap.put(Resource.CPU.resource(),
                new ClusterBalanceOverviewSubVO(state.getCpuAvgResource(), cpuSpec,
                        state.getCpuBalanceState() == null || state.getCpuBalanceState().equals(ClusterBalanceStateEnum.BALANCE.getState())?state.getCpuBalanceState():ClusterBalanceStateEnum.UNBALANCED.getState()));
        subVOMap.put(Resource.NW_IN.resource(),
                new ClusterBalanceOverviewSubVO(
                        state.getBytesInAvgResource(), brokerSpec.getFlow(),
                        state.getBytesInBalanceState() == null || state.getBytesInBalanceState().equals(ClusterBalanceStateEnum.BALANCE.getState())?state.getBytesInBalanceState():ClusterBalanceStateEnum.UNBALANCED.getState()));
        subVOMap.put(Resource.NW_OUT.resource(),
                new ClusterBalanceOverviewSubVO(
                        state.getBytesOutAvgResource(), brokerSpec.getFlow(),
                        state.getBytesOutBalanceState() == null || state.getBytesOutBalanceState().equals(ClusterBalanceStateEnum.BALANCE.getState())?state.getBytesOutBalanceState():ClusterBalanceStateEnum.UNBALANCED.getState()));
        return subVOMap;
    }



    public static ClusterBalanceJobConfigVO convert2ClusterBalanceJobConfigVO(ClusterBalanceJobConfigPO clusterBalanceJobConfigPO){
        ClusterBalanceJobConfigVO configVO = new ClusterBalanceJobConfigVO();
        configVO.setScheduleCron(clusterBalanceJobConfigPO.getTaskCron());
        configVO.setClusterBalanceIntervalList(ConvertUtil.str2ObjArrayByJson(clusterBalanceJobConfigPO.getBalanceIntervalJson(), ClusterBalanceInterval.class));
        configVO.setClusterId(clusterBalanceJobConfigPO.getClusterId());
        configVO.setExecutionStrategy(clusterBalanceJobConfigPO.getExecutionStrategy());
        configVO.setParallelNum(clusterBalanceJobConfigPO.getParallelNum());
        configVO.setMetricCalculationPeriod(clusterBalanceJobConfigPO.getMetricCalculationPeriod());
        configVO.setThrottleUnitB(clusterBalanceJobConfigPO.getThrottleUnitB());
        configVO.setTopicBlackList(CommonUtils.string2StrList(clusterBalanceJobConfigPO.getTopicBlackList()));
        configVO.setStatus(clusterBalanceJobConfigPO.getStatus());
        return configVO;
    }


    public static List<String> convert2HostList(List<Broker> allBrokers, String brokerIdStr){
        if (allBrokers.isEmpty() || ValidateUtils.isBlank(brokerIdStr)){
            return new ArrayList<>();
        }
        List<Integer> brokerIds = CommonUtils.string2IntList(brokerIdStr);
        return allBrokers.stream().filter(broker -> brokerIds.contains(broker.getBrokerId()))
                .map(Broker::getHost).collect(Collectors.toList());
    }

    private static List<HostEnv> convert2ListHostEnv(Map<Integer, Broker> brokerMap, Map<Integer, BrokerSpec> brokerSpecMap) {
        List<HostEnv> hostEnvs = new ArrayList<>();
        for (Map.Entry<Integer, Broker> entry : brokerMap.entrySet()) {
            HostEnv hostEnv = new HostEnv();
            hostEnv.setId(entry.getKey());
            hostEnv.setHost(entry.getValue().getHost());
            hostEnv.setRackId(entry.getValue().getRack());
            BrokerSpec brokerSpec = brokerSpecMap.get(entry.getKey());
            if (brokerSpec == null){
                continue;
            }
            hostEnv.setCpu(brokerSpec.getCpu().intValue() * Constant.ONE_HUNDRED);
            hostEnv.setDisk(brokerSpec.getDisk() * Constant.B_TO_GB);
            hostEnv.setNetwork(brokerSpec.getFlow() * Constant.B_TO_MB);
            hostEnvs.add(hostEnv);
        }

        return hostEnvs;

    }

}
