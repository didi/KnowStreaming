package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicThrottledMetricsDO;
import com.xiaojukeji.kafka.manager.dao.TopicThrottledMetricsDao;
import com.xiaojukeji.kafka.manager.service.service.JmxService;
import com.xiaojukeji.kafka.manager.service.service.ThrottleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 限流信息
 * @author zhongyuankai
 * @date 20/4/3
 */
@Service("throttleService")
public class ThrottleServiceImpl implements ThrottleService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThrottleServiceImpl.class);

    @Autowired
    private TopicThrottledMetricsDao topicThrottleDao;

    @Autowired
    private JmxService jmxService;

    @Override
    public int insertBatch(List<TopicThrottledMetricsDO> dataList) {
        if (ValidateUtils.isEmptyList(dataList)) {
            return 0;
        }
        return topicThrottleDao.insertBatch(dataList);
    }

    @Override
    public List<TopicThrottledMetricsDO> getTopicThrottleFromDB(Long clusterId,
                                                                String topicName,
                                                                String appId,
                                                                Date startTime,
                                                                Date endTime) {
        List<TopicThrottledMetricsDO> topicThrottleDOList = new ArrayList<>();
        try {
            topicThrottleDOList =
                    topicThrottleDao.getTopicThrottle(clusterId, topicName, appId, startTime, endTime);
            if (!ValidateUtils.isNull(topicThrottleDOList)) {
                return topicThrottleDOList;
            }
        } catch (Exception e) {
            LOGGER.error("get topic throttle failed, clusterId:{} topicName:{} appId:{} startTime:{} endTime:{}.",
                    clusterId, topicName, appId, startTime, endTime, e);
        }
        return topicThrottleDOList;
    }

    @Override
    public List<TopicThrottledMetrics> getThrottledTopicsFromJmx(Long clusterId,
                                                                 Set<Integer> brokerIdSet,
                                                                 List<KafkaClientEnum> kafkaClientList) {
        if (ValidateUtils.isNull(brokerIdSet)
                || ValidateUtils.isNull(clusterId)
                || ValidateUtils.isEmptyList(kafkaClientList)) {
            return new ArrayList<>();
        }

        List<TopicThrottledMetrics> metricsList = new ArrayList<>();
        for (KafkaClientEnum kafkaClientEnum: kafkaClientList) {
            Map<String, TopicThrottledMetrics> metricsMap = new HashMap<>();
            for (Integer brokerId : brokerIdSet) {
                Set<String> throttledClients =
                        jmxService.getBrokerThrottleClients(clusterId, brokerId, kafkaClientEnum);
                for (String client: throttledClients) {
                    TopicThrottledMetrics metrics = metricsMap.get(client);
                    if (ValidateUtils.isNull(metrics)) {
                        metrics = buildFrom(clusterId, brokerId, client, kafkaClientEnum);
                    }
                    if (ValidateUtils.isNull(metrics)) {
                        continue;
                    }
                    metrics.getBrokerIdSet().add(brokerId);
                    metricsMap.put(client, metrics);
                }
            }
            metricsList.addAll(metricsMap.values());
        }

        return metricsList;
    }

    private TopicThrottledMetrics buildFrom(Long clusterId,
                                            Integer brokerId,
                                            String client,
                                            KafkaClientEnum kafkaClientEnum) {
        TopicThrottledMetrics metrics = new TopicThrottledMetrics();
        String[] splits = client.split("\\.");
        if (splits.length != 2) {
            return null;
        }
        metrics.setAppId(splits[0]);
        metrics.setClusterId(clusterId);
        metrics.setTopicName(splits[1]);
        metrics.setClientType(kafkaClientEnum);
        metrics.setBrokerIdSet(new HashSet<>());
        metrics.getBrokerIdSet().add(brokerId);
        return metrics;
    }
}
