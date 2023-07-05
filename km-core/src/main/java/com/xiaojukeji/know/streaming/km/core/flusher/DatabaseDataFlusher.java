package com.xiaojukeji.know.streaming.km.core.flusher;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.ha.HaActiveStandbyRelation;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.po.health.HealthCheckResultPO;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.persistence.cache.DataBaseDataLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.ha.HaActiveStandbyRelationService;
import com.xiaojukeji.know.streaming.km.core.service.health.checkresult.HealthCheckResultService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicMetricService;
import com.xiaojukeji.know.streaming.km.core.service.topic.TopicService;
import com.xiaojukeji.know.streaming.km.persistence.cache.LoadedClusterPhyCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DatabaseDataFlusher {
    private static final ILog LOGGER  = LogFactory.getLog(DatabaseDataFlusher.class);

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicMetricService topicMetricService;

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ClusterMetricService clusterMetricService;

    @Autowired
    private HealthCheckResultService healthCheckResultService;

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private HaActiveStandbyRelationService haActiveStandbyRelationService;

    @PostConstruct
    public void init() {
        this.flushPartitionsCache();

        this.flushClusterLatestMetricsCache();

        this.flushTopicLatestMetricsCache();

        this.flushHealthCheckResultCache();

        this.flushHaTopicCache();
    }

    @Scheduled(cron="0 0/1 * * * ?")
    public void flushPartitionsCache() {
        for (ClusterPhy clusterPhy: clusterPhyService.listAllClusters()) {
            FutureUtil.quickStartupFutureUtil.submitTask(() -> {
                try {
                    // 更新缓存
                    Map<String, List<Partition>> newPartitionMap = new ConcurrentHashMap<>();

                    List<Partition> partitionList = partitionService.listPartitionByCluster(clusterPhy.getId());
                    partitionList.forEach(partition -> {
                        newPartitionMap.putIfAbsent(partition.getTopicName(), new ArrayList<>());
                        newPartitionMap.get(partition.getTopicName()).add(partition);
                    });

                    DataBaseDataLocalCache.putPartitions(clusterPhy.getId(), newPartitionMap);
                } catch (Exception e) {
                    LOGGER.error("method=flushPartitionsCache||clusterPhyId={}||errMsg=exception!",  clusterPhy.getId(), e);
                }
            });
        }
    }

    @Scheduled(cron="0 0/1 * * * ?")
    public void flushHealthCheckResultCache() {
        FutureUtil.quickStartupFutureUtil.submitTask(() -> {
            List<HealthCheckResultPO> poList = healthCheckResultService.listAll();

            Map<Long, Map<String, List<HealthCheckResultPO>>> newPOMap = new ConcurrentHashMap<>();

            // 更新缓存
            poList.forEach(po -> {
                Long cacheKey = DataBaseDataLocalCache.getHealthCheckCacheKey(po.getClusterPhyId(), po.getDimension());

                newPOMap.putIfAbsent(cacheKey, new ConcurrentHashMap<>());
                newPOMap.get(cacheKey).putIfAbsent(po.getResName(), new ArrayList<>());
                newPOMap.get(cacheKey).get(po.getResName()).add(po);
            });

            for (Map.Entry<Long, Map<String, List<HealthCheckResultPO>>> entry: newPOMap.entrySet()) {
                DataBaseDataLocalCache.putHealthCheckResults(entry.getKey(), entry.getValue());
            }
        });
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    private void flushClusterLatestMetricsCache() {
        for (ClusterPhy clusterPhy: clusterPhyService.listAllClusters()) {
            FutureUtil.quickStartupFutureUtil.submitTask(() -> {
                try {
                    Result<ClusterMetrics> metricsResult = clusterMetricService.getLatestMetricsFromES(clusterPhy.getId(), Collections.emptyList());
                    if (metricsResult.hasData()) {
                        DataBaseDataLocalCache.putClusterLatestMetrics(clusterPhy.getId(), metricsResult.getData());
                        return;
                    }

                    LOGGER.error("method=flushClusterLatestMetricsCache||clusterPhyId={}||result={}||msg=failed", clusterPhy.getId(), metricsResult);
                } catch (Exception e) {
                    LOGGER.error("method=flushClusterLatestMetricsCache||clusterPhyId={}||errMsg=exception!",  clusterPhy.getId(), e);
                }

                DataBaseDataLocalCache.putClusterLatestMetrics(clusterPhy.getId(), new ClusterMetrics(clusterPhy.getId()));
            });
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    private void flushTopicLatestMetricsCache() {
        for (ClusterPhy clusterPhy: LoadedClusterPhyCache.listAll().values()) {
            FutureUtil.quickStartupFutureUtil.submitTask(() -> {
                List<String> topicNameList = topicService.listTopicsFromCacheFirst(clusterPhy.getId()).stream().map(Topic::getTopicName).collect(Collectors.toList());

                for (int i = 0; i < 3; ++i) {
                    try {
                        List<TopicMetrics> metricsList = topicMetricService.listTopicLatestMetricsFromES(
                                clusterPhy.getId(),
                                topicNameList,
                                Collections.emptyList()
                        );

                        if (!topicNameList.isEmpty() && metricsList.isEmpty()) {
                            // 没有指标时，重试
                            continue;
                        }

                        Map<String, TopicMetrics> metricsMap = metricsList
                                .stream()
                                .collect(Collectors.toMap(TopicMetrics::getTopic, Function.identity()));

                        DataBaseDataLocalCache.putTopicMetrics(clusterPhy.getId(), metricsMap);

                        break;
                    } catch (Exception e) {
                        LOGGER.error("method=flushTopicLatestMetricsCache||clusterPhyId={}||errMsg=exception!",  clusterPhy.getId(), e);
                    }
                }
            });
        }
    }

    @Scheduled(cron="0 0/1 * * * ?")
    public void flushHaTopicCache() {
        List<HaActiveStandbyRelation> haTopicList = haActiveStandbyRelationService.listAllTopicHa();
        for (HaActiveStandbyRelation topic : haTopicList) {
            DataBaseDataLocalCache.putHaTopic(topic.getStandbyClusterPhyId(), topic.getResName());
        }
    }
}
