package com.xiaojukeji.know.streaming.km.biz.topic.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.topic.OpTopicManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicExpansionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaTopicConfigParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicCreateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicPartitionExpandParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicTruncateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.utils.*;
import com.xiaojukeji.know.streaming.km.common.utils.kafka.KafkaReplicaAssignUtil;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.topic.OpTopicService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicConfigService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import kafka.admin.AdminUtils;
import kafka.admin.BrokerMetadata;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import scala.Option;
import scala.collection.Seq;
import scala.jdk.javaapi.CollectionConverters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OpTopicManagerImpl implements OpTopicManager {
    private static final ILog log = LogFactory.getLog(OpTopicManagerImpl.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private OpTopicService opTopicService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private TopicConfigService topicConfigService;

    @Override
    public Result<Void> createTopic(TopicCreateDTO dto, String operator) {
        log.info("method=createTopic||param={}||operator={}.", dto, operator);

        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(dto.getClusterId());
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(dto.getClusterId()));
        }

        // 构造assignmentMap
        scala.collection.Map<Object, Seq<Object>> rawAssignmentMap = AdminUtils.assignReplicasToBrokers(
                this.buildBrokerMetadataSeq(dto.getClusterId(), dto.getBrokerIdList()),
                dto.getPartitionNum(),
                dto.getReplicaNum(),
                -1,
                -1
        );

        // 类型转换
        Map<Integer, List<Integer>> assignmentMap = new HashMap<>();
        rawAssignmentMap.
                toStream().
                foreach(elem -> assignmentMap.put(
                        (Integer) elem._1,
                        CollectionConverters.asJava(elem._2).stream().map(item -> (Integer)item).collect(Collectors.toList()))
                );

        // 创建Topic
        Result<Void> createTopicRes = opTopicService.createTopic(
                new TopicCreateParam(
                        dto.getClusterId(),
                        dto.getTopicName(),
                        new HashMap<String, String>((Map) dto.getProperties()),
                        assignmentMap,
                        dto.getDescription()
                ),
                operator
        );
        if (createTopicRes.successful()){
            try{
                FutureUtil.quickStartupFutureUtil.submitTask(() -> {
                    BackoffUtils.backoff(3000);
                    Result<List<Partition>> partitionsResult = partitionService.listPartitionsFromKafka(clusterPhy, dto.getTopicName());
                    if (partitionsResult.successful()){
                        partitionService.updatePartitions(clusterPhy.getId(), dto.getTopicName(), partitionsResult.getData(),  new ArrayList<>());
                    }
                });
            }catch (Exception e) {
                log.error("method=createTopic||param={}||operator={}||msg=add partition to db failed||errMsg=exception", dto, operator, e);
                return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, "Topic创建成功，但记录Partition到DB中失败，等待定时任务同步partition信息");
            }
        }
        return createTopicRes;
    }

    @Override
    public Result<Void> deleteTopicCombineRes(Long clusterPhyId, String topicName, String operator) {
        // 删除Topic
        Result<Void> rv = opTopicService.deleteTopic(new TopicParam(clusterPhyId, topicName), operator);
        if (rv.failed()) {
            return rv;
        }

        // 删除Topic相关的ACL信息

        return Result.buildSuc();
    }

    @Override
    @Transactional
    public Result<Void> expandTopic(TopicExpansionDTO dto, String operator) {
        Topic topic = topicService.getTopic(dto.getClusterId(), dto.getTopicName());
        if (topic == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(dto.getClusterId(), dto.getTopicName()));
        }

        TopicPartitionExpandParam expandParam = new TopicPartitionExpandParam(
                dto.getClusterId(),
                dto.getTopicName(),
                topic.getPartitionMap(),
                this.generateNewPartitionAssignment(dto.getClusterId(), topic, dto.getBrokerIdList(), dto.getIncPartitionNum())
        );

        // 更新DB分区数信息, 其他信息交由后台任务进行更新
        Result<Void> rv = topicService.updatePartitionNum(topic.getClusterPhyId(), topic.getTopicName(), topic.getPartitionNum() + dto.getIncPartitionNum());
        if (rv.failed()){
            return rv;
        }

        rv = opTopicService.expandTopic(expandParam, operator);
        if (rv.failed()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return rv;
        }
        return rv;
    }

    @Override
    public Result<Void> truncateTopic(Long clusterPhyId, String topicName, String operator) {
        // 增加delete配置
        Result<Tuple<Boolean, String>> rt = this.addDeleteConfigIfNotExist(clusterPhyId, topicName, operator);
        if (rt.failed()) {
            log.error("method=truncateTopic||clusterPhyId={}||topicName={}||operator={}||result={}||msg=get config from kafka failed", clusterPhyId, topicName, operator, rt);
            return Result.buildFromIgnoreData(rt);
        }

        // 清空Topic
        Result<Void> rv = opTopicService.truncateTopic(new TopicTruncateParam(clusterPhyId, topicName, KafkaConstant.TOPICK_TRUNCATE_DEFAULT_OFFSET), operator);
        if (rv.failed()) {
            log.error("method=truncateTopic||clusterPhyId={}||topicName={}||originConfig={}||operator={}||result={}||msg=truncate topic failed", clusterPhyId, topicName, rt.getData().v2(), operator, rv);
            // config被修改了，则错误提示需要提醒一下，否则直接返回错误
            return rt.getData().v1() ? Result.buildFailure(rv.getCode(), rv.getMessage() + "\t\n" + String.format("Topic的CleanupPolicy已被修改，需要手动恢复为%s", rt.getData().v2())) : rv;
        }

        // 恢复compact配置
        rv = this.recoverConfigIfChanged(clusterPhyId, topicName, rt.getData().v1(), rt.getData().v2(), operator);
        if (rv.failed()) {
            log.error("method=truncateTopic||clusterPhyId={}||topicName={}||originConfig={}||operator={}||result={}||msg=truncate topic success but recover config failed", clusterPhyId, topicName, rt.getData().v2(), operator, rv);
            // config被修改了，则错误提示需要提醒一下，否则直接返回错误
            return Result.buildFailure(rv.getCode(), String.format("Topic清空操作已成功，但是恢复CleanupPolicy配置失败，需要手动恢复为%s。", rt.getData().v2()) + "\t\n" + rv.getMessage());
        }

        return Result.buildSuc();
    }

    /**************************************************** private method ****************************************************/

    private Result<Tuple<Boolean, String>> addDeleteConfigIfNotExist(Long clusterPhyId, String topicName, String operator) {
        // 获取Topic配置
        Result<Map<String, String>> configMapResult = topicConfigService.getTopicConfigFromKafka(clusterPhyId, topicName);
        if (configMapResult.failed()) {
            return Result.buildFromIgnoreData(configMapResult);
        }

        String cleanupPolicyValue = configMapResult.getData().getOrDefault(TopicConfig.CLEANUP_POLICY_CONFIG, "");
        List<String> cleanupPolicyValueList = CommonUtils.string2StrList(cleanupPolicyValue);
        if (cleanupPolicyValueList.size() == 1 && cleanupPolicyValueList.contains(TopicConfig.CLEANUP_POLICY_DELETE)) {
            // 不需要修改
            return Result.buildSuc(new Tuple<>(Boolean.FALSE, cleanupPolicyValue));
        }

        Map<String, String> changedConfigMap = new HashMap<>(1);
        changedConfigMap.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE);

        Result<Void> rv = topicConfigService.modifyTopicConfig(new KafkaTopicConfigParam(clusterPhyId, topicName, changedConfigMap), operator);
        if (rv.failed()) {
            // 修改失败
            return Result.buildFromIgnoreData(rv);
        }

        return Result.buildSuc(new Tuple<>(Boolean.TRUE, cleanupPolicyValue));
    }

    private Result<Void> recoverConfigIfChanged(Long clusterPhyId, String topicName, Boolean changed, String originValue, String operator) {
        if (!changed) {
            // 没有修改，直接返回
            return Result.buildSuc();
        }

        // 恢复配置
        Map<String, String> changedConfigMap = new HashMap<>(1);
        changedConfigMap.put(TopicConfig.CLEANUP_POLICY_CONFIG, originValue);

        return topicConfigService.modifyTopicConfig(new KafkaTopicConfigParam(clusterPhyId, topicName, changedConfigMap), operator);
    }

    private Seq<BrokerMetadata> buildBrokerMetadataSeq(Long clusterPhyId, final List<Integer> selectedBrokerIdList) {
        // 选取Broker列表
        List<Broker> brokerList = brokerService.listAliveBrokersFromDB(clusterPhyId).stream().filter( elem ->
                selectedBrokerIdList == null || selectedBrokerIdList.contains(elem.getBrokerId())
        ).collect(Collectors.toList());

        List<BrokerMetadata> brokerMetadataList = new ArrayList<>();
        for (Broker broker: brokerList) {
            brokerMetadataList.add(new BrokerMetadata(broker.getBrokerId(), Option.apply(broker.getRack())));
        }

        return CollectionConverters.asScala(brokerMetadataList);
    }

    private Map<Integer, List<Integer>> generateNewPartitionAssignment(Long clusterPhyId, Topic topic, List<Integer> brokerIdList, Integer incPartitionNum) {
        if (ValidateUtils.isEmptyList(brokerIdList)) {
            // 如果brokerId列表为空，则获取当前集群存活的Broker列表
            brokerIdList = brokerService.listAliveBrokersFromDB(clusterPhyId).stream().map( elem -> elem.getBrokerId()).collect(Collectors.toList());
        }

        Map<Integer, String> brokerRackMap = new HashMap<>();
        for (Broker broker: brokerService.listAliveBrokersFromDB(clusterPhyId)) {
            if (brokerIdList != null && !brokerIdList.contains(broker.getBrokerId())) {
                continue;
            }

            brokerRackMap.put(broker.getBrokerId(), broker.getRack() == null? "": broker.getRack());
        }

        // 生成分配规则
        return KafkaReplicaAssignUtil.generateNewPartitionAssignment(brokerRackMap, topic.getPartitionMap(), incPartitionNum);
    }
}
