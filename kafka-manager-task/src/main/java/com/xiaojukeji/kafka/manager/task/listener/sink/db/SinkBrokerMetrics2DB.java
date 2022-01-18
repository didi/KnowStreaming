package com.xiaojukeji.kafka.manager.task.listener.sink.db;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerMetricsDO;
import com.xiaojukeji.kafka.manager.common.events.metrics.BatchBrokerMetricsCollectedEvent;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.BrokerMetricsDao;
import com.xiaojukeji.kafka.manager.service.utils.MetricsConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/01/17
 */
@Component
@ConditionalOnProperty(prefix = "task.metrics.sink.broker-metrics", name = "sink-db-enabled", havingValue = "true", matchIfMissing = true)
public class SinkBrokerMetrics2DB implements ApplicationListener<BatchBrokerMetricsCollectedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(SinkBrokerMetrics2DB.class);

    @Autowired
    private BrokerMetricsDao metricsDao;

    @Override
    public void onApplicationEvent(BatchBrokerMetricsCollectedEvent event) {
        logger.debug("sink broker-metrics to db start, event:{}.", event);

        List<BrokerMetrics> metricsList = event.getMetricsList();
        if (ValidateUtils.isEmptyList(metricsList)) {
            logger.warn("sink broker-metrics to db finished, without need sink, event:{}.", event);
            return;
        }

        List<BrokerMetricsDO> doList = MetricsConvertUtils.convertAndUpdateCreateTime2BrokerMetricsDOList(event.getCollectTime(), metricsList);
        int i = 0;
        while (i < doList.size()) {
            List<BrokerMetricsDO> subDOList = doList.subList(i, Math.min(i + Constant.BATCH_INSERT_SIZE, doList.size()));
            if (ValidateUtils.isEmptyList(subDOList)) {
                break;
            }

            metricsDao.batchAdd(subDOList);
            i += Constant.BATCH_INSERT_SIZE;
        }

        logger.debug("sink broker-metrics to db finished, event:{}.", event);
    }
}