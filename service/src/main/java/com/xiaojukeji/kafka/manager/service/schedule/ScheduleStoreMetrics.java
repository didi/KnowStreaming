package com.xiaojukeji.kafka.manager.service.schedule;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.dao.BrokerMetricsDao;
import com.xiaojukeji.kafka.manager.dao.ClusterMetricsDao;
import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Metrics信息存DB
 * @author zengqiao
 * @date 2019-05-10
 */
@Component
public class ScheduleStoreMetrics implements SchedulingConfigurer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleStoreMetrics.class);

    @Autowired
    private TopicMetricsDao topicMetricsDao;

    @Autowired
    private BrokerMetricsDao brokerMetricsDao;

    @Autowired
    private ClusterMetricsDao clusterMetricsDao;

    private static final Integer INSERT_BATCH_SIZE = 100;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setScheduler(Executors.newScheduledThreadPool(3));
    }

    @Scheduled(cron="0 0/1 * * * ?")
    public void storeTopicMetrics(){
        long startTime = System.currentTimeMillis();
        for (Long clusterId: KafkaMetricsCache.getTopicMetricsClusterIdSet()) {
            try {
                List<TopicMetrics> topicMetricsList = KafkaMetricsCache.getTopicMetricsFromCache(clusterId);
                if (topicMetricsList == null || topicMetricsList.isEmpty()) {
                    continue;
                }
                int i = 0;
                do {
                    topicMetricsDao.batchAdd(topicMetricsList.subList(i, Math.min(i + INSERT_BATCH_SIZE, topicMetricsList.size())));
                    i += INSERT_BATCH_SIZE;
                } while (i < topicMetricsList.size());
            } catch (Throwable t) {
                LOGGER.error("save topic metrics failed, clusterId:{}.", clusterId, t);
            }
        }
        LOGGER.info("save topic metrics finished, costTime:{}ms.", System.currentTimeMillis() - startTime);
    }

    @Scheduled(cron="0 0/1 * * * ?")
    public void storeBrokerMetrics(){
        long startTime = System.currentTimeMillis();
        for (Long clusterId: KafkaMetricsCache.getBrokerMetricsClusterIdSet()) {
            try {
                List<BrokerMetrics> brokerMetricsList = KafkaMetricsCache.getBrokerMetricsFromCache(clusterId);
                if (brokerMetricsList == null || brokerMetricsList.isEmpty()) {
                    continue;
                }
                int i = 0;
                do {
                    brokerMetricsDao.batchAdd(brokerMetricsList.subList(i, Math.min(i + INSERT_BATCH_SIZE, brokerMetricsList.size())));
                    i += INSERT_BATCH_SIZE;
                } while (i < brokerMetricsList.size());
            } catch (Throwable t) {
                LOGGER.error("save broker metrics failed, clusterId:{}.", clusterId, t);
            }
        }
        LOGGER.info("save broker metrics finished, costTime:{}ms.", System.currentTimeMillis() - startTime);
    }

    @Scheduled(cron="0 0/1 * * * ?")
    public void storeClusterMetrics(){
        long startTime = System.currentTimeMillis();
        for (Long clusterId: KafkaMetricsCache.getBrokerMetricsClusterIdSet()) {
            try {
                List<BrokerMetrics> brokerMetricsList = KafkaMetricsCache.getBrokerMetricsFromCache(clusterId);
                if (brokerMetricsList == null || brokerMetricsList.isEmpty()) {
                    continue;
                }
                ClusterMetricsDO clusterMetricsDO = new ClusterMetricsDO();
                clusterMetricsDO.setClusterId(clusterId);
                clusterMetricsDO.setTopicNum(0);
                clusterMetricsDO.setPartitionNum(0);
                clusterMetricsDO.setBrokerNum(0);
                for (BrokerMetrics brokerMetrics: brokerMetricsList) {
                    clusterMetricsDO.setBytesInPerSec(clusterMetricsDO.getBytesInPerSec() + brokerMetrics.getBytesInPerSec());
                    clusterMetricsDO.setBytesOutPerSec(clusterMetricsDO.getBytesOutPerSec() + brokerMetrics.getBytesOutPerSec());
                    clusterMetricsDO.setBytesRejectedPerSec(clusterMetricsDO.getBytesRejectedPerSec() + brokerMetrics.getBytesRejectedPerSec());
                    clusterMetricsDO.setMessagesInPerSec(clusterMetricsDO.getMessagesInPerSec() + brokerMetrics.getMessagesInPerSec());
                }
                List<ClusterMetricsDO> clusterMetricsDOList = new ArrayList<>();
                clusterMetricsDOList.add(clusterMetricsDO);
                clusterMetricsDao.batchAdd(clusterMetricsDOList);
            } catch (Throwable t) {
                LOGGER.error("save cluster metrics failed, clusterId:{}.", clusterId, t);
            }
        }
        LOGGER.info("save cluster metrics finished, costTime:{}ms.", System.currentTimeMillis() - startTime);
    }
}
