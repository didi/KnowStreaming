package com.xiaojukeji.know.streaming.km.biz.topic.impl;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.biz.group.GroupManager;
import com.xiaojukeji.know.streaming.km.biz.topic.TopicStateManager;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicRecordDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.broker.Broker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.offset.KSOffsetSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.po.group.GroupMemberPO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.broker.BrokerReplicaSummaryVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.group.GroupTopicOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicBrokersPartitionsSummaryVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicRecordVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.broker.TopicBrokerAllVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.broker.TopicBrokerSingleVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.partition.TopicPartitionVO;
import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import com.xiaojukeji.know.streaming.km.common.constant.KafkaConstant;
import com.xiaojukeji.know.streaming.km.common.constant.MsgConstant;
import com.xiaojukeji.know.streaming.km.common.constant.PaginationConstant;
import com.xiaojukeji.know.streaming.km.common.converter.TopicVOConverter;
import com.xiaojukeji.know.streaming.km.common.enums.OffsetTypeEnum;
import com.xiaojukeji.know.streaming.km.common.enums.SortTypeEnum;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import com.xiaojukeji.know.streaming.km.common.utils.PaginationUtil;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.core.service.broker.BrokerService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.config.KSConfigUtils;
import com.xiaojukeji.know.streaming.km.core.service.group.GroupService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionMetricService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicConfigService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.core.service.version.metrics.kafka.TopicMetricVersionItems;
import com.xiaojukeji.know.streaming.km.core.utils.ApiCallThreadPoolService;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TopicStateManagerImpl implements TopicStateManager {
    private static final ILog LOGGER = LogFactory.getLog(TopicStateManagerImpl.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private BrokerService brokerService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private PartitionMetricService partitionMetricService;

    @Autowired
    private TopicMetricService topicMetricService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private TopicConfigService topicConfigService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupManager groupManager;

    @Autowired
    private KSConfigUtils ksConfigUtils;

    @Override
    public TopicBrokerAllVO getTopicBrokerAll(Long clusterPhyId, String topicName, String searchBrokerHost) throws NotExistException {
        Topic topic = topicService.getTopic(clusterPhyId, topicName);

        List<Partition> partitionList = partitionService.listPartitionByTopic(clusterPhyId, topicName);
        Map<Integer, List<Partition>> brokerIdPartitionListMap = this.convert2BrokerIdPartitionListMap(partitionList);

        Map<Integer, Broker> brokerMap = brokerService.listAllBrokerByTopic(clusterPhyId, topicName).stream().collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));

        TopicBrokerAllVO allVO = new TopicBrokerAllVO();

        allVO.setTotal(topic.getBrokerIdSet().size());
        allVO.setLive((int)brokerMap.values().stream().filter(Broker::alive).count());
        allVO.setDead(allVO.getTotal() - allVO.getLive());

        allVO.setPartitionCount(topic.getPartitionNum());
        allVO.setBrokerPartitionStateList(new ArrayList<>());
        allVO.setUnderReplicatedPartitionIdList(new ArrayList<>());
        allVO.setNoLeaderPartitionIdList(new ArrayList<>());

        // 补充无Leader及未同步的分区
        for (Partition partition: partitionList) {
            if (partition.getLeaderBrokerId() == null || Constant.INVALID_CODE == partition.getLeaderBrokerId()) {
                allVO.getNoLeaderPartitionIdList().add(partition.getPartitionId());
            }
            if (partition.getInSyncReplicaList().size() != partition.getAssignReplicaList().size()) {
                allVO.getUnderReplicatedPartitionIdList().add(partition.getPartitionId());
            }
        }

        // 补充Broker中分区的详情
        for (Integer brokerId: topic.getBrokerIdSet()) {
            Broker broker = brokerMap.get(brokerId);
            if (!ValidateUtils.isBlank(searchBrokerHost) && (broker == null || !broker.getHost().contains(searchBrokerHost))) {
                // 不满足搜索的要求，则直接略过该Broker
                continue;
            }
            allVO.getBrokerPartitionStateList().add(this.getTopicBrokerSingle(clusterPhyId, topicName, brokerIdPartitionListMap, brokerId, broker));
        }

        return allVO;
    }

    @Override
    public Result<List<TopicRecordVO>> getTopicMessages(Long clusterPhyId, String topicName, TopicRecordDTO dto) throws AdminOperateException {
        long startTime = System.currentTimeMillis();

        // 获取集群
        ClusterPhy clusterPhy = clusterPhyService.getClusterByCluster(clusterPhyId);
        if (clusterPhy == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getClusterPhyNotExist(clusterPhyId));
        }

        // 获取分区beginOffset
        Result<Map<TopicPartition, Long>> beginOffsetsMapResult = partitionService.getPartitionOffsetFromKafka(clusterPhyId, topicName, dto.getFilterPartitionId(), KSOffsetSpec.earliest());
        if (beginOffsetsMapResult.failed()) {
            return Result.buildFromIgnoreData(beginOffsetsMapResult);
        }
        // 获取分区endOffset
        Result<Map<TopicPartition, Long>> endOffsetsMapResult = partitionService.getPartitionOffsetFromKafka(clusterPhyId, topicName, dto.getFilterPartitionId(), KSOffsetSpec.latest());
        if (endOffsetsMapResult.failed()) {
            return Result.buildFromIgnoreData(endOffsetsMapResult);
        }

        // 数据采集
        List<TopicRecordVO> voList = this.getTopicMessages(clusterPhy, topicName, beginOffsetsMapResult.getData(), endOffsetsMapResult.getData(), startTime, dto);

        // 排序
        if (ValidateUtils.isBlank(dto.getSortType())) {
            // 默认按时间倒序排序
            dto.setSortType(SortTypeEnum.DESC.getSortType());
        }
        if (ValidateUtils.isBlank(dto.getSortField())) {
            // 默认按照timestampUnitMs字段排序
            dto.setSortField(PaginationConstant.TOPIC_RECORDS_TIME_SORTED_FIELD);
        }

        if (PaginationConstant.TOPIC_RECORDS_TIME_SORTED_FIELD.equals(dto.getSortField())) {
            // 如果是时间类型，则第二排序规则是offset
            PaginationUtil.pageBySort(voList, dto.getSortField(), dto.getSortType(), PaginationConstant.TOPIC_RECORDS_OFFSET_SORTED_FIELD, dto.getSortType());
        } else {
            // 如果是非时间类型，则第二排序规则是时间
            PaginationUtil.pageBySort(voList, dto.getSortField(), dto.getSortType(), PaginationConstant.TOPIC_RECORDS_TIME_SORTED_FIELD, dto.getSortType());
        }

        return Result.buildSuc(voList.subList(0, Math.min(dto.getMaxRecords(), voList.size())));
    }

    @Override
    public Result<TopicStateVO> getTopicState(Long clusterPhyId, String topicName) {
        Topic topic = topicService.getTopic(clusterPhyId, topicName);
        if (topic == null) {
            return Result.buildFromRSAndMsg(ResultStatus.NOT_EXIST, MsgConstant.getTopicNotExist(clusterPhyId, topicName));
        }

        List<Partition> partitionList = partitionService.listPartitionByTopic(clusterPhyId, topicName);
        if (partitionList == null) {
            partitionList = new ArrayList<>();
        }

        TopicStateVO vo = new TopicStateVO();

        // 分区信息
        vo.setPartitionCount(topic.getPartitionNum());
        vo.setAllPartitionHaveLeader(partitionList.stream().filter(elem -> elem.getLeaderBrokerId().equals(-1)).count() <= 0);

        // 副本信息
        vo.setReplicaFactor(topic.getReplicaNum());
        vo.setAllReplicaInSync(partitionList.stream().filter(elem -> elem.getInSyncReplicaList().size() != topic.getReplicaNum()).count() <= 0);

        // 配置信息
        Map<String, String> topicConfigMap = new HashMap<>();
        Result<Map<String, String>> configResult = topicConfigService.getTopicConfigFromKafka(clusterPhyId, topicName);
        if (configResult.hasData()) {
            topicConfigMap = configResult.getData();
        }

        // 最小副本
        Integer minIsr = ConvertUtil.string2Integer(topicConfigMap.get(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG));
        if (minIsr == null) {
            vo.setMinimumIsr(null);
            vo.setAllPartitionMatchAtMinIsr(null);
        } else {
            vo.setMinimumIsr(minIsr);
            vo.setAllPartitionMatchAtMinIsr(partitionList.stream().filter(elem -> elem.getInSyncReplicaList().size() < minIsr).count() <= 0);
        }

        // 压缩方式
        String cleanupPolicy = topicConfigMap.get(TopicConfig.CLEANUP_POLICY_CONFIG);
        if (ValidateUtils.isBlank(cleanupPolicy)) {
            vo.setCompacted(null);
        } else {
            vo.setCompacted(cleanupPolicy.contains(TopicConfig.CLEANUP_POLICY_COMPACT));
        }

        return Result.buildSuc(vo);
    }

    @Override
    public Result<List<TopicPartitionVO>> getTopicPartitions(Long clusterPhyId, String topicName, List<String> metricsNames) {
        long startTime = System.currentTimeMillis();

        List<Partition> partitionList = partitionService.listPartitionByTopic(clusterPhyId, topicName);
        if (ValidateUtils.isEmptyList(partitionList)) {
            return Result.buildSuc();
        }

        Map<Integer, PartitionMetrics> metricsMap = new HashMap<>();
        ApiCallThreadPoolService.runnableTask(
                String.format("clusterPhyId=%d||topicName=%s||method=getTopicPartitions", clusterPhyId, topicName),
                ksConfigUtils.getApiCallLeftTimeUnitMs(System.currentTimeMillis() - startTime),
                () -> {
                    Result<List<PartitionMetrics>> metricsResult = partitionMetricService.collectPartitionsMetricsFromKafka(clusterPhyId, topicName, metricsNames);
                    if (metricsResult.failed()) {
                        // 仅打印错误日志，但是不直接返回错误
                        LOGGER.error(
                                "method=getTopicPartitions||clusterPhyId={}||topicName={}||result={}||msg=get metrics from kafka failed",
                                clusterPhyId, topicName, metricsResult
                        );
                    }

                    for (PartitionMetrics metrics: metricsResult.getData()) {
                        metricsMap.put(metrics.getPartitionId(), metrics);
                    }
                }
        );
        boolean finished = ApiCallThreadPoolService.waitResultAndReturnFinished(1);

        if (!finished && metricsMap.isEmpty()) {
            // 未完成 -> 打印日志
            LOGGER.error("method=getTopicPartitions||clusterPhyId={}||topicName={}||msg=get metrics from kafka failed", clusterPhyId, topicName);
        }

        List<TopicPartitionVO> voList = new ArrayList<>();
        for (Partition partition: partitionList) {
            voList.add(TopicVOConverter.convert2TopicPartitionVO(partition, metricsMap.get(partition.getPartitionId())));
        }
        return Result.buildSuc(voList);
    }

    @Override
    public Result<TopicBrokersPartitionsSummaryVO> getTopicBrokersPartitionsSummary(Long clusterPhyId, String topicName) {
        List<Partition> partitionList = partitionService.listPartitionByTopic(clusterPhyId, topicName);
        Map<Integer, Broker> brokerMap = brokerService.listAllBrokerByTopic(clusterPhyId, topicName).stream().collect(Collectors.toMap(Broker::getBrokerId, Function.identity()));

        TopicBrokersPartitionsSummaryVO vo = new TopicBrokersPartitionsSummaryVO();

        // Broker统计信息
        vo.setBrokerCount(brokerMap.size());
        vo.setLiveBrokerCount((int)brokerMap.values().stream().filter(Broker::alive).count());
        vo.setDeadBrokerCount(vo.getBrokerCount() - vo.getLiveBrokerCount());

        // Partition统计信息
        vo.setPartitionCount(partitionList.size());
        vo.setNoLeaderPartitionCount(0);
        vo.setUnderReplicatedPartitionCount(0);

        // 补充无Leader及未同步的分区
        for (Partition partition: partitionList) {
            if (partition.getLeaderBrokerId() == null || Constant.INVALID_CODE == partition.getLeaderBrokerId()) {
                vo.setNoLeaderPartitionCount(vo.getNoLeaderPartitionCount() + 1);
            }

            if (partition.getInSyncReplicaList().size() != partition.getAssignReplicaList().size()) {
                vo.setUnderReplicatedPartitionCount(vo.getUnderReplicatedPartitionCount() + 1);
            }
        }

        return Result.buildSuc(vo);
    }

    @Override
    public PaginationResult<GroupTopicOverviewVO> pagingTopicGroupsOverview(Long clusterPhyId, String topicName, String searchGroupName, PaginationBaseDTO dto) {
        long startTimeUnitMs = System.currentTimeMillis();

        PaginationResult<GroupMemberPO> paginationResult = groupService.pagingGroupMembers(clusterPhyId, topicName, "", "", searchGroupName, dto);

        if (!paginationResult.hasData()) {
            return PaginationResult.buildSuc(new ArrayList<>(), paginationResult);
        }

        List<GroupTopicOverviewVO> groupTopicVOList = groupManager.getGroupTopicOverviewVOList(
                clusterPhyId,
                paginationResult.getData().getBizData(),
                ksConfigUtils.getApiCallLeftTimeUnitMs(System.currentTimeMillis() - startTimeUnitMs)    // 超时时间
        );

        return PaginationResult.buildSuc(groupTopicVOList, paginationResult);
    }

    /**************************************************** private method ****************************************************/

    private boolean checkIfIgnore(ConsumerRecord<String, String> consumerRecord, String filterKey, String filterValue) {
        if (filterKey != null && consumerRecord.key() == null) {
            // ignore
            return true;
        }
        if (filterKey != null && consumerRecord.key() != null && !consumerRecord.key().contains(filterKey)) {
            return true;
        }

        if (filterValue != null && consumerRecord.value() == null) {
            // ignore
            return true;
        }

        return (filterValue != null && consumerRecord.value() != null && !consumerRecord.value().contains(filterValue));
    }

    private TopicBrokerSingleVO getTopicBrokerSingle(Long clusterPhyId,
                                                     String topicName,
                                                     Map<Integer, List<Partition>> brokerIdPartitionListMap,
                                                     Integer brokerId,
                                                     Broker broker) {
        TopicBrokerSingleVO singleVO = new TopicBrokerSingleVO();
        singleVO.setBrokerId(brokerId);
        singleVO.setHost(broker != null? broker.getHost(): null);
        singleVO.setAlive(broker != null && broker.alive());

        TopicMetrics metrics = topicMetricService.getTopicLatestMetricsFromES(clusterPhyId, brokerId, topicName, Arrays.asList(
                TopicMetricVersionItems.TOPIC_METRIC_BYTES_IN,
                TopicMetricVersionItems.TOPIC_METRIC_BYTES_OUT
        ));
        if (metrics != null) {
            singleVO.setBytesInOneMinuteRate(metrics.getMetrics().get(TopicMetricVersionItems.TOPIC_METRIC_BYTES_IN));
            singleVO.setBytesOutOneMinuteRate(metrics.getMetrics().get(TopicMetricVersionItems.TOPIC_METRIC_BYTES_OUT));
        }
        singleVO.setReplicaList(this.getBrokerReplicaSummaries(brokerId, brokerIdPartitionListMap.getOrDefault(brokerId, new ArrayList<>())));
        return singleVO;
    }

    private List<BrokerReplicaSummaryVO> getBrokerReplicaSummaries(Integer brokerId, List<Partition> partitionList) {
        List<BrokerReplicaSummaryVO> voList = new ArrayList<>();
        for (Partition partition: partitionList) {
            BrokerReplicaSummaryVO summaryVO = new BrokerReplicaSummaryVO();
            summaryVO.setTopicName(partition.getTopicName());
            summaryVO.setPartitionId(partition.getPartitionId());
            summaryVO.setLeaderBrokerId(partition.getLeaderBrokerId());
            summaryVO.setIsLeaderReplace(brokerId.equals(partition.getLeaderBrokerId()));
            summaryVO.setInSync(partition.getInSyncReplicaList().contains(brokerId));
            voList.add(summaryVO);
        }
        return voList;
    }

    private Map<Integer, List<Partition>> convert2BrokerIdPartitionListMap(List<Partition> partitionList) {
        Map<Integer, List<Partition>> brokerIdPartitionListMap = new HashMap<>();
        for (Partition partition: partitionList) {
            for (Integer brokerId: partition.getAssignReplicaList()) {
                brokerIdPartitionListMap.putIfAbsent(brokerId, new ArrayList<>());
                brokerIdPartitionListMap.get(brokerId).add(partition);
            }
        }
        return brokerIdPartitionListMap;
    }

    private Properties generateClientProperties(ClusterPhy clusterPhy, Integer maxPollRecords) {
        Properties props = ConvertUtil.str2ObjByJson(clusterPhy.getClientProperties(), Properties.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, clusterPhy.getBootstrapServers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, Math.max(2, Math.min(5, maxPollRecords)));
        return props;
    }

    private List<TopicRecordVO> getTopicMessages(ClusterPhy clusterPhy,
                                                 String topicName,
                                                 Map<TopicPartition, Long> beginOffsetsMap,
                                                 Map<TopicPartition, Long> endOffsetsMap,
                                                 long startTime,
                                                 TopicRecordDTO dto) throws AdminOperateException {
        List<TopicRecordVO> voList = new ArrayList<>();

        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(this.generateClientProperties(clusterPhy, dto.getMaxRecords()))) {
            // 移动到指定位置
            long maxMessage = this.assignAndSeekToSpecifiedOffset(kafkaConsumer, beginOffsetsMap, endOffsetsMap, dto);

            // 这里需要减去 KafkaConstant.POLL_ONCE_TIMEOUT_UNIT_MS 是因为poll一次需要耗时，如果这里不减去，则可能会导致poll之后，超过要求的时间
            while (System.currentTimeMillis() - startTime <= dto.getPullTimeoutUnitMs() && voList.size() < maxMessage) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(KafkaConstant.POLL_ONCE_TIMEOUT_UNIT_MS));
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    if (this.checkIfIgnore(consumerRecord, dto.getFilterKey(), dto.getFilterValue())) {
                        continue;
                    }

                    voList.add(TopicVOConverter.convert2TopicRecordVO(topicName, consumerRecord));
                    if (voList.size() >= dto.getMaxRecords()) {
                        break;
                    }
                }

                // 超时则返回
                if (System.currentTimeMillis() - startTime + KafkaConstant.POLL_ONCE_TIMEOUT_UNIT_MS > dto.getPullTimeoutUnitMs()
                        || voList.size() > dto.getMaxRecords()) {
                    break;
                }
            }

            return voList;
        } catch (Exception e) {
            LOGGER.error("method=getTopicMessages||clusterPhyId={}||topicName={}||param={}||errMsg=exception", clusterPhy.getId(), topicName, dto, e);

            throw new AdminOperateException(e.getMessage(), e, ResultStatus.KAFKA_OPERATE_FAILED);
        }
    }

    private long assignAndSeekToSpecifiedOffset(KafkaConsumer<String, String> kafkaConsumer,
                                                Map<TopicPartition, Long> beginOffsetsMap,
                                                Map<TopicPartition, Long> endOffsetsMap,
                                                TopicRecordDTO dto) {
        List<TopicPartition> partitionList = new ArrayList<>();
        long maxMessage = 0;
        for (Map.Entry<TopicPartition, Long> entry : endOffsetsMap.entrySet()) {
            long begin = beginOffsetsMap.get(entry.getKey());
            long end = entry.getValue();
            if (begin == end){
                continue;
            }
            maxMessage += end - begin;
            partitionList.add(entry.getKey());
        }
        maxMessage = Math.min(maxMessage, dto.getMaxRecords());
        kafkaConsumer.assign(partitionList);

        Map<TopicPartition, OffsetAndTimestamp> partitionOffsetAndTimestampMap = new HashMap<>();
        // 获取指定时间每个分区的offset（按指定开始时间查询消息时）
        if (OffsetTypeEnum.PRECISE_TIMESTAMP.getResetType() == dto.getFilterOffsetReset()) {
            Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
            partitionList.forEach(topicPartition -> timestampsToSearch.put(topicPartition, dto.getStartTimestampUnitMs()));
            partitionOffsetAndTimestampMap = kafkaConsumer.offsetsForTimes(timestampsToSearch);
        }

        for (TopicPartition partition : partitionList) {
            if (OffsetTypeEnum.EARLIEST.getResetType() == dto.getFilterOffsetReset()) {
                // 重置到最旧
                kafkaConsumer.seek(partition, beginOffsetsMap.get(partition));
            } else if (OffsetTypeEnum.PRECISE_TIMESTAMP.getResetType() == dto.getFilterOffsetReset()) {
                // 重置到指定时间
                kafkaConsumer.seek(partition, partitionOffsetAndTimestampMap.get(partition).offset());
            } else if (OffsetTypeEnum.PRECISE_OFFSET.getResetType() == dto.getFilterOffsetReset()) {
                // 重置到指定位置

            } else {
                // 默认，重置到最新
                kafkaConsumer.seek(partition, Math.max(beginOffsetsMap.get(partition), endOffsetsMap.get(partition) - dto.getMaxRecords()));
            }
        }

        return maxMessage;
    }
}
