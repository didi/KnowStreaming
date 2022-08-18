package com.xiaojukeji.know.streaming.km.core.service.partition.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
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
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.common.zookeeper.znode.brokers.PartitionMap;
import com.xiaojukeji.know.streaming.km.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.version.BaseVersionControlService;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaAdminClient;
import com.xiaojukeji.know.streaming.km.persistence.kafka.KafkaConsumerClient;
import com.xiaojukeji.know.streaming.km.persistence.mysql.partition.PartitionDAO;
import com.xiaojukeji.know.streaming.km.persistence.zk.KafkaZKDAO;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus.VC_HANDLE_NOT_EXIST;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionEnum.*;
import static com.xiaojukeji.know.streaming.km.common.enums.version.VersionItemTypeEnum.SERVICE_OP_PARTITION;

/**
 * @author didi
 */
@Service("partitionService")
public class PartitionServiceImpl extends BaseVersionControlService implements PartitionService {
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

    private final Cache<String, List<Partition>> partitionsCache = Caffeine.newBuilder()
            .expireAfterWrite(90, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();

    @PostConstruct
    private void init() {
        registerVCHandler(PARTITION_OFFSET_GET,     V_0_10_0_0, V_0_10_2_0,  "getPartitionOffsetFromKafkaConsumerClient",       this::getPartitionOffsetFromKafkaConsumerClient);
        registerVCHandler(PARTITION_OFFSET_GET,     V_0_10_2_0, V_MAX,       "getPartitionOffsetFromKafkaAdminClient",          this::getPartitionOffsetFromKafkaAdminClient);
    }

    @Override
    public Result<Map<String, List<Partition>>> listPartitionsFromKafka(ClusterPhy clusterPhy) {
        if (clusterPhy.getRunState().equals(ClusterRunStateEnum.RUN_ZK.getRunState())) {
            return this.getPartitionsFromZKClient(clusterPhy);
        }

        return this.getPartitionsFromAdminClient(clusterPhy);
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
    public List<Partition> listPartitionFromCacheFirst(Long clusterPhyId, String topicName) {
        String clusterPhyIdAndTopicKey = MsgConstant.getClusterTopicKey(clusterPhyId, topicName);
        List<Partition> partitionList = partitionsCache.getIfPresent(clusterPhyIdAndTopicKey);

        if (!ValidateUtils.isNull(partitionList)) {
            return partitionList;
        }

        partitionList = this.listPartitionByTopic(clusterPhyId, topicName);
        partitionsCache.put(clusterPhyIdAndTopicKey, partitionList);
        return partitionList;
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
    public List<Partition> listPartitionByBroker(Long clusterPhyId, Integer brokerId) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);

        List<Partition> partitionList = this.convert2PartitionList(partitionDAO.selectList(lambdaQueryWrapper));

        return partitionList.stream().filter(elem -> elem.getAssignReplicaList().contains(brokerId)).collect(Collectors.toList());
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
    public Integer getPartitionSizeByClusterId(Long clusterPhyId) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);

        return partitionDAO.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Integer getLeaderPartitionSizeByClusterId(Long clusterPhyId) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.ne(PartitionPO::getLeaderBrokerId, -1);

        return partitionDAO.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Integer getNoLeaderPartitionSizeByClusterId(Long clusterPhyId) {
        LambdaQueryWrapper<PartitionPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PartitionPO::getClusterPhyId, clusterPhyId);
        lambdaQueryWrapper.eq(PartitionPO::getLeaderBrokerId, -1);

        return partitionDAO.selectCount(lambdaQueryWrapper);
    }

    @Override
    public Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, String topicName, OffsetSpec offsetSpec, Long timestamp) {
        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = new HashMap<>();
        this.listPartitionByTopic(clusterPhyId, topicName)
                .stream()
                .forEach(elem -> topicPartitionOffsets.put(new TopicPartition(topicName, elem.getPartitionId()), offsetSpec));

        try {
            return (Result<Map<TopicPartition, Long>>) doVCHandler(clusterPhyId, PARTITION_OFFSET_GET, new PartitionOffsetParam(clusterPhyId, topicPartitionOffsets, timestamp));
        } catch (VCHandlerNotExistException e) {
            return Result.buildFailure(VC_HANDLE_NOT_EXIST);
        }
    }

    @Override
    public Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, String topicName, Integer partitionId, OffsetSpec offsetSpec, Long timestamp) {
        if (partitionId == null) {
            return this.getPartitionOffsetFromKafka(clusterPhyId, topicName, offsetSpec, timestamp);
        }

        Map<TopicPartition, OffsetSpec> topicPartitionOffsets = new HashMap<>();
        this.listPartitionByTopic(clusterPhyId, topicName)
                .stream()
                .filter(elem -> elem.getPartitionId().equals(partitionId))
                .forEach(elem -> topicPartitionOffsets.put(new TopicPartition(topicName, elem.getPartitionId()), offsetSpec));

        try {
            return (Result<Map<TopicPartition, Long>>) doVCHandler(clusterPhyId, PARTITION_OFFSET_GET, new PartitionOffsetParam(clusterPhyId, topicPartitionOffsets, timestamp));
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


    private Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafkaAdminClient(VersionItemParam itemParam) {
        PartitionOffsetParam offsetParam = (PartitionOffsetParam) itemParam;
        try {
            AdminClient adminClient = kafkaAdminClient.getClient(offsetParam.getClusterPhyId());

            ListOffsetsResult listOffsetsResult = adminClient.listOffsets(offsetParam.getTopicPartitionOffsets(), new ListOffsetsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));

            Map<TopicPartition, Long> offsetMap = new HashMap<>();
            listOffsetsResult.all().get().entrySet().stream().forEach(elem -> offsetMap.put(elem.getKey(), elem.getValue().offset()));

            return Result.buildSuc(offsetMap);
        } catch (NotExistException nee) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(offsetParam.getClusterPhyId()));
        } catch (Exception e) {
            log.error("method=getPartitionOffsetFromKafkaAdminClient||clusterPhyId={}||errMsg=exception!", offsetParam.getClusterPhyId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafkaConsumerClient(VersionItemParam itemParam) {
        KafkaConsumer<String, String> kafkaConsumer = null;

        PartitionOffsetParam offsetParam = (PartitionOffsetParam) itemParam;
        try {
            if (ValidateUtils.isEmptyMap(offsetParam.getTopicPartitionOffsets())) {
                return Result.buildSuc(new HashMap<>());
            }

            kafkaConsumer = kafkaConsumerClient.getClient(offsetParam.getClusterPhyId());

            OffsetSpec offsetSpec = new ArrayList<>(offsetParam.getTopicPartitionOffsets().values()).get(0);
            if (offsetSpec instanceof OffsetSpec.LatestSpec) {
                return Result.buildSuc(
                        kafkaConsumer.endOffsets(
                                offsetParam.getTopicPartitionOffsets().keySet(),
                                Duration.ofMillis(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
                        )
                );
            }

            if (offsetSpec instanceof OffsetSpec.EarliestSpec) {
                return Result.buildSuc(
                        kafkaConsumer.beginningOffsets(
                                offsetParam.getTopicPartitionOffsets().keySet(),
                                Duration.ofMillis(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
                        )
                );
            }

            if (offsetSpec instanceof OffsetSpec.TimestampSpec) {
                // 按照时间进行查找
                Map<TopicPartition, Long> timestampMap = new HashMap<>();
                offsetParam.getTopicPartitionOffsets().entrySet().stream().forEach(elem -> timestampMap.put(elem.getKey(), offsetParam.getTimestamp()));

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
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(offsetParam.getClusterPhyId()));
        } catch (Exception e) {
            log.error("method=getPartitionOffsetFromKafkaConsumerClient||clusterPhyId={}||errMsg=exception!", offsetParam.getClusterPhyId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        } finally {
            if (kafkaConsumer != null) {
                kafkaConsumerClient.returnClient(offsetParam.getClusterPhyId(), kafkaConsumer);
            }
        }
    }

    private Result<Map<String, List<Partition>>> getPartitionsFromAdminClient(ClusterPhy clusterPhy) {
        Map<String, List<Partition>> partitionMap = new HashMap<>();

        try {
            AdminClient adminClient = kafkaAdminClient.getClient(clusterPhy.getId());

            // 获取Topic列表
            ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS));
            for (String topicName: listTopicsResult.names().get()) {
                DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(
                        Arrays.asList(topicName),
                        new DescribeTopicsOptions().timeoutMs(KafkaConstant.ADMIN_CLIENT_REQUEST_TIME_OUT_UNIT_MS)
                );

                TopicDescription description = describeTopicsResult.all().get().get(topicName);

                partitionMap.put(topicName, PartitionConverter.convert2PartitionList(clusterPhy.getId(), description));
            }

            return Result.buildSuc(partitionMap);
        } catch (Exception e) {
            log.error("class=PartitionServiceImpl||method=getPartitionsFromAdminClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private Result<Map<String, List<Partition>>> getPartitionsFromZKClient(ClusterPhy clusterPhy) {
        Map<String, List<Partition>> partitionMap = new HashMap<>();

        try {
            List<String> topicNameList = kafkaZKDAO.getChildren(clusterPhy.getId(), TopicsZNode.path(), false);
            for (String topicName: topicNameList) {
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

                partitionMap.put(topicName, partitionList);
            }

            return Result.buildSuc(partitionMap);
        } catch (Exception e) {
            log.error("class=PartitionServiceImpl||method=getPartitionsFromZKClient||clusterPhyId={}||errMsg=exception", clusterPhy.getId(), e);

            return Result.buildFromRSAndMsg(ResultStatus.KAFKA_OPERATE_FAILED, e.getMessage());
        }
    }

    private List<Partition> convert2PartitionList(List<PartitionPO> poList) {
        if (poList == null) {
            return new ArrayList<>();
        }

        List<Partition> partitionList = new ArrayList<>();
        for (PartitionPO po: poList) {
            if(null != po){partitionList.add(convert2Partition(po));}
        }
        return partitionList;
    }

    private List<PartitionPO> convert2PartitionPOList(List<Partition> partitionList) {
        if (partitionList == null) {
            return new ArrayList<>();
        }

        List<PartitionPO> poList = new ArrayList<>();
        for (Partition partition: partitionList) {
            poList.add(this.convert2PartitionPO(partition));
        }
        return poList;
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
