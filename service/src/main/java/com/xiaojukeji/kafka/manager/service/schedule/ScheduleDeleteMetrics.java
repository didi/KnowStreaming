package com.xiaojukeji.kafka.manager.service.schedule;

import com.xiaojukeji.kafka.manager.dao.BrokerMetricsDao;
import com.xiaojukeji.kafka.manager.dao.ClusterMetricsDao;
import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 定期删除Metrics信息
 * @author zengqiao
 * @date 20/1/8
 */
@Component
@ConditionalOnProperty(prefix = "cluster-metrics", name = "enabled", havingValue = "true")
public class ScheduleDeleteMetrics {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScheduleDeleteMetrics.class);

    @Autowired
    private TopicMetricsDao topicMetricsDao;

    @Autowired
    private BrokerMetricsDao brokerMetricsDao;

    @Autowired
    private ClusterMetricsDao clusterMetricsDao;

    @Scheduled(cron="0/7 * * * * ?")
    public void deleteDBMetrics(){
        return;
//        long startTime = System.currentTimeMillis();
//        LOGGER.info("start delete metrics");
//        try {
//            deleteTopicMetrics();
//        } catch (Exception e) {
//            LOGGER.error("delete topic metrics failed.", e);
//        }
//        try {
//            deleteBrokerMetrics();
//        } catch (Exception e) {
//            LOGGER.error("delete broker metrics failed.", e);
//        }
//        try {
//            deleteClusterMetrics();
//        } catch (Exception e) {
//            LOGGER.error("delete cluster metrics failed.", e);
//        }
//        LOGGER.info("finish delete metrics, costTime:{}ms.", System.currentTimeMillis() - startTime);
    }

    private void deleteTopicMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000);
        topicMetricsDao.deleteBeforeTime(endTime);
    }

    private void deleteBrokerMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000);
        brokerMetricsDao.deleteBeforeTime(endTime);
    }

    private void deleteClusterMetrics() {
        Date endTime = new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000);
        clusterMetricsDao.deleteBeforeTime(endTime);
    }
}