package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.TopicOffsetChangedEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.bizenum.OffsetPosEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.KafkaMetricsCollections;
import com.xiaojukeji.kafka.manager.common.constant.TopicSampleConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionAttributeDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.*;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicDataSampleDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionMap;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.dao.TopicAppMetricsDao;
import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import com.xiaojukeji.kafka.manager.dao.TopicRequestMetricsDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.service.cache.KafkaClientPool;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.LogicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.service.strategy.AbstractHealthScoreStrategy;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author limeng
 * @date 2018/5/4.
 */
@Service("topicService")
public class TopicServiceImpl implements TopicService {
    private final static Logger LOGGER = LoggerFactory.getLogger(TopicService.class);

    @Autowired
    private TopicMetricsDao topicMetricsDao;

    @Autowired
    private TopicAppMetricsDao topicAppMetricsDao;

    @Autowired
    private ThrottleService topicThrottleService;

    @Autowired
    private JmxService jmxService;

    @Autowired
    private TopicRequestMetricsDao topicRequestMetricsDao;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private AppService appService;

    @Autowired
    private LogicalClusterMetadataManager logicalClusterMetadataManager;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private AbstractHealthScoreStrategy healthScoreStrategy;

    @Override
    public List<TopicMetricsDO> getTopicMetricsFromDB(Long clusterId, String topicName, Date startTime, Date endTime) {
        try {
            return topicMetricsDao.getTopicMetrics(clusterId, topicName, startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("select topic metrics failed, clusterId:{} topicName:{}.", clusterId, topicName, e);
        }
        return null;
    }

    @Override
    public List<TopicMetricsDTO> getTopicMetricsFromDB(String appId,
                                                       Long clusterId,
                                                       String topicName,
                                                       Date startTime,
                                                       Date endTime) {
        List<TopicMetricsDO> topicMetricsDOList = this.getTopicMetricsFromDB(clusterId, topicName, startTime, endTime);
        if (ValidateUtils.isNull(topicMetricsDOList)) {
            topicMetricsDOList = new ArrayList<>();
        }
        List<TopicMetrics> topicMetricsList = MetricsConvertUtils.convert2TopicMetricsList(topicMetricsDOList);

        // topic限流
        List<TopicThrottledMetricsDO> topicThrottleDOList =
                topicThrottleService.getTopicThrottleFromDB(clusterId, topicName, appId, startTime, endTime);
        if (ValidateUtils.isNull(topicThrottleDOList)) {
            topicThrottleDOList = new ArrayList<>();
        }

        // appId+topic维度流量信息
        List<TopicMetricsDO> topicAppIdMetricsDOList =
                topicAppMetricsDao.getTopicAppMetrics(clusterId, topicName, appId, startTime, endTime);
        if (ValidateUtils.isNull(topicAppIdMetricsDOList)) {
            topicAppIdMetricsDOList = new ArrayList<>();
        }
        List<TopicMetrics> topicAppIdMetricsList =
                MetricsConvertUtils.convert2TopicMetricsList(topicAppIdMetricsDOList);

        Map<Long, TopicMetricsDTO> dtoMap = new TreeMap<>();
        for (TopicMetrics metrics: topicMetricsList) {
            Long timestamp = metrics.getSpecifiedMetrics(JmxConstant.CREATE_TIME, Long.class);
            if (ValidateUtils.isNull(timestamp)) {
                continue;
            }
            timestamp = timestamp / 1000 / 60;
            TopicMetricsDTO dto = dtoMap.getOrDefault(timestamp, new TopicMetricsDTO());
            dto.setBytesInPerSec(metrics.getSpecifiedMetrics("BytesInPerSecOneMinuteRate"));
            dto.setBytesOutPerSec(metrics.getSpecifiedMetrics("BytesOutPerSecOneMinuteRate"));
            dto.setBytesRejectedPerSec(metrics.getSpecifiedMetrics("BytesRejectedPerSecOneMinuteRate"));
            dto.setMessagesInPerSec(metrics.getSpecifiedMetrics("MessagesInPerSecOneMinuteRate"));
            dto.setTotalProduceRequestsPerSec(metrics.getSpecifiedMetrics("TotalProduceRequestsPerSecOneMinuteRate"));
            dto.setGmtCreate(timestamp * 1000 * 60);
            dtoMap.put(timestamp, dto);
        }
        for (TopicThrottledMetricsDO data: topicThrottleDOList) {
            Long timestamp = (data.getGmtCreate().getTime() / 1000 / 60);
            TopicMetricsDTO dto = dtoMap.getOrDefault(timestamp, new TopicMetricsDTO());
            dto.setConsumeThrottled(data.getFetchThrottled() > 0);
            dto.setProduceThrottled(data.getProduceThrottled() > 0);
            dto.setGmtCreate(timestamp * 1000 * 60);
            dtoMap.put(timestamp, dto);
        }
        for (TopicMetrics metrics: topicAppIdMetricsList) {
            Long timestamp = metrics.getSpecifiedMetrics(JmxConstant.CREATE_TIME, Long.class);
            if (ValidateUtils.isNull(timestamp)) {
                continue;
            }
            timestamp = timestamp / 1000 / 60;
            TopicMetricsDTO dto = dtoMap.getOrDefault(timestamp, new TopicMetricsDTO());
            dto.setAppIdBytesInPerSec(metrics.getSpecifiedMetrics("TopicAppIdBytesInPerSecOneMinuteRate"));
            dto.setAppIdBytesOutPerSec(metrics.getSpecifiedMetrics("TopicAppIdBytesOutPerSecOneMinuteRate"));
            dto.setAppIdMessagesInPerSec(metrics.getSpecifiedMetrics("TopicAppIdMessagesInPerSecOneMinuteRate"));
            dto.setGmtCreate(timestamp * 1000 * 60);
            dtoMap.put(timestamp, dto);
        }
        return new ArrayList<>(dtoMap.values());
    }


    @Override
    public Double getMaxAvgBytesInFromDB(Long clusterId, String topicName, Date startTime, Date endTime) {
        List<TopicMetricsDO> doList = getTopicMetricsFromDB(clusterId, topicName, startTime, endTime);
        if (ValidateUtils.isEmptyList(doList)) {
            return null;
        }
        List<TopicMetrics> metricsList = MetricsConvertUtils.convert2TopicMetricsList(doList);

        Double bytesInSum = 0.0, bytesInSumTemp = 0.0;
        for (int idx = 0; idx < metricsList.size(); ++idx) {
            Double bytesIn = metricsList.get(idx).getBytesInPerSecOneMinuteRate(0.0);
            bytesInSumTemp += bytesIn;
            if (idx >= Constant.MAX_AVG_BYTES_DURATION) {
                bytesInSumTemp -= (metricsList.get(idx - Constant.MAX_AVG_BYTES_DURATION)).getBytesInPerSecOneMinuteRate(0.0);
            }
            if (bytesInSumTemp > bytesInSum) {
                bytesInSum = bytesInSumTemp;
            }
        }
        return bytesInSum / Math.min(Constant.MAX_AVG_BYTES_DURATION, doList.size());
    }

    @Override
    public Map<String, List<Integer>> getTopicPartitionIdMap(Long clusterId, Integer brokerId) {
        Map<String, List<Integer>> result = new HashMap<>();
        for (String topicName : PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (topicMetadata == null) {
                continue;
            }

            Set<Integer> brokerIdSet = topicMetadata.getBrokerIdSet();
            if (brokerIdSet == null || !brokerIdSet.contains(brokerId)) {
                continue;
            }
            PartitionMap partitionMap = topicMetadata.getPartitionMap();
            result.put(topicName, new ArrayList<>(partitionMap.getPartitions().keySet()));
        }
        return result;
    }

    @Override
    public TopicBasicDTO getTopicBasicDTO(Long clusterId, String topicName) {
        TopicBasicDTO basicDTO = new TopicBasicDTO();
        basicDTO.setClusterId(clusterId);
        basicDTO.setTopicName(topicName);

        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (topicMetadata == null) {
            return basicDTO;
        }
        basicDTO.setBrokerNum(topicMetadata.getBrokerIdSet().size());
        basicDTO.setReplicaNum(topicMetadata.getReplicaNum());
        basicDTO.setPartitionNum(topicMetadata.getPartitionNum());
        basicDTO.setCreateTime(topicMetadata.getCreateTime());
        basicDTO.setModifyTime(topicMetadata.getModifyTime());
        basicDTO.setRetentionTime(PhysicalClusterMetadataManager.getTopicRetentionTime(clusterId, topicName));

        TopicDO topicDO = topicManagerService.getByTopicName(clusterId, topicName);
        if (!ValidateUtils.isNull(topicDO)) {
            basicDTO.setDescription(topicDO.getDescription());
        }
        AppDO appDO = ValidateUtils.isNull(topicDO)? null: appService.getByAppId(topicDO.getAppId());
        if (!ValidateUtils.isNull(appDO)) {
            basicDTO.setAppId(appDO.getAppId());
            basicDTO.setAppName(appDO.getName());
            basicDTO.setPrincipals(appDO.getPrincipals());
        }

        List<RegionDO> regionDOList = regionService.getRegionListByTopicName(clusterId, topicName);
        basicDTO.setRegionNameList(regionDOList.stream().map(RegionDO::getName).collect(Collectors.toList()));

        basicDTO.setTopicCodeC(jmxService.getTopicCodeCValue(clusterId, topicName));
        basicDTO.setScore(healthScoreStrategy.calTopicHealthScore(clusterId, topicName));
        return basicDTO;
    }

    @Override
    public List<TopicPartitionDTO> getTopicPartitionDTO(ClusterDO clusterDO, String topicName, Boolean needDetail) {
        if (ValidateUtils.isNull(clusterDO) || ValidateUtils.isNull(topicName)) {
            return null;
        }
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterDO.getId(), topicName);
        if (ValidateUtils.isNull(topicMetadata)) {
            return null;
        }

        List<PartitionState> partitionStateList = KafkaZookeeperUtils.getTopicPartitionState(
                PhysicalClusterMetadataManager.getZKConfig(clusterDO.getId()),
                topicName,
                new ArrayList<>(topicMetadata.getPartitionMap().getPartitions().keySet())
        );

        Map<Integer, PartitionAttributeDTO> partitionMap = new HashMap<>();
        try {

            partitionMap = jmxService.getPartitionAttribute(clusterDO.getId(), topicName, partitionStateList);
        } catch (Exception e) {
            LOGGER.error("get topic partition failed, clusterDO:{}, topicName:{}.", clusterDO, topicName, e);
        }
        List<TopicPartitionDTO> dtoList = convert2TopicPartitionDTOList(
                topicMetadata,
                partitionStateList,
                partitionMap);
        if (!needDetail) {
            return dtoList;
        }

        Map<TopicPartition, Long> endOffsetMap = getPartitionOffset(clusterDO, topicName, OffsetPosEnum.END);
        Map<TopicPartition, Long> beginOffsetMap = getPartitionOffset(clusterDO, topicName, OffsetPosEnum.BEGINNING);
        for (TopicPartitionDTO dto : dtoList) {
            TopicPartition tp = new TopicPartition(topicName, dto.getPartitionId());
            dto.setEndOffset(endOffsetMap.getOrDefault(tp, 0L));
            dto.setBeginningOffset(beginOffsetMap.getOrDefault(tp, 0L));
        }
        return dtoList;
    }

    private List<TopicPartitionDTO> convert2TopicPartitionDTOList(TopicMetadata topicMetadata,
                                                                  List<PartitionState> partitionStateList,
                                                                  Map<Integer, PartitionAttributeDTO> partitionMap) {
        List<TopicPartitionDTO> dtoList = new ArrayList<>();
        for (PartitionState partitionState : partitionStateList) {
            TopicPartitionDTO topicPartitionDTO = new TopicPartitionDTO();
            topicPartitionDTO.setPartitionId(partitionState.getPartitionId());
            topicPartitionDTO.setLeaderBrokerId(partitionState.getLeader());
            topicPartitionDTO.setLeaderEpoch(partitionState.getLeaderEpoch());
            topicPartitionDTO.setReplicaBrokerIdList(
                    topicMetadata.getPartitionMap().getPartitions().get(partitionState.getPartitionId())
            );
            PartitionAttributeDTO partitionAttributeDTO =
                    partitionMap.getOrDefault(partitionState.getPartitionId(), null);
            if (!ValidateUtils.isNull(partitionAttributeDTO)) {
                topicPartitionDTO.setLogSize(partitionAttributeDTO.getLogSize());
            }
            if (topicPartitionDTO.getReplicaBrokerIdList() != null
                    && !topicPartitionDTO.getReplicaBrokerIdList().isEmpty()) {
                topicPartitionDTO.setPreferredBrokerId(topicPartitionDTO.getReplicaBrokerIdList().get(0));
            }
            topicPartitionDTO.setIsrBrokerIdList(partitionState.getIsr());
            if (topicPartitionDTO.getIsrBrokerIdList().size() < topicPartitionDTO.getReplicaBrokerIdList().size()) {
                topicPartitionDTO.setUnderReplicated(false);
            } else {
                topicPartitionDTO.setUnderReplicated(true);
            }
            dtoList.add(topicPartitionDTO);
        }
        return dtoList;
    }

    @Override
    public TopicMetrics getTopicMetricsFromJMX(Long clusterId, String topicName, Integer metricsCode, Boolean byAdd) {
        return jmxService.getTopicMetrics(clusterId, topicName, metricsCode, byAdd);
    }

    @Override
    public Map<TopicPartition, Long> getPartitionOffset(ClusterDO clusterDO,
                                                        String topicName,
                                                        OffsetPosEnum offsetPosEnum) {
        if (clusterDO == null || StringUtils.isEmpty(topicName)) {
            return new HashMap<>(0);
        }
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterDO.getId(), topicName);
        if (topicMetadata == null) {
            return new HashMap<>();
        }
        List<TopicPartition> topicPartitionList = new ArrayList<>();
        for (Integer partitionId = 0; partitionId < topicMetadata.getPartitionNum(); ++partitionId) {
            topicPartitionList.add(new TopicPartition(topicName, partitionId));
        }
        Map<TopicPartition, Long> topicPartitionLongMap = new HashMap<>();
        KafkaConsumer kafkaConsumer = null;
        try {
            kafkaConsumer = KafkaClientPool.borrowKafkaConsumerClient(clusterDO);
            if ((offsetPosEnum.getCode() & OffsetPosEnum.END.getCode()) > 0) {
                topicPartitionLongMap = kafkaConsumer.endOffsets(topicPartitionList);
            } else if ((offsetPosEnum.getCode() & OffsetPosEnum.BEGINNING.getCode()) > 0) {
                topicPartitionLongMap = kafkaConsumer.beginningOffsets(topicPartitionList);
            }
        } catch (Exception e) {
            LOGGER.error("get topic endOffsets failed, clusterId:{} topicName:{}.",
                    clusterDO.getId(), topicPartitionList.get(0).topic(), e);
        } finally {
            KafkaClientPool.returnKafkaConsumerClient(clusterDO.getId(), kafkaConsumer);
        }
        return topicPartitionLongMap;
    }

    @Override
    public List<TopicOverview> getTopicOverviewList(Long clusterId, Integer brokerId) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isNull(brokerId)) {
            return new ArrayList<>();
        }

        List<String> topicNameList = new ArrayList<>();
        for (String topicName : PhysicalClusterMetadataManager.getTopicNameList(clusterId)) {
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                continue;
            }

            if (!topicMetadata.getBrokerIdSet().contains(brokerId)) {
                continue;
            }
            topicNameList.add(topicName);
        }
        return this.getTopicOverviewList(clusterId, topicNameList);
    }

    @Override
    public List<TopicOverview> getTopicOverviewList(Long clusterId, List<String> topicNameList) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isEmptyList(topicNameList)) {
            return new ArrayList<>();
        }

        List<TopicDO> topicDOList = topicManagerService.getByClusterIdFromCache(clusterId);
        if (ValidateUtils.isNull(topicDOList)) {
            topicDOList = new ArrayList<>();
        }
        Map<String, TopicDO> topicDOMap = new HashMap<>();
        for (TopicDO topicDO : topicDOList) {
            topicDOMap.put(topicDO.getTopicName(), topicDO);
        }

        List<AppDO> appDOList = appService.listAll();
        if (ValidateUtils.isNull(appDOList)) {
            appDOList = new ArrayList<>();
        }
        Map<String, AppDO> appDOMap = new HashMap<>();
        for (AppDO appDO : appDOList) {
            appDOMap.put(appDO.getAppId(), appDO);
        }

        List<TopicOverview> dtoList = new ArrayList<>();
        for (String topicName : topicNameList) {
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                continue;
            }

            TopicDO topicDO = topicDOMap.get(topicName);
            AppDO appDO = topicDO == null ? null : appDOMap.get(topicDO.getAppId());
            TopicOverview overview = getTopicOverview(
                    clusterId,
                    logicalClusterMetadataManager.getTopicLogicalCluster(clusterId, topicName),
                    topicMetadata,
                    topicDO,
                    appDO
            );
            if (ValidateUtils.isNull(overview)) {
                continue;
            }
            dtoList.add(overview);
        }

        return dtoList;
    }

    private TopicOverview getTopicOverview(Long physicalClusterId,
                                           LogicalClusterDO logicalClusterDO,
                                           TopicMetadata topicMetadata,
                                           TopicDO topicDO,
                                           AppDO appDO) {
        TopicOverview overview = new TopicOverview();
        overview.setClusterId(physicalClusterId);
        overview.setTopicName(topicMetadata.getTopic());
        overview.setPartitionNum(topicMetadata.getPartitionNum());
        overview.setReplicaNum(topicMetadata.getReplicaNum());
        overview.setUpdateTime(topicMetadata.getModifyTime());
        overview.setRetentionTime(
                PhysicalClusterMetadataManager.getTopicRetentionTime(physicalClusterId, topicMetadata.getTopic())
        );
        if (!ValidateUtils.isNull(topicDO)) {
            overview.setAppId(topicDO.getAppId());
            overview.setDescription(topicDO.getDescription());
        }
        if (!ValidateUtils.isNull(appDO)) {
            overview.setAppName(appDO.getName());
        }
        if (!ValidateUtils.isNull(logicalClusterDO)) {
            overview.setLogicalClusterId(logicalClusterDO.getId());
        }
        TopicMetrics metrics = KafkaMetricsCache.getTopicMetricsFromCache(physicalClusterId, topicMetadata.getTopic());
        if (ValidateUtils.isNull(metrics)) {
            metrics = jmxService.getTopicMetrics(
                    physicalClusterId,
                    topicMetadata.getTopic(),
                    KafkaMetricsCollections.TOPIC_FLOW_OVERVIEW,
                    true
            );
        }
        if (ValidateUtils.isNull(metrics)) {
            return overview;
        }
        overview.setByteIn(metrics.getBytesInPerSecOneMinuteRate(null));
        overview.setByteOut(metrics.getBytesOutPerSecOneMinuteRate(null));
        overview.setProduceRequest(metrics.getTotalProduceRequestsPerSecOneMinuteRate(null));
        return overview;
    }

    @Override
    public Map<String, List<PartitionState>> getTopicPartitionState(Long clusterId, Integer filterBrokerId) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isNull(filterBrokerId)) {
            return new HashMap<>(0);
        }

        List<String> topicNameList = PhysicalClusterMetadataManager.getTopicNameList(clusterId);
        if (ValidateUtils.isEmptyList(topicNameList)) {
            return new HashMap<>(0);
        }

        Map<String, List<PartitionState>> stateMap = new HashMap<>();
        for (String topicName : topicNameList) {
            TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
            if (ValidateUtils.isNull(topicMetadata)) {
                continue;
            }
            if (!(Constant.NOT_FILTER_BROKER_ID.equals(filterBrokerId)
                    || topicMetadata.getBrokerIdSet().contains(filterBrokerId))) {
                // 按照 filterBrokerId 进行过滤
                continue;
            }
            List<PartitionState> partitionStateList = getTopicPartitionState(clusterId, topicMetadata);
            if (ValidateUtils.isEmptyList(partitionStateList)) {
                continue;
            }
            stateMap.put(topicName, partitionStateList);
        }
        return stateMap;
    }

    private List<PartitionState> getTopicPartitionState(Long clusterId, TopicMetadata topicMetadata) {
        List<PartitionState> partitionStateList = null;
        try {
            partitionStateList = KafkaZookeeperUtils.getTopicPartitionState(
                    PhysicalClusterMetadataManager.getZKConfig(clusterId),
                    topicMetadata.getTopic(),
                    new ArrayList<>(topicMetadata.getPartitionMap().getPartitions().keySet())
            );

            // 判断分区副本是否在isr
            for (PartitionState partitionState : partitionStateList) {
                if (topicMetadata.getReplicaNum() > partitionState.getIsr().size()) {
                    partitionState.setUnderReplicated(false);
                } else {
                    partitionState.setUnderReplicated(true);
                }
            }
        } catch (Exception e) {
            LOGGER.error("get partition state from zk failed, clusterId:{} topicName:{}.",
                    clusterId,
                    topicMetadata.getTopic()
            );
        }
        return partitionStateList;
    }

    @Override
    public List<PartitionOffsetDTO> getPartitionOffsetList(ClusterDO clusterDO, String topicName, Long timestamp) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterDO.getId(), topicName);
        if (topicMetadata == null) {
            return null;
        }
        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
        for (Integer partitionId : topicMetadata.getPartitionMap().getPartitions().keySet()) {
            timestampsToSearch.put(new TopicPartition(topicName, partitionId), timestamp);
        }
        if (timestampsToSearch.isEmpty()) {
            return new ArrayList<>();
        }
        KafkaConsumer kafkaConsumer = null;

        List<PartitionOffsetDTO> partitionOffsetDTOList = new ArrayList<>();
        try {
            kafkaConsumer = KafkaClientPool.borrowKafkaConsumerClient(clusterDO);
            Map<TopicPartition, OffsetAndTimestamp> offsetAndTimestampMap = kafkaConsumer.offsetsForTimes(timestampsToSearch);
            if (offsetAndTimestampMap == null) {
                return new ArrayList<>();
            }
            for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : offsetAndTimestampMap.entrySet()) {
                TopicPartition tp = entry.getKey();
                OffsetAndTimestamp offsetAndTimestamp = entry.getValue();
                partitionOffsetDTOList.add(new PartitionOffsetDTO(tp.partition(), offsetAndTimestamp.offset(), offsetAndTimestamp.timestamp()));
            }
        } catch (Exception e) {
            LOGGER.error("get offset failed, clusterId:{} topicName:{} timestamp:{}.", clusterDO.getId(), topicName, timestamp, e);
        } finally {
            KafkaClientPool.returnKafkaConsumerClient(clusterDO.getId(), kafkaConsumer);
        }
        return partitionOffsetDTOList;
    }

    @Override
    public List<String> fetchTopicData(ClusterDO clusterDO, String topicName, TopicDataSampleDTO reqObj) {
        KafkaConsumer kafkaConsumer = null;
        try {
            kafkaConsumer = createConsumerClient(clusterDO, reqObj.getMaxMsgNum() + 1);
            return fetchTopicData(kafkaConsumer, clusterDO, topicName, reqObj);
        } catch (Exception e) {
            LOGGER.error("create consumer failed, clusterDO:{} req:{}.", clusterDO, reqObj, e);
        } finally {
            if (kafkaConsumer != null) {
                kafkaConsumer.close();
            }
        }
        return null;
    }

    private List<String> fetchTopicData(KafkaConsumer kafkaConsumer, ClusterDO clusterDO, String topicName, TopicDataSampleDTO reqObj) {
        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterDO.getId(), topicName);
        List<TopicPartition> tpList = new ArrayList<>();
        for (int partitionId = 0; partitionId < topicMetadata.getPartitionNum(); ++partitionId) {
            if (!ValidateUtils.isNull(reqObj.getPartitionId()) && !reqObj.getPartitionId().equals(partitionId)) {
                continue;
            }
            tpList.add(new TopicPartition(topicName, partitionId));
        }
        if (ValidateUtils.isEmptyList(tpList)) {
            return null;
        }

        kafkaConsumer.assign(tpList);
        if (!ValidateUtils.isNull(reqObj.getOffset()) && tpList.size() >= 1) {
            // 如若指定offset则从指定offset开始
            kafkaConsumer.seek(tpList.get(0), reqObj.getOffset());
            return fetchTopicData(kafkaConsumer, reqObj.getMaxMsgNum(), reqObj.getTimeout(), reqObj.getTruncate());
        }
        // 获取各个分区最新的数据
        return fetchTopicData(kafkaConsumer, reqObj.getMaxMsgNum(), reqObj.getTimeout().longValue(), reqObj.getTruncate(), tpList);
    }

    @Override
    public List<String> fetchTopicData(KafkaConsumer kafkaConsumer,
                                        Integer maxMsgNum,
                                        Long maxWaitMs,
                                        Boolean truncated,
                                        List<TopicPartition> tpList) {
        if (maxWaitMs <= 0) {
            return Collections.emptyList();
        }
        Map<TopicPartition, Long> endOffsetMap = kafkaConsumer.endOffsets(tpList);

        Long begin = System.currentTimeMillis();
        Long remainingWaitMs = maxWaitMs;
        List<String> dataList = new ArrayList<>(maxMsgNum);
        // 遍历所有分区最新的数据
        for (Map.Entry<TopicPartition, Long> entry : endOffsetMap.entrySet()) {
            if (remainingWaitMs <= 0) {
                break;
            }
            Integer remainingMsgNum = maxMsgNum - dataList.size();
            Long startOffset = Math.max(0, entry.getValue() - remainingMsgNum);
            kafkaConsumer.seek(entry.getKey(), startOffset);
            dataList.addAll(fetchTopicDataNotRetry(kafkaConsumer, remainingMsgNum, remainingWaitMs, truncated));
            // 采样数据条数已经够了
            if (dataList.size() >= maxMsgNum) {
                break;
            }

            // 检查是否超时
            long elapsed = System.currentTimeMillis() - begin;
            if (elapsed >= maxWaitMs) {
                break;
            }
            remainingWaitMs = maxWaitMs - elapsed;
        }
        return dataList;
    }

    private List<String> fetchTopicDataNotRetry(KafkaConsumer kafkaConsumer,
                                                Integer maxMsgNum,
                                                Long maxWaitMs,
                                                Boolean truncated) {
        if (maxWaitMs <= 0) {
            return Collections.emptyList();
        }
        long begin = System.currentTimeMillis();
        long remainingWaitMs = maxWaitMs;

        List<String> dataList = new ArrayList<>();
        int currentSize = dataList.size();
        while (dataList.size() < maxMsgNum) {
            try {
                if (remainingWaitMs <= 0) {
                    break;
                }
                ConsumerRecords<String, String> records = kafkaConsumer.poll(TopicSampleConstant.POLL_TIME_OUT_UNIT_MS);
                for (ConsumerRecord record : records) {
                    String value = (String) record.value();
                    dataList.add(
                            truncated ?
                                    value.substring(0, Math.min(value.length(), TopicSampleConstant.MAX_DATA_LENGTH_UNIT_BYTE))
                                    : value
                    );
                }
                // 当前批次一条数据都没拉取到，则结束拉取
                if (dataList.size() - currentSize == 0) {
                    break;
                }
                currentSize = dataList.size();
                // 检查是否超时
                long elapsed = System.currentTimeMillis() - begin;
                if (elapsed >= maxWaitMs) {
                    break;
                }
                remainingWaitMs = maxWaitMs - elapsed;
            } catch (Exception e) {
                LOGGER.error("fetch topic data failed, TopicPartitions:{}.", kafkaConsumer.assignment(), e);
            }
        }
        return dataList.subList(0, Math.min(dataList.size(), maxMsgNum));
    }

    @Override
    public List<String> fetchTopicData(KafkaConsumer kafkaConsumer,
                                       Integer maxMsgNum,
                                       Integer timeout,
                                       Boolean truncated) {
        List<String> dataList = new ArrayList<>();

        long timestamp = System.currentTimeMillis();
        while (dataList.size() < maxMsgNum) {
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(TopicSampleConstant.POLL_TIME_OUT_UNIT_MS);
                for (ConsumerRecord record : records) {
                    String value = (String) record.value();
                    dataList.add(
                            truncated ?
                                    value.substring(0, Math.min(value.length(), TopicSampleConstant.MAX_DATA_LENGTH_UNIT_BYTE))
                                    : value
                    );
                }
                if (System.currentTimeMillis() - timestamp > timeout
                        || dataList.size() >= maxMsgNum) {
                    break;
                }
                Thread.sleep(10);
            } catch (Exception e) {
                LOGGER.error("fetch topic data failed, TopicPartitions:{}.", kafkaConsumer.assignment(), e);
            }
        }
        return dataList.subList(0, Math.min(dataList.size(), maxMsgNum));
    }

    @Override
    public List<TopicMetricsDO> getTopicRequestMetricsFromDB(Long clusterId, String topicName, Date startTime, Date endTime) {
        try {
            return topicRequestMetricsDao.selectByTime(clusterId, topicName, startTime, endTime);
        } catch (Exception e) {
            LOGGER.error("get topic request metrics failed, clusterId:{} topicName:{}.", clusterId, topicName, e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<TopicBrokerDTO> getTopicBrokerList(Long clusterId, String topicName) {
        List<TopicBrokerDTO> topicBrokerDOList = new ArrayList<>();
        // 获取每个broker下的分区Id
        Map<Integer, List<Integer>> brokerPartitionMap = new HashMap<>();
        // <partitionId, brokerList>
        Map<Integer, List<Integer>> partitionBrokerMap = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName).getPartitionMap().getPartitions();
        for (Map.Entry<Integer, List<Integer>> entry : partitionBrokerMap.entrySet()) {
            Integer partitionId = entry.getKey();
            for (Integer brokerId : entry.getValue()) {
                if (!brokerPartitionMap.containsKey(brokerId)) {
                    brokerPartitionMap.put(brokerId, new ArrayList<>());
                }
                brokerPartitionMap.get(brokerId).add(partitionId);
            }
        }

        TopicMetadata topicMetadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);

        // 获取每个broker下的leader分区的Id
        List<PartitionState> partitionStateList = KafkaZookeeperUtils.getTopicPartitionState(
                PhysicalClusterMetadataManager.getZKConfig(clusterId),
                topicName,
                new ArrayList<>(topicMetadata.getPartitionMap().getPartitions().keySet())
        );

        Map<Integer, List<Integer>> leaderPartitionIdMap = new HashMap<>();
        for (PartitionState partitionState : partitionStateList) {
            Integer leaderBrokerId = partitionState.getLeader();
            Integer partitionId = partitionState.getPartitionId();
            if (!leaderPartitionIdMap.containsKey(leaderBrokerId)) {
                leaderPartitionIdMap.put(leaderBrokerId, new ArrayList<>());
            }
            leaderPartitionIdMap.get(leaderBrokerId).add(partitionId);
        }
        // 封装TopicBrokerDO
        for (Map.Entry<Integer, List<Integer>> entry : brokerPartitionMap.entrySet()) {
            Integer brokerId = entry.getKey();
            TopicBrokerDTO topicBrokerDO = new TopicBrokerDTO();
            topicBrokerDO.setBrokerId(entry.getKey());
            topicBrokerDO.setLeaderPartitionIdList(leaderPartitionIdMap.getOrDefault(brokerId, new ArrayList<>()));
            topicBrokerDO.setPartitionIdList(entry.getValue());
            topicBrokerDO.setPartitionNum(entry.getValue().size());
            topicBrokerDOList.add(topicBrokerDO);
            BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterId, brokerId);
            if (!ValidateUtils.isNull(brokerMetadata)) {
                topicBrokerDO.setHost(brokerMetadata.getHost());
                topicBrokerDO.setAlive(true);
            }
        }
        return topicBrokerDOList;
    }

    private KafkaConsumer createConsumerClient(ClusterDO clusterDO, Integer maxPollRecords) {
        Properties properties = KafkaClientPool.createProperties(clusterDO, false);
        properties.put("enable.auto.commit", false);
        properties.put("max.poll.records", maxPollRecords);
        properties.put("request.timeout.ms", 15000);
        return new KafkaConsumer(properties);
    }

    @Override
    public Result<TopicOffsetChangedEnum> checkTopicOffsetChanged(Long physicalClusterId,
                                                                  String topicName,
                                                                  Long latestTime) {
        try {
            ClusterDO clusterDO = clusterService.getById(physicalClusterId);
            if (ValidateUtils.isNull(clusterDO)) {
                return Result.buildFrom(ResultStatus.CLUSTER_NOT_EXIST);
            }

            Map<TopicPartition, Long> endOffsetMap = this.getPartitionOffset(clusterDO, topicName, OffsetPosEnum.END);
            if (ValidateUtils.isEmptyMap(endOffsetMap)) {
                return new Result<>(TopicOffsetChangedEnum.UNKNOWN);
            }

            Long timestamp = System.currentTimeMillis() - latestTime;
            List<PartitionOffsetDTO> dtoList = this.getPartitionOffsetList(clusterDO, topicName, timestamp);
            if (ValidateUtils.isEmptyList(dtoList)) {
                return checkTopicOffsetChanged(clusterDO, topicName, endOffsetMap);
            }

            TopicOffsetChangedEnum offsetChangedEnum = TopicOffsetChangedEnum.NO;
            for (PartitionOffsetDTO dto: dtoList) {
                Long endOffset = endOffsetMap.get(new TopicPartition(topicName, dto.getPartitionId()));
                if (!ValidateUtils.isNull(endOffset) && endOffset > dto.getOffset()) {
                    // 任意分区的数据发生变化, 则表示有数据写入
                    return new Result<>(TopicOffsetChangedEnum.YES);
                }
                if (ValidateUtils.isNull(endOffset)) {
                    offsetChangedEnum = TopicOffsetChangedEnum.UNKNOWN;
                }
            }
            if (!TopicOffsetChangedEnum.NO.equals(offsetChangedEnum) || endOffsetMap.size() != dtoList.size()) {
                return new Result<>(TopicOffsetChangedEnum.UNKNOWN);
            }
            return new Result<>(TopicOffsetChangedEnum.NO);
        } catch (Exception e) {
            LOGGER.error("check topic expired failed, clusterId:{} topicName:{} latestTime:{}."
                    ,physicalClusterId, topicName, latestTime);
        }
        return new Result<>(TopicOffsetChangedEnum.UNKNOWN);
    }

    private Result<TopicOffsetChangedEnum> checkTopicOffsetChanged(ClusterDO clusterDO,
                                                                   String topicName,
                                                                   Map<TopicPartition, Long> endOffsetMap) {
        if (ValidateUtils.isNull(clusterDO)
                || ValidateUtils.isNull(topicName)
                || ValidateUtils.isEmptyMap(endOffsetMap)) {
            return new Result<>(TopicOffsetChangedEnum.UNKNOWN);
        }

        Map<TopicPartition, Long> beginningOffsetMap =
                this.getPartitionOffset(clusterDO, topicName, OffsetPosEnum.BEGINNING);
        if (ValidateUtils.isEmptyMap(beginningOffsetMap)) {
            return new Result<>(TopicOffsetChangedEnum.UNKNOWN);
        }

        TopicOffsetChangedEnum changedEnum = TopicOffsetChangedEnum.NO;
        for (Map.Entry<TopicPartition, Long> entry: endOffsetMap.entrySet()) {
            Long beginningOffset = beginningOffsetMap.get(entry.getKey());
            if (!ValidateUtils.isNull(beginningOffset) && beginningOffset < entry.getValue()) {
                return new Result<>(TopicOffsetChangedEnum.YES);
            }
            if (ValidateUtils.isNull(beginningOffset)) {
                changedEnum = TopicOffsetChangedEnum.UNKNOWN;
            }
        }

        if (TopicOffsetChangedEnum.UNKNOWN.equals(changedEnum)
                || endOffsetMap.size() != beginningOffsetMap.size()) {
            return new Result<>(TopicOffsetChangedEnum.UNKNOWN);
        }
        return new Result<>(TopicOffsetChangedEnum.NO);
    }
}
