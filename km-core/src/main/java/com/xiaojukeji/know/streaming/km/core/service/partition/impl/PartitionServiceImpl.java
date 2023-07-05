package com.xiaojukeji.know.streaming.km.core.service.partition.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.offset.KSOffsetSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.partition.PartitionOffsetParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.po.partition.PartitionPO;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.converter.PartitionConverter;
import com.xiaojukeji.know.streaming.km.common.enums.cluster.ClusterRunStateEnum;
import com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.exception.VCHandlerNotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.Triple;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.persistence.cache.DataBaseDataLocalCache;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.brokers.PartitionMap;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseKafkaVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaConsumerClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.partition.PartitionDAO;
import com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.service.KafkaZKDAO;
import kafka.zk.TopicPartitionStateZNode;
import kafka.zk.TopicPartitionsZNode;
import kafka.zk.TopicZNode;
import kafka.zk.TopicsZNode;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.VC_HANDLE_NOT_EXIST;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_PARTITION;

/**
 * @author didi
 */
@Service
public class PartitionServiceImpl extends BaseKafkaVersionControlService implements PartitionService {
    private static final ILog log = LogFactory.getLog(PartitionServiceImpl.class);

    private static final String PARTITION_OFFSET_GET    = "getPartitionOffset";

    @Autowired
    private KafkaZKDAO kafkaZKDAO;

    @Autowired
    private PartitionDAO partitionDAO;

    @Autowired
    private KafkaAdminClient kafkaAdminClient;

    @Autowired
    private KafkaConsumerClient kafkaConsumerClient;

    @Override
    protected VersionItemTypeEnum getVersionItemType() {
        return SERVICE_OP_PARTITION;
    }

    @PostConstruct
    private void init() {
        registerVCHandler(PARTITION_OFFSET_GET,     V_0_10_0_0, V_0_11_0_0,  "batchGetPartitionOffsetFromKafkaConsumerClient",  this::batchGetPartitionOffsetFromKafkaConsumerClient);
        registerVCHandler(PARTITION_OFFSET_GET,     V_0_11_0_0, V_MAX,       "batchGetPartitionOffsetFromKafkaAdminClient",     this::batchGetPartitionOffsetFromKafkaAdminClient);
    }

    @Override
    public Result<Map<String, List<Partition>>> listPartitionsFromKafka(ClusterPhy clusterPhy) {
        if (clusterPhy.getRunState().equals(ClusterRunStateEnum.RUN_ZK.getRunState())) {
            return this.getPartitionsFromZKClient(clusterPhy);
        }

        return this.getPartitionsFromAdminClient(clusterPhy);
    }

    @Override
    public Result<List<Partition>> listPartitionsFromKafka(ClusterPhy clusterPhy, String topicName) {
        if (clusterPhy.getRunState().equals(ClusterRunStateEnum.RUN_ZK.getRunState())) {
            return this.getPartitionsFromZKClientByClusterTopicName(clusterPhy,topicName);
        }
        return this.getPartitionsFromAdminClientByClusterTopicName(clusterPhy,topicName);

    }

    @Override
    public List<Partition> listPartitionByCluster(Long clusterPhyId) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);

        return this.convert2PartitionList(partitionDAO.selectList(lambdaQueryWrapper));
    }

    @Override
    public List<PartitionPO> listPartitionPOByCluster(Long clusterPhyId) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);

        return partitionDAO.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<Partition> listPartitionByTopic(Long clusterPhyId, String topicName) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(PartitionPO::getTopicName, topicName);

        return this.convert2PartitionList(partitionDAO.selectList(lambdaQueryWrapper));
    }

    @Override
    public List<Partition> listPartitionFromCacheFirst(Long clusterPhyId) {
        Map<String, List<Partition>> partitionMap = DataBaseDataLocalCache.getPartitions(clusterPhyId);

        if (partitionMap != null) {
            return partitionMap.values().stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        }

        return this.listPartitionByCluster(clusterPhyId);
    }

    @Override
    public List<Partition> listPartitionFromCacheFirst(Long clusterPhyId, Integer brokerId) {
        List<Partition> partitionList = this.listPartitionFromCacheFirst(clusterPhyId);

        return partitionList.stream().filter(elem -> elem.getAssignReplicaList().contains(brokerId)).collect(Collectors.toList());
    }

    @Override
    public List<Partition> listPartitionFromCacheFirst(Long clusterPhyId, String topicName) {
        Map<String, List<Partition>> partitionMap = DataBaseDataLocalCache.getPartitions(clusterPhyId);

        if (partitionMap != null) {
            return partitionMap.getOrDefault(topicName, new ArrayList<>());
        }

        return this.listPartitionByTopic(clusterPhyId, topicName);
    }

    @Override
    public Partition getPartitionFromCacheFirst(Long clusterPhyId, String topicName, Integer partitionId) {
        List<Partition> partitionList = this.listPartitionFromCacheFirst(clusterPhyId, topicName);
        if (ValidateUtils.isEmptyList(partitionList)) {
            return null;
        }

        for (Partition partition: partitionList) {
            if (partition.getPartitionId().equals(partitionId)) {
                return partition;
            }
        }

        return null;
    }

    @Override
    public Partition getPartitionByTopicAndPartitionId(Long clusterPhyId, String topicName, Integer partitionId) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(PartitionPO::getTopicName, topicName);
        lambdaQueryWrapper.eq(PartitionPO::getPartitionId, partitionId);

        return this.convert2Partition(partitionDAO.selectOne(lambdaQueryWrapper));
    }

    @Override
    public Result<Map<TopicPartition, Long>> getAllPartitionOffsetFromKafka(Long clusterPhyId, KSOffsetSpec offsetSpec) {
        List<TopicPartition> tpList = this.listPartitionFromCacheFirst(clusterPhyId).stream()
                .filter(item -> !item.getLeaderBrokerId().equals(KafkaConstant.NO_LEADER))
                .map(elem -> new TopicPartition(elem.getTopicName(), elem.getPartitionId()))
                .collect(Collectors.toList());

        try {
            Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>> listResult =
                    (Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>>) doVCHandler(clusterPhyId, PARTITION_OFFSET_GET, new PartitionOffsetParam(clusterPhyId, offsetSpec, tpList));

            return this.convert2OffsetMapResult(listResult);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, String topicName, KSOffsetSpec offsetSpec) {
        List<TopicPartition> tpList = this.listPartitionFromCacheFirst(clusterPhyId, topicName).stream()
                .filter(item -> !item.getLeaderBrokerId().equals(KafkaConstant.NO_LEADER))
                .map(elem -> new TopicPartition(topicName, elem.getPartitionId()))
                .collect(Collectors.toList());

        if (tpList.isEmpty()) {
            // 所有分区no-leader
            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FAILED, MsgConstant.getPartitionNoLeader(clusterPhyId, topicName));
        }

        try {
            Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>> listResult =
                    (Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>>) doVCHandler(clusterPhyId, PARTITION_OFFSET_GET, new PartitionOffsetParam(clusterPhyId, topicName, offsetSpec, tpList));

            return this.convert2OffsetMapResult(listResult);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Tuple<Map<TopicPartition, Long>, Map<TopicPartition, Long>>> getPartitionBeginAndEndOffsetFromKafka(Long clusterPhyId, String topicName) {
        List<TopicPartition> tpList = this.listPartitionFromCacheFirst(clusterPhyId, topicName).stream()
                .filter(item -> !item.getLeaderBrokerId().equals(KafkaConstant.NO_LEADER))
                .map(elem -> new TopicPartition(topicName, elem.getPartitionId()))
                .collect(Collectors.toList());

        if (tpList.isEmpty()) {
            // 所有分区no-leader
            return Result.buildFromRSAndMsg(ResultStatus.OPERATION_FAILED, MsgConstant.getPartitionNoLeader(clusterPhyId, topicName));
        }

        try {
            Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>> listResult =
                    (Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>>) doVCHandler(clusterPhyId, PARTITION_OFFSET_GET, new PartitionOffsetParam(clusterPhyId, topicName, Arrays.asList(KSOffsetSpec.earliest(), KSOffsetSpec.latest()), tpList));
            if (listResult.failed()) {
                return Result.buildFromIgnoreData(listResult);
            } else if (ValidateUtils.isEmptyList(listResult.getData())) {
                return Result.buildSuc(new Tuple<Map<TopicPartition, Long>, Map<TopicPartition, Long>>(new HashMap<>(0), new HashMap<>(0)));
            }

            Tuple<Map<TopicPartition, Long>, Map<TopicPartition, Long>> tuple = new Tuple<>(new HashMap<>(0), new HashMap<>(0));
            listResult.getData().forEach(elem -> {
                if (elem.getV1() instanceof KSOffsetSpec.KSEarliestSpec) {
                    tuple.setV1(elem.v2());
                } else if (elem.v1() instanceof KSOffsetSpec.KSLatestSpec) {
                    tuple.setV2(elem.v2());
                }
            });

            return Result.buildSuc(tuple);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, String topicName, Integer partitionId, KSOffsetSpec offsetSpec) {
        if (partitionId == null) {
            return this.getPartitionOffsetFromKafka(clusterPhyId, topicName, offsetSpec);
        }

        List<TopicPartition> tpList = this.listPartitionFromCacheFirst(clusterPhyId, topicName).stream()
                .filter(item -> !item.getLeaderBrokerId().equals(KafkaConstant.NO_LEADER))
                .filter(partition -> partition.getPartitionId().equals(partitionId))
                .map(elem -> new TopicPartition(topicName, elem.getPartitionId()))
                .collect(Collectors.toList());

        if (ValidateUtils.isEmptyList(tpList)) {
            return Result.buildSuc(new HashMap<>(0));
        }

        try {
            Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>> listResult =
                    (Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>>) doVCHandler(clusterPhyId, PARTITION_OFFSET_GET, new PartitionOffsetParam(clusterPhyId, topicName, offsetSpec, tpList));

            return this.convert2OffsetMapResult(listResult);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, List<TopicPartition> tpList, KSOffsetSpec offsetSpec) {
        // 集群具有leader的分区列表
        Set<TopicPartition> existLeaderTPSet = this.listPartitionFromCacheFirst(clusterPhyId).stream()
                .filter(item -> !item.getLeaderBrokerId().equals(KafkaConstant.NO_LEADER))
                .map(elem -> new TopicPartition(elem.getTopicName(), elem.getPartitionId()))
                .collect(Collectors.toSet());

        List<TopicPartition> existLeaderTPList = tpList.stream().filter(elem -> existLeaderTPSet.contains(elem)).collect(Collectors.toList());
        if (existLeaderTPList.isEmpty()) {
            return Result.buildSuc(new HashMap<>(0));
        }

        try {
            Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>> listResult = (Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>>) doVCHandler(
                    clusterPhyId,
                    PARTITION_OFFSET_GET,
                    new PartitionOffsetParam(clusterPhyId, offsetSpec, existLeaderTPList)
            );

            return this.convert2OffsetMapResult(listResult);
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public int updatePartitions(Long clusterPhyId, String topicName, List<Partition> kafkaPartitionList, List<PartitionPO> dbPartitionList) {
        Map<Integer, Partition> partitionMap = kafkaPartitionList.stream().collect(Collectors.toMap(Partition::getPartitionId, Function.identity()));

        // 更新已有的分区
        for (PartitionPO dbPartitionPO: dbPartitionList) {
            Partition partition = partitionMap.remove(dbPartitionPO.getPartitionId());
            if (partition == null) {
                // 分区不存在，则进行删除
                partitionDAO.deleteById(dbPartitionPO.getId());
                continue;
            }

            PartitionPO presentPartitionPO = this.convert2PartitionPO(partition);
            if (presentPartitionPO.equals(dbPartitionPO)) {
                // 数据一样，不进行DB操作
                continue;
            }
            presentPartitionPO.setId(dbPartitionPO.getId());
            partitionDAO.updateById(presentPartitionPO);
        }

        // 插入新的分区
        for (Partition partition: partitionMap.values()) {
            try {
                partitionDAO.insert(this.convert2PartitionPO(partition));
            } catch (DuplicateKeyException dke) {
                // 多台部署，因此可能会存在重复，此时如果是重复的错误，则忽略该错误
            }
        }

        return kafkaPartitionList.size();
    }

    @Override
    public void deletePartitionsIfNotIn(Long clusterPhyId, Set<String> topicNameSet) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);

        try {
            List<PartitionPO> poList = partitionDAO.selectList(lambdaQueryWrapper);
            for (PartitionPO po: poList) {
                if (topicNameSet.contains(po.getTopicName())) {
                    continue;
                }

                // 不存在 则删除
                partitionDAO.deleteById(po.getId());
            }
        } catch (Exception e) {
            log.error("method=deletePartitionsIfNotIn||clusterPhyId={}||msg=delete failed", clusterPhyId, e);
        }
    }


    /**************************************************** private method ****************************************************/

    private Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>> batchGetPartitionOffsetFromKafkaAdminClient(VersionItemParam itemParam) {
        PartitionOffsetParam offsetParam = (PartitionOffsetParam) itemParam;
        if (offsetParam.getOffsetSpecList().isEmpty()) {
            return Result.buildSuc(Collections.emptyList());
        }

        List<Triple<String, KSOffsetSpec, ListOffsetsResult>> resultList = new ArrayList<>();
        for (Triple<String, KSOffsetSpec, List<TopicPartition>> elem: offsetParam.getOffsetSpecList()) {
            Result<ListOffsetsResult> offsetsResult = this.getPartitionOffsetFromKafkaAdminClient(
                    offsetParam.getClusterPhyId(),
                    elem.v1(),
                    elem.v2(),
                    elem.v3()
            );

            if (offsetsResult.failed() && offsetParam.getOffsetSpecList().size() == 1) {
                return Result.buildFromIgnoreData(offsetsResult);
            }

            if (offsetsResult.hasData()) {
                resultList.add(new Triple<>(elem.v1(), elem.v2(), offsetsResult.getData()));
            }
        }

        List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>> offsetMapList = new ArrayList<>();
        for (Triple<String, KSOffsetSpec, ListOffsetsResult> triple: resultList) {
            try {
                Map<TopicPartition, Long> offsetMap = new HashMap<>();
                triple.v3().all().get().entrySet().stream().forEach(elem -> offsetMap.put(elem.getKey(), elem.getValue().offset()));

                offsetMapList.add(new Tuple<>(triple.v2(), offsetMap));
            } catch (Exception e) {
                log.error(
                        "method=batchGetPartitionOffsetFromKafkaAdminClient||clusterPhyId={}||topicName={}||offsetSpec={}||errMsg=exception!",
                        offsetParam.getClusterPhyId(), triple.v1(), triple.v2(), e
                );
            }
        }

        return Result.buildSuc(offsetMapList);
    }

    private Result<ListOffsetsResult> getPartitionOffsetFromKafkaAdminClient(Long clusterPhyId, String topicName, KSOffsetSpec offsetSpec, List<TopicPartition> tpList) {
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(clusterPhyId);

            Map<TopicPartition, OffsetSpec> kafkaOffsetSpecMap = new HashMap<>(tpList.size());
            tpList.forEach(elem -> {
                if (offsetSpec instanceof KSOffsetSpec.KSEarliestSpec) {
                    kafkaOffsetSpecMap.put(elem, OffsetSpec.earliest());
                } else if (offsetSpec instanceof KSOffsetSpec.KSLatestSpec) {
                    kafkaOffsetSpecMap.put(elem, OffsetSpec.latest());
                } else if (offsetSpec instanceof KSOffsetSpec.KSTimestampSpec) {
                    kafkaOffsetSpecMap.put(elem, OffsetSpec.forTimestamp(((KSOffsetSpec.KSTimestampSpec) offsetSpec).timestamp()));
                }
            });

            ListOffsetsResult listOffsetsResult = adminClient.listOffsets(kafkaOffsetSpecMap, new ListOffsetsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));

            return Result.buildSuc(listOffsetsResult);
        } catch (NotExistException nee) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        } catch (Exception e) {
            log.error(
                    "method=getPartitionOffsetFromKafkaAdminClient||clusterPhyId={}||topicName={}||errMsg=exception!",
                    clusterPhyId, topicName, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>> batchGetPartitionOffsetFromKafkaConsumerClient(VersionItemParam itemParam) {
        PartitionOffsetParam offsetParam = (PartitionOffsetParam) itemParam;
        if (offsetParam.getOffsetSpecList().isEmpty()) {
            return Result.buildSuc(Collections.emptyList());
        }

        List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>> offsetMapList = new ArrayList<>();
        for (Triple<String, KSOffsetSpec, List<TopicPartition>> triple: offsetParam.getOffsetSpecList()) {
            Result<Map<TopicPartition, Long>> subOffsetMapResult = this.getPartitionOffsetFromKafkaConsumerClient(
                    offsetParam.getClusterPhyId(),
                    triple.v1(),
                    triple.v2(),
                    triple.v3()
            );

            if (subOffsetMapResult.failed() && offsetParam.getOffsetSpecList().size() == 1) {
                return Result.buildFromIgnoreData(subOffsetMapResult);
            }

            if (subOffsetMapResult.hasData()) {
                offsetMapList.add(new Tuple<>(triple.v2(), subOffsetMapResult.getData()));
            }
        }

        return Result.buildSuc(offsetMapList);
    }

    private Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafkaConsumerClient(Long clusterPhyId, String topicName, KSOffsetSpec offsetSpec, List<TopicPartition> tpList) {
        KafkaConsumer<String, String> kafkaConsumer = null;

        try {
            if (ValidateUtils.isEmptyList(tpList)) {
                return Result.buildSuc(new HashMap<>());
            }

            kafkaConsumer = kafkaConsumerClient.getClient(clusterPhyId);

            if (offsetSpec instanceof KSOffsetSpec.KSLatestSpec) {
                return Result.buildSuc(
                        kafkaConsumer.endOffsets(
                                tpList,
                                Duration.ofMillis(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
                        )
                );
            }

            if (offsetSpec instanceof KSOffsetSpec.KSEarliestSpec) {
                return Result.buildSuc(
                        kafkaConsumer.beginningOffsets(
                                tpList,
                                Duration.ofMillis(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
                        )
                );
            }

            if (offsetSpec instanceof KSOffsetSpec.KSTimestampSpec) {
                // 按照时间进行查找
                Map<TopicPartition, Long> timestampMap = new HashMap<>();
                tpList.forEach(elem -> timestampMap.put(elem, ((KSOffsetSpec.KSTimestampSpec) offsetSpec).timestamp()));

                Map<TopicPartition, OffsetAndTimestamp> offsetMetadataMap = kafkaConsumer.offsetsForTimes(
                        timestampMap,
                        Duration.ofMillis(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
                );

                Map<TopicPartition, Long> offsetMap = new HashMap<>();
                offsetMetadataMap.entrySet().stream().forEach(elem -> offsetMap.put(elem.getKey(), elem.getValue().offset()));
                return Result.buildSuc(offsetMap);
            }

            return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, "OffsetSpec type illegal");
        } catch (NotExistException nee) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        } catch (Exception e) {
            log.error(
                    "method=getPartitionOffsetFromKafkaConsumerClient||clusterPhyId={}||topicName={}||errMsg=exception!",
                    clusterPhyId, topicName, e
            );

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        } finally {
            if (kafkaConsumer != null) {
                kafkaConsumerClient.returnClient(clusterPhyId, kafkaConsumer);
            }
        }
    }

    private Result<Map<String, List<Partition>>> getPartitionsFromAdminClient(ClusterPhy clusterPhy) {
        Map<String, List<Partition>> partitionMap = new HashMap<>();

        try {
            AdminClient adminClient = kafkaAdminClient.getClient(clusterPhy.getId());

            // 获取Topic列表
            ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS).listInternal(true));
            for (String topicName: listTopicsResult.names().get()) {
                Result<List<Partition>> partitionListRes = this.getPartitionsFromAdminClientByClusterTopicName(clusterPhy, topicName);
                if (partitionListRes.successful()){
                    partitionMap.put(topicName, partitionListRes.getData());
                }else {
                    return Result.buildFromIgnoreData(partitionListRes);
                }
            }

            return Result.buildSuc(partitionMap);
        } catch (Exception e) {
            log.error("method=getPartitionsFromAdminClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Map<String, List<Partition>>> getPartitionsFromZKClient(ClusterPhy clusterPhy) {
        Map<String, List<Partition>> partitionMap = new HashMap<>();

        try {
            List<String> topicNameList = kafkaZKDAO.getChildren(clusterPhy.getId(), TopicsZNode.path(), false);
            for (String topicName: topicNameList) {
                Result<List<Partition>> partitionListRes = this.getPartitionsFromZKClientByClusterTopicName(clusterPhy, topicName);
                if (partitionListRes.successful()){
                    partitionMap.put(topicName, partitionListRes.getData());
                }
            }
            return Result.buildSuc(partitionMap);
        } catch (Exception e) {
            log.error("method=getPartitionsFromZKClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<List<Partition>> getPartitionsFromAdminClientByClusterTopicName(ClusterPhy clusterPhy, String topicName) {

        try {
            AdminClient adminClient = kafkaAdminClient.getClient(clusterPhy.getId());
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(
                    Arrays.asList(topicName),
                    new DescribeTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
            );
            TopicDescription description = describeTopicsResult.all().get().get(topicName);
            return Result.buildSuc(PartitionConverter.convert2PartitionList(clusterPhy.getId(), description));
        }catch (Exception e) {
            log.error("method=getPartitionsFromAdminClientByClusterTopicName||clusterPhyId={}||topicName={}||errMsg=exception", clusterPhy.getId(),topicName, e);
            return Result.buildFailure(ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }

    private Result<List<Partition>> getPartitionsFromZKClientByClusterTopicName(ClusterPhy clusterPhy, String topicName) {
        try {
                PartitionMap zkPartitionMap = kafkaZKDAO.getData(clusterPhy.getId(), TopicZNode.path(topicName), PartitionMap.class);
                List<Partition> partitionList = new ArrayList<>();
                List<String> partitionIdList = kafkaZKDAO.getChildren(clusterPhy.getId(), TopicPartitionsZNode.path(topicName), false);
                for (String partitionId: partitionIdList) {
                    PartitionState partitionState = kafkaZKDAO.getData(clusterPhy.getId(), TopicPartitionStateZNode.path(new TopicPartition(topicName, Integer.valueOf(partitionId))), PartitionState.class);
                    Partition partition = new Partition();
                    partition.setClusterPhyId(clusterPhy.getId());
                    partition.setTopicName(topicName);
                    partition.setPartitionId(Integer.valueOf(partitionId));
                    partition.setLeaderBrokerId(partitionState.getLeader());
                    partition.setInSyncReplicaList(partitionState.getIsr());
                    partition.setAssignReplicaList(zkPartitionMap.getPartitionAssignReplicas(Integer.valueOf(partitionId)));
                    partitionList.add(partition);
                }
            return Result.buildSuc(partitionList);
        } catch (Exception e) {
            log.error("method=getPartitionsFromZKClientByClusterTopicName||clusterPhyId={}||topicName={}||errMsg=exception", clusterPhy.getId(),topicName, e);
            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private List<Partition> convert2PartitionList(List<PartitionPO> poList) {
        if (poList == null) {
            return new ArrayList<>();
        }

        List<Partition> partitionList = new ArrayList<>();
        for (PartitionPO po: poList) {
            if(null != po) {
                partitionList.add(this.convert2Partition(po));
            }
        }
        return partitionList;
    }

    private Result<Map<TopicPartition, Long>> convert2OffsetMapResult(Result<List<Tuple<KSOffsetSpec, Map<TopicPartition, Long>>>> listResult) {
        if (listResult.failed()) {
            return Result.buildFromIgnoreData(listResult);
        } else if (ValidateUtils.isEmptyList(listResult.getData())) {
            return Result.buildSuc(new HashMap<>(0));
        }

        Map<TopicPartition, Long> offsetMap = new HashMap<>();
        listResult.getData().forEach(elem -> offsetMap.putAll(elem.v2()));

        return Result.buildSuc(offsetMap);
    }

    private PartitionPO convert2PartitionPO(Partition partition) {
        if (partition == null) {
            return null;
        }

        PartitionPO po = new PartitionPO();
        po.setClusterPhyId(partition.getClusterPhyId());
        po.setTopicName(partition.getTopicName());
        po.setPartitionId(partition.getPartitionId());
        po.setLeaderBrokerId(partition.getLeaderBrokerId());
        po.setInSyncReplicas(CommonUtils.intList2String(partition.getInSyncReplicaList()));
        po.setAssignReplicas(CommonUtils.intList2String(partition.getAssignReplicaList()));
        return po;
    }

    private Partition convert2Partition(PartitionPO po) {
        if(null == po){return null;}

        Partition partition = new Partition();
        partition.setClusterPhyId(po.getClusterPhyId());
        partition.setTopicName(po.getTopicName());
        partition.setPartitionId(po.getPartitionId());
        partition.setLeaderBrokerId(po.getLeaderBrokerId());
        partition.setInSyncReplicaList(CommonUtils.string2IntList(po.getInSyncReplicas()));
        partition.setAssignReplicaList(CommonUtils.string2IntList(po.getAssignReplicas()));
        return partition;
    }
}
