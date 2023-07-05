package com.xiaojukeji.know.streaming.km.core.service.topic.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.didiglobal.logi.security.common.dto.oplog.OplogDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.ha.HaActiveStandbyRelation;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicCreateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicPartitionExpandParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicTruncateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.converter.TopicConverter;
import com.xiaojukeji.know.streaming.km.common.enums.ha.HaResTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.ModuleEnum;
import com.xiaojukeji.know.streaming.km.common.enums.operaterecord.OperationEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.core.service.ha.HaActiveStandbyRelationService;
import com.xiaojukeji.know.streaming.km.core.service.oprecord.OpLogWrapService;
import com.xiaojukeji.know.streaming.km.core.service.topic.OpTopicService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminZKClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.controller.ReplicaAssignment;
import kafka.server.ConfigType;
import kafka.zk.AdminZkClient;
import kafka.zk.KafkaZkClient;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Option;
import scala.collection.Seq;
import scala.jdk.javaapi.CollectionConverters;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.VC_HANDLE_NOT_EXIST;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_TOPIC;

/**
 * @author didi
 */
@Service
public class OpTopicServiceImpl extends BaseKafkaVersionControlService implements OpTopicService {
    private static final ILog log = LogFactory.getLog(TopicConfigServiceImpl.class);

    private static final String TOPIC_CREATE    = "createTopic";
    private static final String TOPIC_DELETE    = "deleteTopic";
    private static final String TOPIC_EXPAND    = "expandTopic";
    private static final String TOPIC_TRUNCATE  = "truncateTopic";

    @Autowired
    private TopicService topicService;

    @Autowired
    private OpLogWrapService opLogWrapService;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaAdminZKClient kafkaAdminZKClient;

    @Autowired
    private KafkaZKDAO kafkaZKDAO;

    @Autowired
    private HaActiveStandbyRelationService haActiveStandbyRelationService;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return SERVICE_OP_TOPIC;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(TOPIC_CREATE,     V_0_10_0_0, V_0_10_0_1, "createByZKClientV1",          this::createByZKClientV1);
        registerVCHandler(TOPIC_CREATE,     V_0_10_0_1, V_0_11_0_3, "createByZKClientV2",          this::createByZKClientV2);
        registerVCHandler(TOPIC_CREATE,     V_0_11_0_3, V_MAX,      "createByKafkaClient",         this::createByKafkaClient);

        registerVCHandler(TOPIC_DELETE,     V_0_10_0_0, V_0_11_0_3, "deleteByZKClient",             this::deleteByZKClient);
        registerVCHandler(TOPIC_DELETE,     V_0_11_0_3, V_MAX,      "deleteByKafkaClient",          this::deleteByKafkaClient);

        registerVCHandler(TOPIC_EXPAND,     V_0_10_0_0, V_0_11_0_3, "expandTopicByZKClient",        this::expandTopicByZKClient);
        registerVCHandler(TOPIC_EXPAND,     V_0_11_0_3, V_MAX,      "expandTopicByKafkaClient",     this::expandTopicByKafkaClient);

        registerVCHandler(TOPIC_TRUNCATE,   V_0_11_0_0, V_MAX,      "truncateTopicByKafkaClient",   this::truncateTopicByKafkaClient);
    }

    @Override
    public Result<Void> createTopic(TopicCreateParam createParam, String operator) {
        // 检查参数
        Result<Void> rv = createParam.simpleCheckFieldIsNull();
        if (rv.failed()) {
            return Result.buildFromIgnoreData(rv);
        }

        // 进行Topic创建
        try {
            rv = (Result<Void>) doVCHandler(createParam.getClusterPhyId(), TOPIC_CREATE, createParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }

        if (rv == null || rv.failed()) {
            return rv;
        }

        // 记录操作
        OplogDTO oplogDTO = new OplogDTO(
                operator,
                OperationEnum.ADD.getDesc(),
                ModuleEnum.KAFKA_TOPIC.getDesc(),
                MsgConstant.getTopicBizStr(createParam.getClusterPhyId(),createParam.getTopicName()),
                "新增" + createParam.toString());
        opLogWrapService.saveOplogAndIgnoreException(oplogDTO);

        try {
            topicService.addNewTopic2DB(TopicConverter.convert2TopicPOAndIgnoreTime(createParam));
        } catch (Exception e) {
            log.error("method=createTopic||param={}||operator={}||msg=add topic to db failed||errMsg=exception", createParam, operator, e);

            return Result.buildFromRSAndMsg(ResultStatus.MYSQL_OPERATE_FAILED, "Topic创建成功，但记录到DB中失败，将缺少部分业务数据");
        }

        return rv;
    }

    @Override
    public Result<Void> deleteTopic(TopicParam param, String operator) {
        try {
            Result<Void> rv = (Result<Void>) doVCHandler(param.getClusterPhyId(), TOPIC_DELETE, param);
            if (rv.failed()) {
                return rv;
            }

            // 删除DB中的Topic数据
            topicService.deleteTopicInDB(param.getClusterPhyId(), param.getTopicName());

            //解除高可用Topic关联
            List<HaActiveStandbyRelation> haActiveStandbyRelations = haActiveStandbyRelationService.listByClusterAndType(param.getClusterPhyId(), HaResTypeEnum.MIRROR_TOPIC);
            for (HaActiveStandbyRelation activeStandbyRelation : haActiveStandbyRelations) {
                if (activeStandbyRelation.getResName().equals(param.getTopicName())) {
                    try {
                        KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(activeStandbyRelation.getStandbyClusterPhyId());
                        Properties haTopics = kafkaZkClient.getEntityConfigs("ha-topics", activeStandbyRelation.getResName());
                        if (haTopics.size() != 0) {
                            kafkaZkClient.setOrCreateEntityConfigs("ha-topics", activeStandbyRelation.getResName(), new Properties());
                            kafkaZkClient.createConfigChangeNotification("ha-topics/" + activeStandbyRelation.getResName());
                        }
                        haActiveStandbyRelationService.batchDeleteTopicHA(activeStandbyRelation.getActiveClusterPhyId(), activeStandbyRelation.getStandbyClusterPhyId(), Collections.singletonList(activeStandbyRelation.getResName()));
                    } catch (Exception e) {
                        log.error("method=deleteTopic||topicName:{}||errMsg=exception", activeStandbyRelation.getResName(), e);
                        return Result.buildFailure(e.getMessage());
                    }
                }
            }

            // 记录操作
            OplogDTO oplogDTO = new OplogDTO(operator,
                    OperationEnum.DELETE.getDesc(),
                    ModuleEnum.KAFKA_TOPIC.getDesc(),
                    MsgConstant.getTopicBizStr(param.getClusterPhyId(), param.getTopicName()),
                    String.format("删除Topic:[%s]", param.toString()));
            opLogWrapService.saveOplogAndIgnoreException(oplogDTO);
            return rv;
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Void> expandTopic(TopicPartitionExpandParam expandParam, String operator) {
        // 检查参数
        Result<Void> rv = expandParam.simpleCheckFieldIsNull();
        if (rv.failed()) {
            return Result.buildFromIgnoreData(rv);
        }

        try {
            rv = (Result<Void>) doVCHandler(expandParam.getClusterPhyId(), TOPIC_EXPAND, expandParam);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }

        if (rv == null || rv.failed()) {
            return rv;
        }
        // 记录操作
        OplogDTO oplogDTO = new OplogDTO(operator,
                OperationEnum.EDIT.getDesc(),
                ModuleEnum.KAFKA_TOPIC.getDesc(),
                MsgConstant.getTopicBizStr(expandParam.getClusterPhyId(),expandParam.getTopicName()),
                MsgConstant.getTopicExtend((long)expandParam.getExistPartitionNum(), (long) expandParam.getTotalPartitionNum(),expandParam.toString()));
        opLogWrapService.saveOplogAndIgnoreException(oplogDTO);
        return rv;
    }

    @Override
    public Result<Void> truncateTopic(TopicTruncateParam param, String operator) {
        try {
            // 清空topic数据
            Result<Void> rv = (Result<Void>) doVCHandler(param.getClusterPhyId(), TOPIC_TRUNCATE, param);

            if (rv == null || rv.failed()) {
                return rv;
            }

            // 记录操作
            OplogDTO oplogDTO = new OplogDTO(operator,
                    OperationEnum.TRUNCATE.getDesc(),
                    ModuleEnum.KAFKA_TOPIC.getDesc(),
                    MsgConstant.getTopicBizStr(param.getClusterPhyId(), param.getTopicName()),
                    String.format("清空Topic:[%s]", param.toString()));
            opLogWrapService.saveOplogAndIgnoreException(oplogDTO);
            return rv;
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    /**************************************************** private method ****************************************************/

    private Result<Void> truncateTopicByKafkaClient(VersionItemParam itemParam) {
        TopicTruncateParam param = (TopicTruncateParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());
            //获取topic的分区信息
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Arrays.asList(param.getTopicName()), new DescribeTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            Map<String, TopicDescription> descriptionMap = describeTopicsResult.all().get();

            Map<TopicPartition, RecordsToDelete> recordsToDelete = new HashMap<>();
            RecordsToDelete recordsToDeleteOffset = RecordsToDelete.beforeOffset(param.getOffset());

            descriptionMap.forEach((topicName, topicDescription) -> {
                for (TopicPartitionInfo topicPartition : topicDescription.partitions()) {
                    recordsToDelete.put(new TopicPartition(topicName, topicPartition.partition()), recordsToDeleteOffset);
                }
            });

            DeleteRecordsResult deleteRecordsResult = adminClient.deleteRecords(recordsToDelete, new DeleteRecordsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            deleteRecordsResult.all().get();
        } catch (Exception e) {
            log.error("truncate topic by kafka-client failed，clusterPhyId:{} topicName:{} offset:{}", param.getClusterPhyId(), param.getTopicName(), param.getOffset(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> deleteByKafkaClient(VersionItemParam itemParam) {
        TopicParam param = (TopicParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(param.getClusterPhyId());

            DeleteTopicsResult deleteTopicsResult = adminClient.deleteTopics(Arrays.asList(param.getTopicName()), new DeleteTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            deleteTopicsResult.all().get();
        } catch (Exception e) {
            log.error("delete topic by kafka-client failed，clusterPhyId:{} topicName:{}", param.getClusterPhyId(), param.getTopicName(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> deleteByZKClient(VersionItemParam itemParam){
        TopicParam param = (TopicParam) itemParam;

        try {
            AdminZkClient adminZkClient = kafkaAdminZKClient.getKafkaZKWrapClient(param.getClusterPhyId());

            adminZkClient.deleteTopic(param.getTopicName());
        } catch (Exception e) {
            log.error("delete topic by zk-client failed，clusterPhyId:{} topicName:{}", param.getClusterPhyId(), param.getTopicName(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> createByKafkaClient(VersionItemParam itemParam) {
        TopicCreateParam createParam = (TopicCreateParam)itemParam;

        try {
            AdminClient adminClient = kafkaAdminClient.getClient(createParam.getClusterPhyId());

            CreateTopicsResult createTopicsResult = adminClient.createTopics(
                    Arrays.asList(new NewTopic(createParam.getTopicName(), createParam.getAssignmentMap()).configs(createParam.getConfig())),
                    new CreateTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            createTopicsResult.all().get();
        } catch (Exception e) {
            log.error("method=createByKafkaClient||param={}||errMsg=exception!", createParam, e);

            return Result.buildFailure(ResultStatus.KAFKA_OPERATE_FAILED);
        }

        return Result.buildSuc();
    }

    private Result<Void> createByZKClientV2(VersionItemParam itemParam){
        TopicCreateParam createParam = (TopicCreateParam)itemParam;

        // 转换配置的类型
        Map<String, String> config = createParam.getConfig();
        Properties props = new Properties();
        props.putAll(config);

        try {
            AdminZkClient adminZkClient = kafkaAdminZKClient.getKafkaZKWrapClient(createParam.getClusterPhyId());

            adminZkClient.createTopicWithAssignment(createParam.getTopicName(), props, this.convert2ScalaMap(createParam.getAssignmentMap()), true, false);
        } catch (Exception e) {
            log.error("method=createByZKClientV2||param={}||errMsg=exception!", createParam, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> createByZKClientV1(VersionItemParam itemParam){
        TopicCreateParam createParam = (TopicCreateParam)itemParam;

        // 配置
        Map<String, String> config = createParam.getConfig();
        Properties props = new Properties();
        props.putAll(config);

        // assignment
        scala.collection.Map<Object, Seq<Object>> assignmentMap = this.convert2ScalaMap(createParam.getAssignmentMap());

        try {
            AdminZkClient adminZkClient = kafkaAdminZKClient.getKafkaZKWrapClient(createParam.getClusterPhyId());
            KafkaZkClient kafkaZkClient = kafkaAdminZKClient.getClient(createParam.getClusterPhyId());

            // 检查参数是否合法
            adminZkClient.validateTopicCreate(createParam.getTopicName(), assignmentMap, props);

            // 修改配置的数据节点
            kafkaZkClient.setOrCreateEntityConfigs(ConfigType.Topic(), createParam.getTopicName(), props);

            // 修改配置的通知节点
            kafkaZKDAO.createConfigChangeNotificationVersionOne(createParam.getClusterPhyId(), ConfigType.Topic(), createParam.getTopicName());

            // 创建TopicAssignment
            Map<TopicPartition, Seq<Object>> replicaAssignmentMap = new HashMap<>();
            for (Map.Entry<Object, Seq<Object>> entry: CollectionConverters.asJava(assignmentMap).entrySet()) {
                replicaAssignmentMap.put(new TopicPartition(createParam.getTopicName(), (Integer) entry.getKey()), entry.getValue());
            }

            kafkaZkClient.createTopicAssignment(createParam.getTopicName(), Option.apply(null),  CollectionConverters.asScala(replicaAssignmentMap));
        } catch (Exception e) {
            log.error("method=createByZKClientV1||param={}||errMsg=exception!", createParam, e);

            return Result.buildFromRSAndMsg(ResultStatus.ZK_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> expandTopicByKafkaClient(VersionItemParam itemParam) {
        TopicPartitionExpandParam expandParam = (TopicPartitionExpandParam) itemParam;
        try {
            Map<String, NewPartitions> newPartitionMap = new HashMap<>();
            newPartitionMap.put(expandParam.getTopicName(), NewPartitions.increaseTo(
                    expandParam.getTotalPartitionNum(),
                    new ArrayList<>(expandParam.getNewPartitionAssignment().values())
            ));

            AdminClient adminClient = kafkaAdminClient.getClient(expandParam.getClusterPhyId());
            CreatePartitionsResult createPartitionsResult = adminClient.createPartitions(
                    newPartitionMap,
                    new CreatePartitionsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );

            createPartitionsResult.all().get();
        } catch (NotExistException nee) {
            log.warn("method=expandTopicByKafkaClient||param={}||msg=cluster or topic not exist", expandParam);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.warn("method=expandTopicByKafkaClient||param={}||errMsg=exception!", expandParam, e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private Result<Void> expandTopicByZKClient(VersionItemParam itemParam){
        TopicPartitionExpandParam expandParam = (TopicPartitionExpandParam) itemParam;

        try {
            AdminZkClient adminZkClient = kafkaAdminZKClient.getKafkaZKWrapClient(expandParam.getClusterPhyId());

            Map<Object, ReplicaAssignment> existingAssignment = new HashMap<>();
            for (Map.Entry<Integer, List<Integer>> entry: expandParam.getExistingAssignment().entrySet()) {
                existingAssignment.put(entry.getKey(), ReplicaAssignment.apply(CollectionConverters.asScala(new ArrayList<>(entry.getValue()))));
            }

            Map<Object, ReplicaAssignment> newPartitionAssignment = new HashMap<>();
            for (Map.Entry<Integer, List<Integer>> entry: expandParam.getNewPartitionAssignment().entrySet()) {
                newPartitionAssignment.put(entry.getKey(), ReplicaAssignment.apply(CollectionConverters.asScala(new ArrayList<>(entry.getValue()))));
            }

            adminZkClient.createPartitionsWithAssignment(
                    expandParam.getTopicName(),
                    CollectionConverters.asScala(existingAssignment),
                    CollectionConverters.asScala(newPartitionAssignment)
            );
        } catch (NotExistException nee) {
            log.warn("method=expandTopicByZKClient||param={}||msg=cluster or topic not exist", expandParam);

            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, nee.getMessage());
        } catch (Exception e) {
            log.warn("method=expandTopicByZKClient||param={}||errMsg={}", expandParam, e.getMessage());

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }

        return Result.buildSuc();
    }

    private scala.collection.Map<Object, Seq<Object>> convert2ScalaMap(Map<Integer, List<Integer>> rawAssignmentMap) {
        Map<Object, Seq<Object>> assignmentMap = new HashMap<>();
        rawAssignmentMap.entrySet().stream().forEach(elem ->
                assignmentMap.put(
                        elem.getKey(),
                        CollectionConverters.asScala(elem.getValue().stream().map(item -> (Object)item).collect(Collectors.toList()))
                )
        );

        return CollectionConverters.asScala(assignmentMap);
    }
}
