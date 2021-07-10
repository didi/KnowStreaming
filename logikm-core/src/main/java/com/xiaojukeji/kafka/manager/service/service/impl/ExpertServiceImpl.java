package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.ConfigConstant;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.expert.RegionTopicHotConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.TopicAnomalyFlowConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.expert.TopicExpiredConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.config.expert.TopicInsufficientPartitionConfig;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicAnomalyFlow;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicInsufficientPartition;
import com.xiaojukeji.kafka.manager.common.entity.ao.expert.TopicRegionHot;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/3/20
 */
@Service("expertService")
public class ExpertServiceImpl implements ExpertService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExpertServiceImpl.class);

    @Autowired
    private ConfigService configService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private TopicManagerService topicManagerService;

    private static final Integer LATEST_MAX_AVG_BYTES_IN_DAY = 3;

    @Override
    public List<TopicRegionHot> getRegionHotTopics() {
        RegionTopicHotConfig config = configService.getByKey(ConfigConstant.REGION_HOT_TOPIC_CONFIG_KEY, RegionTopicHotConfig.class);
        if (ValidateUtils.isNull(config)) {
            config = new RegionTopicHotConfig();
        }

        List<TopicRegionHot> hotTopics = new ArrayList<>();
        for (ClusterDO clusterDO: clusterService.list()) {
            if (config.getIgnoreClusterIdList().contains(clusterDO.getId())) {
                continue;
            }
            hotTopics.addAll(getRegionHotTopics(clusterDO, config));
        }
        return hotTopics;
    }

    private List<TopicRegionHot> getRegionHotTopics(ClusterDO clusterDO, RegionTopicHotConfig config) {
        Map<String, Set<Integer>> topicNameRegionBrokerIdMap =
                regionService.getTopicNameRegionBrokerIdMap(clusterDO.getId());

        List<TopicRegionHot> hotTopics = new ArrayList<>();
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId())) {
            Set<Integer> regionBrokerIdSet =
                    topicNameRegionBrokerIdMap.get(topicName);
            TopicMetadata topicMetadata =
                    PhysicalClusterMetadataManager.getTopicMetadata(clusterDO.getId(), topicName);
            TopicMetrics metrics =
                    KafkaMetricsCache.getTopicMetricsFromCache(clusterDO.getId(), topicName);
            if (ValidateUtils.isNull(regionBrokerIdSet)
                    || ValidateUtils.isNull(topicMetadata)
                    || ValidateUtils.isNull(metrics)) {
                continue;
            }

            Double bytesIn = metrics.getBytesInPerSecOneMinuteRate(0.0);
            if (topicMetadata.getPartitionNum() <= 1
                    || ValidateUtils.isNull(bytesIn)
                    || bytesIn <= config.getMinTopicBytesInUnitB()) {
                continue;
            }
            TopicRegionHot hotTopic =
                    checkAndGetIfImBalanced(clusterDO, topicMetadata, regionBrokerIdSet, config);
            if (ValidateUtils.isNull(hotTopic)) {
                continue;
            }
            hotTopics.add(hotTopic);
        }
        return hotTopics;
    }

    private TopicRegionHot checkAndGetIfImBalanced(ClusterDO clusterDO,
                                                   TopicMetadata topicMetadata,
                                                   Set<Integer> regionBrokerIdSet,
                                                   RegionTopicHotConfig config) {
        Map<Integer, Integer> brokerIdPartitionNum = new HashMap<>();
        for (Integer brokerId: regionBrokerIdSet) {
            brokerIdPartitionNum.put(brokerId, 0);
        }

        for (Map.Entry<Integer, List<Integer>> entry: topicMetadata.getPartitionMap().getPartitions().entrySet()) {
            for (Integer brokerId: entry.getValue()) {
                Integer partitionNum = brokerIdPartitionNum.getOrDefault(brokerId, 0);
                brokerIdPartitionNum.put(brokerId, partitionNum + 1);
            }
        }

        Integer maxPartitionNum = Integer.MIN_VALUE;
        Integer minPartitionNum = Integer.MAX_VALUE;
        for (Integer partitionNum: brokerIdPartitionNum.values()) {
            if (maxPartitionNum < partitionNum) {
                maxPartitionNum = partitionNum;
            }
            if (minPartitionNum > partitionNum) {
                minPartitionNum = partitionNum;
            }
        }

        if (maxPartitionNum - minPartitionNum < config.getMaxDisPartitionNum()) {
            return null;
        }

        return new TopicRegionHot(
                clusterDO,
                topicMetadata.getTopic(),
                PhysicalClusterMetadataManager.getTopicRetentionTime(clusterDO.getId(), topicMetadata.getTopic()),
                brokerIdPartitionNum
        );
    }

    @Override
    public List<TopicInsufficientPartition> getPartitionInsufficientTopics() {
        TopicInsufficientPartitionConfig config = configService.getByKey(ConfigConstant.TOPIC_INSUFFICIENT_PARTITION_CONFIG_KEY, TopicInsufficientPartitionConfig.class);
        if (ValidateUtils.isNull(config)) {
            config = new TopicInsufficientPartitionConfig();
        }

        List<TopicInsufficientPartition> dataList = new ArrayList<>();
        for (ClusterDO clusterDO: clusterService.list()) {
            if (config.getIgnoreClusterIdList().contains(clusterDO.getId())) {
                continue;
            }
            dataList.addAll(getPartitionInsufficientTopics(clusterDO, config));
        }
        return dataList;
    }

    private List<TopicInsufficientPartition> getPartitionInsufficientTopics(ClusterDO clusterDO,
                                                                            TopicInsufficientPartitionConfig config) {
        Map<String, Set<Integer>> topicNameRegionBrokerIdMap =
                regionService.getTopicNameRegionBrokerIdMap(clusterDO.getId());

        Map<String, List<Double>> maxAvgBytesInMap = topicManagerService.getTopicMaxAvgBytesIn(
                clusterDO.getId(),
                -1 * LATEST_MAX_AVG_BYTES_IN_DAY,
                config.getMinTopicBytesInUnitB().doubleValue()
        );

        List<TopicInsufficientPartition> dataList = new ArrayList<>();
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId())) {
            if (!topicNameRegionBrokerIdMap.containsKey(topicName)) {
                // Topic不属于任何Region, 直接忽略
                continue;
            }

            // Topic不存在 or 流量不存在
            TopicMetadata topicMetadata =
                    PhysicalClusterMetadataManager.getTopicMetadata(clusterDO.getId(), topicName);
            TopicMetrics metrics = KafkaMetricsCache.getTopicMetricsFromCache(clusterDO.getId(), topicName);
            if (ValidateUtils.isNull(topicMetadata) || ValidateUtils.isNull(metrics)) {
                continue;
            }

            // 流量不存在 or 未达到阈值 or 分区数充足
            Double bytesIn = metrics.getBytesInPerSecOneMinuteRate(0.0);
            if (ValidateUtils.isNull(bytesIn)
                    || bytesIn <= config.getMinTopicBytesInUnitB()
                    || bytesIn / topicMetadata.getPartitionNum() < config.getMaxBytesInPerPartitionUnitB()) {
                continue;
            }
            Integer suggestedPartitionNum = (int) Math.round(
                    bytesIn / config.getMaxBytesInPerPartitionUnitB()
            );
            if (suggestedPartitionNum - topicMetadata.getPartitionNum() < 1) {
                continue;
            }

            // 分区不足的, 保存
            dataList.add(new TopicInsufficientPartition(
                    clusterDO,
                    topicName,
                    topicMetadata.getPartitionNum(),
                    suggestedPartitionNum - topicMetadata.getPartitionNum(),
                    maxAvgBytesInMap.getOrDefault(topicName, new ArrayList<>()),
                    bytesIn / topicMetadata.getPartitionNum(),
                    new ArrayList<>(topicNameRegionBrokerIdMap.get(topicName))
            ));
        }
        return dataList;
    }

    @Override
    public List<TopicAnomalyFlow> getAnomalyFlowTopics(Long timestamp) {
        TopicAnomalyFlowConfig config = new TopicAnomalyFlowConfig();

        List<TopicAnomalyFlow> anomalyFlowList = new ArrayList<>();
        for (ClusterDO clusterDO: clusterService.list()) {
            anomalyFlowList.addAll(getAnomalyFlowTopics(clusterDO, timestamp, config));
        }
        return anomalyFlowList;
    }

    private List<TopicAnomalyFlow> getAnomalyFlowTopics(ClusterDO clusterDO,
                                                        Long timestamp,
                                                        TopicAnomalyFlowConfig config) {
        List<TopicAnomalyFlow> anomalyFlowList = new ArrayList<>();
        for (String topicName: PhysicalClusterMetadataManager.getTopicNameList(clusterDO.getId())) {
            TopicMetadata topicMetadata =
                    PhysicalClusterMetadataManager.getTopicMetadata(clusterDO.getId(), topicName);
        }
        return anomalyFlowList;
    }

    @Override
    public List<TopicExpiredDO> getExpiredTopics() {
        TopicExpiredConfig config = configService.getByKey(ConfigConstant.EXPIRED_TOPIC_CONFIG_KEY, TopicExpiredConfig.class);
        if (ValidateUtils.isNull(config)) {
            config = new TopicExpiredConfig();
        }

        List<TopicExpiredDO> expiredTopicList = topicManagerService.getExpiredTopics(config.getMinExpiredDay());
        if (ValidateUtils.isEmptyList(expiredTopicList)) {
            return new ArrayList<>();
        }

        List<TopicExpiredDO> filteredExpiredTopicList = new ArrayList<>();
        for (TopicExpiredDO elem: expiredTopicList) {
            if (config.getIgnoreClusterIdList().contains(elem.getClusterId())) {
                continue;
            }
            filteredExpiredTopicList.add(elem);
        }
        return filteredExpiredTopicList;
    }
}