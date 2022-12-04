package com.xiaojukeji.know.streaming.km.core.flusher;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.ClusterMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.utils.FutureUtil;
import com.xiaojukeji.know.streaming.km.core.cache.DataBaseDataLocalCache;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterMetricService;
import com.xiaojukeji.know.streaming.km.core.service.cluster.ClusterPhyService;
import com.xiaojukeji.know.streaming.km.core.service.partition.PartitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DatabaseDataFlusher {
    private static final ILog LOGGER  = LogFactory.getLog(DatabaseDataFlusher.class);

    @Autowired
    private ClusterPhyService clusterPhyService;

    @Autowired
    private ClusterMetricService clusterMetricService;

    @Autowired
    private PartitionService partitionService;

    @PostConstruct
    public void init() {
        this.flushPartitionsCache();

        this.flushClusterLatestMetricsCache();
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
}
