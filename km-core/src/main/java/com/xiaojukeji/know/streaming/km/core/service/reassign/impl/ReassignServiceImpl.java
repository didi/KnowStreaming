package com.xiaojukeji.know.streaming.km.core.service.reassign.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.reassign.ExecuteReassignParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignPlan;
import com.xiaojukeji.know.streaming.km.common.bean.entity.reassign.ReassignResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.converter.ReassignConverter;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.reassign.ReassignService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import kafka.admin.ReassignPartitionsCommand;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Tuple2;
import scala.collection.Seq;
import scala.jdk.javaapi.CollectionConverters;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.VC_HANDLE_NOT_EXIST;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_REASSIGNMENT;

@Service
public class ReassignServiceImpl extends BaseKafkaVersionControlService implements ReassignService {
    private static final ILog log = LogFactory.getLog(ReassignServiceImpl.class);

    private static final String EXECUTE_TASK                = "executeTask";
    private static final String VERIFY_TASK                 = "verifyTask";
    private static final String REPLACE_THROTTLE_TASK       = "replaceThrottleTask";

    @Autowired
    private TopicService topicService;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return SERVICE_OP_REASSIGNMENT;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(EXECUTE_TASK,             V_0_10_2_0, V_2_6_0, "executeTaskByZKClient",             this::executeTaskByZKClient);
        registerVCHandler(EXECUTE_TASK,             V_2_6_0, V_MAX,      "executeTaskByKafkaClient",          this::executeTaskByKafkaClient);

        registerVCHandler(VERIFY_TASK,              V_0_10_2_0, V_2_6_0, "verifyTaskByZKClient",              this::verifyTaskByZKClient);
        registerVCHandler(VERIFY_TASK,              V_2_6_0, V_MAX,      "verifyTaskByKafkaClient",           this::verifyTaskByKafkaClient);

        registerVCHandler(REPLACE_THROTTLE_TASK,    V_0_10_2_0, V_2_6_0, "modifyThrottleTaskByZKClient",      this::modifyThrottleTaskByZKClient);
        registerVCHandler(REPLACE_THROTTLE_TASK,    V_2_6_0, V_MAX,      "modifyThrottleTaskByKafkaClient",   this::modifyThrottleTaskByKafkaClient);
    }

    @Override
    public Result<ReassignPlan> generateReassignmentJson(Long clusterPhyId,
                                                         String topicName,
                                                         List<Integer> partitionIdList,
                                                         List<Integer> brokerIdList,
                                                         Boolean enableRackAwareness) {
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(clusterPhyId);

            // 生成原始的迁移json-map, 生成迁移计划时，会自动严格检查参数
            Tuple2<scala.collection.Map<TopicPartition, Seq<Object>>, scala.collection.Map<TopicPartition, Seq<Object>>> scalaReassignmentMapTuple =
                    ReassignPartitionsCommand.generateAssignment(
                            adminClient,
                            this.generateTopicMoveJson(topicName),
                            ConvertUtil.list2String(brokerIdList, Constant.COMMA),
                            enableRackAwareness
                    );

            // 过滤掉不需要的分区并进行格式转换
            Map<TopicPartition, List<Integer>> reAssignPlanMap = this.filterAndConvertReassignmentMap(CollectionConverters.asJava(scalaReassignmentMapTuple._1), partitionIdList);

            Map<TopicPartition, List<Integer>> currentAssignMap = this.filterAndConvertReassignmentMap(CollectionConverters.asJava(scalaReassignmentMapTuple._2), partitionIdList);

            // 返回结果
            return Result.buildSuc(new ReassignPlan(clusterPhyId, topicName, reAssignPlanMap, currentAssignMap));
        } catch (NotExistException nee) {
            log.error("method=generateReassignmentJson||clusterPhyId={}||topicName={}||errMsg=not exist error", clusterPhyId, topicName, nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=generateReassignmentJson||clusterPhyId={}||topicName={}||brokerIdList={}||errMsg=exception", clusterPhyId, topicName, brokerIdList, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<ReassignPlan> generateReplicaChangeReassignmentJson(Long clusterPhyId,
                                                                      String topicName,
                                                                      Integer newReplicaNum,
                                                                      List<Integer> brokerIdList) {
        try {
            // 获取Topic当前的分布
            Map<Integer, List<Integer>> currentPartitionMap = topicService.getTopicPartitionMapFromKafka(clusterPhyId, topicName);

            Map<TopicPartition, List<Integer>> currentAssignMap = new HashMap<>();
            currentPartitionMap.entrySet().stream().forEach(entry -> currentAssignMap.put(new TopicPartition(topicName, entry.getKey()), entry.getValue()));

            // 获取扩缩副本之后的分配规则
            Map<TopicPartition, List<Integer>> reAssignPlanMap = this.generateReplicaChangeReassignmentJson(
                    topicName,
                    newReplicaNum,
                    brokerIdList,
                    currentPartitionMap
            );

            // 返回结果
            return Result.buildSuc(new ReassignPlan(clusterPhyId, topicName, reAssignPlanMap, currentAssignMap));
        } catch (NotExistException nee) {
            log.error("method=generateReplicaChangeReassignmentJson||clusterPhyId={}||topicName={}||errMsg=not exist error", clusterPhyId, topicName, nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=generateReplicaChangeReassignmentJson||clusterPhyId={}||topicName={}||brokerIdList={}||errMsg=exception", clusterPhyId, topicName, brokerIdList, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    @Override
    public Result<Void> executePartitionReassignments(ExecuteReassignParam executeReassignParam) {
        try {
            return (Result<Void>) doVCHandler(executeReassignParam.getClusterPhyId(), EXECUTE_TASK, executeReassignParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<ReassignResult> verifyPartitionReassignments(ExecuteReassignParam executeReassignParam) {
        try {
            return (Result<ReassignResult>) doVCHandler(executeReassignParam.getClusterPhyId(), VERIFY_TASK, executeReassignParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Void> changReassignmentThrottles(ExecuteReassignParam executeReassignParam) {
        try {
            return (Result<Void>) doVCHandler(executeReassignParam.getClusterPhyId(), REPLACE_THROTTLE_TASK, executeReassignParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Void> parseExecuteAssignmentArgs(Long clusterPhyId, String reassignmentJson) {
        try {
            ReassignPartitionsCommand.parseExecuteAssignmentArgs(reassignmentJson);

            return Result.buildSuc();
        } catch (Exception e) {
            log.error("method=parseExecuteAssignmentArgs||clusterPhyId={}||reassignmentJson={}||errMsg=exception", clusterPhyId, reassignmentJson, e);

            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, e.getMessage());
        }
    }

    /**************************************************** private method ****************************************************/

    private Result<Void> modifyThrottleTaskByZKClient(VersionItemParam itemParam) {
        ExecuteReassignParam param = (ExecuteReassignParam) itemParam;
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(param.getClusterPhyId());

            // 迁移Json的迁移信息
            scala.collection.Map<TopicPartition, Seq<Object>> proposedParts = ReassignPartitionsCommand.parseExecuteAssignmentArgs(param.getReassignmentJson())._1;

            ReassignPartitionsCommand.verifyReplicasAndBrokersInAssignment(kafkaZkClient, proposedParts);

            // 当前ZK上的迁移信息
            scala.collection.Map<TopicPartition, Seq<Object>> currentParts = kafkaZkClient.getReplicaAssignmentForTopics(
                    proposedParts.keySet().map(elem -> elem.topic()).toSet()
            );

            // 转为moveMap格式
            scala.collection.mutable.Map<String, scala.collection.mutable.Map<Object, ReassignPartitionsCommand.PartitionMove>> moveMap =
                    ReassignPartitionsCommand.calculateProposedMoveMap(new scala.collection.mutable.HashMap<>(), proposedParts, currentParts);

            // 对Topic进行限流
            scala.collection.Map<String, String> leaderThrottles = ReassignPartitionsCommand.calculateLeaderThrottles(moveMap);
            scala.collection.Map<String, String> followerThrottles = ReassignPartitionsCommand.calculateFollowerThrottles(moveMap);
            ReassignPartitionsCommand.modifyTopicThrottles(kafkaZkClient, leaderThrottles, followerThrottles);

            // 对Broker进行限流
            scala.collection.immutable.Set<Object> reassigningBrokers = ReassignPartitionsCommand.calculateReassigningBrokers(moveMap);
            ReassignPartitionsCommand.modifyBrokerThrottles(kafkaZkClient, reassigningBrokers, param.getThrottleUnitB());

            return Result.buildSuc();
        } catch (NotExistException nee) {
            log.error("method=modifyThrottleTaskByZKClient||clusterPhyId={}||errMsg=not exist error", param.getClusterPhyId(), nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=modifyThrottleTaskByZKClient||clusterPhyId={}||param={}||errMsg=exception", param.getClusterPhyId(), param, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> modifyThrottleTaskByKafkaClient(VersionItemParam itemParam) {
        ExecuteReassignParam param = (ExecuteReassignParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            ReassignPartitionsCommand.executeAssignment(
                    adminClient,
                    true,
                    param.getReassignmentJson(),
                    param.getThrottleUnitB(),
                    -1L,
                    KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS,
                    Time.SYSTEM
            );

            return Result.buildSuc();
        } catch (NotExistException nee) {
            log.error("method=modifyThrottleTaskByKafkaClient||clusterPhyId={}||errMsg=not exist error", param.getClusterPhyId(), nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=modifyThrottleTaskByKafkaClient||clusterPhyId={}||param={}||errMsg=exception", param.getClusterPhyId(), param, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> executeTaskByZKClient(VersionItemParam itemParam) {
        ExecuteReassignParam param = (ExecuteReassignParam) itemParam;
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(param.getClusterPhyId());

            ReassignPartitionsCommand.executeAssignment(kafkaZkClient, param.getReassignmentJson(), param.getThrottleUnitB());

            return Result.buildSuc();
        } catch (NotExistException nee) {
            log.error("method=executeTaskByZKClient||clusterPhyId={}||errMsg=not exist error", param.getClusterPhyId(), nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=executeTaskByZKClient||clusterPhyId={}||param={}||errMsg=exception", param.getClusterPhyId(), param, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Void> executeTaskByKafkaClient(VersionItemParam itemParam) {
        ExecuteReassignParam param = (ExecuteReassignParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            ReassignPartitionsCommand.executeAssignment(
                    adminClient,
                    false,
                    param.getReassignmentJson(),
                    param.getThrottleUnitB(),
                    -1L,
                    KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS,
                    Time.SYSTEM
            );

            return Result.buildSuc();
        } catch (NotExistException nee) {
            log.error("method=executeTaskByKafkaClient||clusterPhyId={}||errMsg=not exist error", param.getClusterPhyId(), nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=executeTaskByKafkaClient||clusterPhyId={}||param={}||errMsg=exception", param.getClusterPhyId(), param, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<ReassignResult> verifyTaskByZKClient(VersionItemParam itemParam) {
        ExecuteReassignParam param = (ExecuteReassignParam) itemParam;
        try {
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(param.getClusterPhyId());

            ReassignPartitionsCommand.VerifyAssignmentResult assignmentResult = ReassignPartitionsCommand.verifyAssignment(
                    kafkaZkClient,
                    param.getReassignmentJson(),
                    false
            );

            return Result.buildSuc(ReassignConverter.convert2ReassignmentResult(assignmentResult));
        } catch (NotExistException nee) {
            log.error("method=verifyTaskByZKClient||clusterPhyId={}||errMsg=not exist error", param.getClusterPhyId(), nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=verifyTaskByZKClient||clusterPhyId={}||param={}||errMsg=exception", param.getClusterPhyId(), param, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<ReassignResult> verifyTaskByKafkaClient(VersionItemParam itemParam) {
        ExecuteReassignParam param = (ExecuteReassignParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            ReassignPartitionsCommand.VerifyAssignmentResult assignmentResult = ReassignPartitionsCommand.verifyAssignment(
                    adminClient,
                    param.getReassignmentJson(),
                    false
            );

            return Result.buildSuc(ReassignConverter.convert2ReassignmentResult(assignmentResult));
        } catch (NotExistException nee) {
            log.error("method=verifyTaskByKafkaClient||clusterPhyId={}||errMsg=not exist error", param.getClusterPhyId(), nee);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.error("method=verifyTaskByKafkaClient||clusterPhyId={}||param={}||errMsg=exception", param.getClusterPhyId(), param, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Map<TopicPartition, List<Integer>> filterAndConvertReassignmentMap(Map<TopicPartition, Seq<Object>> rowScalaReassignmentMap, List<Integer> partitionIdList) {
        Map<TopicPartition, List<Integer>> reassignmentMap = new HashMap<>();

        rowScalaReassignmentMap.entrySet()
                .stream()
                .filter(
                        // 过滤掉不需要的分区
                        entry1 -> ValidateUtils.isEmptyList(partitionIdList) || partitionIdList.contains(entry1.getKey().partition()))
                .forEach(
                        // 转换格式
                        entry2 -> reassignmentMap.put(
                                entry2.getKey(),
                                CollectionConverters.asJava(entry2.getValue()).stream().map(brokerId -> (Integer)brokerId).collect(Collectors.toList())
                        )
                );

        return reassignmentMap;
    }

    private String generateTopicMoveJson(String topicName) {
        Map<String, Object> topicNameMap = new HashMap<>(1);
        topicNameMap.put("topic", topicName);

        Properties properties = new Properties();
        properties.put("topics", Arrays.asList(topicNameMap));

        properties.put("version", KafkaConstant.DATA_VERSION_ONE);

        return ConvertUtil.obj2Json(properties);
    }

    private Map<TopicPartition, List<Integer>> generateReplicaChangeReassignmentJson(String topicName,
                                                                                     Integer newReplicaNum,
                                                                                     List<Integer> brokerIdList,
                                                                                     Map<Integer, List<Integer>> currentPartitionMap) throws AdminOperateException{
        Map<Integer, Integer> brokerIdReplicasMap = this.initBrokerUsed(currentPartitionMap, brokerIdList);

        Map<TopicPartition, List<Integer>> finalAssignmentMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry: currentPartitionMap.entrySet()) {
            // 获取变更的副本数
            Integer changedReplicaNum = newReplicaNum - entry.getValue().size();

            if (changedReplicaNum > 0) {
                // 扩副本
                finalAssignmentMap.put(
                        new TopicPartition(topicName, entry.getKey()),
                        this.generatePartitionAddReplicaAssignment(entry.getValue(), brokerIdList, brokerIdReplicasMap, changedReplicaNum)
                );
            } else if (changedReplicaNum < 0) {
                // 缩副本，直接缩小末尾几个副本
                finalAssignmentMap.put(
                        new TopicPartition(topicName, entry.getKey()),
                        entry.getValue().subList(0, entry.getValue().size() + changedReplicaNum)
                );
            } else {
                // 没有变化
                finalAssignmentMap.put(new TopicPartition(topicName, entry.getKey()), entry.getValue());
            }
        }

        return finalAssignmentMap;
    }

    private List<Integer> generatePartitionAddReplicaAssignment(List<Integer> presentBrokerIdList,
                                                                List<Integer> assignBrokerIdList,
                                                                Map<Integer, Integer> brokerIdReplicasMap,
                                                                Integer changedReplicaNum) throws AdminOperateException{
        List<Integer> finalBrokerIdList = new ArrayList<>(presentBrokerIdList);

        for (int idx = 0; idx < changedReplicaNum; ++idx) {
            Integer targetBrokerId = null;
            for (Integer brokerId: brokerIdReplicasMap.keySet()) {
                if (finalBrokerIdList.contains(brokerId) || !assignBrokerIdList.contains(brokerId)) {
                    // 如果当前副本已经落在该broker上， 或者  当前broker不在目标broker里面，则跳过该broker
                    continue;
                }

                if (targetBrokerId == null) {
                    targetBrokerId = brokerId;
                }

                if (brokerIdReplicasMap.get(targetBrokerId) > brokerIdReplicasMap.get(brokerId)) {
                    // targetBroker副本数 > 当前broker副本数，则用当前broker替换targetBroker
                    targetBrokerId = brokerId;
                }
            }

            if (targetBrokerId == null) {
                // 没有找到合适的broker，则直接抛出异常
                throw new AdminOperateException("find targetBrokerId failed, maybe brokers not enough");
            }

            finalBrokerIdList.add(targetBrokerId);

            // 该broker拥有的副本数+1
            Integer replicas = brokerIdReplicasMap.get(targetBrokerId);
            brokerIdReplicasMap.put(targetBrokerId, replicas + 1);
        }

        return finalBrokerIdList;
    }

    private Map<Integer, Integer> initBrokerUsed(Map<Integer, List<Integer>> currentPartitionMap, List<Integer> brokerIdList) {
        Map<Integer, Integer> brokerIdReplicasMap = new HashMap<>();

        currentPartitionMap.entrySet().stream().forEach(entry -> {
            for (Integer brokerId: entry.getValue()) {
                Integer replicas = brokerIdReplicasMap.getOrDefault(brokerId, 0);
                brokerIdReplicasMap.put(brokerId, replicas + 1);
            }
        });

        brokerIdList.stream().forEach(brokerId -> brokerIdReplicasMap.putIfAbsent(brokerId, 0));

        return brokerIdReplicasMap;
    }
}
