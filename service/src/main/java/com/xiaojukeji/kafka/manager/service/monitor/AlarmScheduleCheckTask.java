package com.xiaojukeji.kafka.manager.service.monitor;

import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorMatchStatus;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmRuleDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.service.cache.KafkaMetricsCache;
import com.xiaojukeji.kafka.manager.service.monitor.impl.BrokerMonitorMatchServiceImpl;
import com.xiaojukeji.kafka.manager.service.monitor.impl.ConsumerGroupMonitorMatchServiceImpl;
import com.xiaojukeji.kafka.manager.service.monitor.impl.TopicMonitorMatchServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 19/12/25
 */
@Component
@ConditionalOnProperty(prefix = "kafka-monitor", name = "enabled", havingValue = "true")
public class AlarmScheduleCheckTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmScheduleCheckTask.class);

    @Autowired
    private TopicMonitorMatchServiceImpl topicMonitorMatchService;

    @Autowired
    private BrokerMonitorMatchServiceImpl brokerMonitorMatchService;

    @Autowired
    private ConsumerGroupMonitorMatchServiceImpl consumerGroupMonitorMatchService;

    @Autowired
    private AlarmNotifyService alarmNotifyService;

    @Scheduled(cron="0 0/1 * * * ?")
    public void checkIfMatch() {LOGGER.info("alarm check, start.");
        long startTime = System.currentTimeMillis();

        Map<Long, AlarmRuleDTO> alarmRuleDTOMap = AlarmRuleManager.getActiveAlarmRuleMap();
        for (Long alarmRuleId: alarmRuleDTOMap.keySet()) {
            AlarmRuleDTO alarmRuleDTO = alarmRuleDTOMap.get(alarmRuleId);
            if (alarmRuleDTO == null) {
                continue;
            }
            MonitorMatchStatus status = checkIfMatch(alarmRuleDTO);
            if (MonitorMatchStatus.NO.equals(status)) {
                alarmRuleDTO.setDuration(0);
                continue;
            } else if (MonitorMatchStatus.UNKNOWN.equals(status)) {
                continue;
            }

            alarmRuleDTO.setDuration(alarmRuleDTO.getDuration() + 1);
            if (alarmRuleDTO.getDuration() < alarmRuleDTO.getStrategyExpression().getDuration()) {
                continue;
            }
            // 达到告警阈值
            alarmNotifyService.send(alarmRuleDTO);
            alarmRuleDTO.setDuration(0);
        }
        LOGGER.info("alarm check, finish, costTime:{}ms.", System.currentTimeMillis() - startTime);
    }

    private MonitorMatchStatus checkIfMatch(AlarmRuleDTO alarmRuleDTO) {
        if (alarmRuleDTO.getStrategyFilterMap().keySet().contains("brokerId")) {
            // Broker
            return checkIfMatchBroker(alarmRuleDTO);
        } else if (alarmRuleDTO.getStrategyFilterMap().keySet().contains("consumerGroup")) {
            // ConsumerGroup Lag
            return checkIfMatchConsumerGroup(alarmRuleDTO);
        } else if (alarmRuleDTO.getStrategyFilterMap().keySet().contains("topicName")){
            // Topic
            return checkIfMatchTopic(alarmRuleDTO);
        }
        // unknown
        return MonitorMatchStatus.UNKNOWN;
    }

    private MonitorMatchStatus checkIfMatchTopic(AlarmRuleDTO alarmRuleDTO) {
        for (Long clusterId: KafkaMetricsCache.getTopicMetricsClusterIdSet()) {
            if (!clusterId.equals(alarmRuleDTO.getClusterId())) {
                continue;
            }

            List<TopicMetrics> topicMetricsList = KafkaMetricsCache.getTopicMetricsFromCache(clusterId);
            if (topicMetricsList == null || topicMetricsList.isEmpty()) {
                continue;
            }
            return topicMonitorMatchService.validate(alarmRuleDTO, topicMetricsList);
        }
        return MonitorMatchStatus.UNKNOWN;
    }

    private MonitorMatchStatus checkIfMatchBroker(AlarmRuleDTO alarmRuleDTO) {
        for (Long clusterId: KafkaMetricsCache.getBrokerMetricsClusterIdSet()) {
            if (!clusterId.equals(alarmRuleDTO.getClusterId())) {
                continue;
            }

            List<BrokerMetrics> brokerMetricsList = KafkaMetricsCache.getBrokerMetricsFromCache(clusterId);
            if (brokerMetricsList == null || brokerMetricsList.isEmpty()) {
                continue;
            }
            return brokerMonitorMatchService.validate(alarmRuleDTO, brokerMetricsList);
        }
        return MonitorMatchStatus.UNKNOWN;
    }

    private MonitorMatchStatus checkIfMatchConsumerGroup(AlarmRuleDTO alarmRuleDTO) {
        for (Long clusterId: KafkaMetricsCache.getConsumerMetricsClusterIdSet()) {
            if (!clusterId.equals(alarmRuleDTO.getClusterId())) {
                continue;
            }

            List<ConsumerMetrics> consumerMetricsList = KafkaMetricsCache.getConsumerMetricsFromCache(clusterId);
            if (consumerMetricsList == null || consumerMetricsList.isEmpty()) {
                continue;
            }
            return consumerGroupMonitorMatchService.validate(alarmRuleDTO, consumerMetricsList);
        }
        return MonitorMatchStatus.UNKNOWN;
    }
}