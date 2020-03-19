package com.xiaojukeji.kafka.manager.service.monitor.impl;

import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorMatchStatus;
import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorMetricsType;
import com.xiaojukeji.kafka.manager.common.entity.ConsumerMetrics;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmRuleDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyExpressionDTO;
import com.xiaojukeji.kafka.manager.service.monitor.AbstractMonitorMatchService;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/18
 */
@Service
public class ConsumerGroupMonitorMatchServiceImpl extends AbstractMonitorMatchService<ConsumerMetrics> {
    @Override
    public MonitorMatchStatus validate(AlarmRuleDTO alarmRuleDTO, List<ConsumerMetrics> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return MonitorMatchStatus.UNKNOWN;
        }
        for (ConsumerMetrics data: dataList) {
            MonitorMatchStatus status = validate(alarmRuleDTO, data);
            if (!MonitorMatchStatus.UNKNOWN.equals(status)) {
                return status;
            }
        }
        return MonitorMatchStatus.UNKNOWN;
    }

    private MonitorMatchStatus validate(AlarmRuleDTO alarmRuleDTO, ConsumerMetrics data) {
        if (!data.getTopicName().equals(alarmRuleDTO.getStrategyFilterMap().get("topicName"))
                || !data.getConsumerGroup().equals(alarmRuleDTO.getStrategyFilterMap().get("consumerGroup"))
                || !data.getClusterId().equals(alarmRuleDTO.getClusterId())) {
            // 数值不一致
            return MonitorMatchStatus.UNKNOWN;
        }

        AlarmStrategyExpressionDTO alarmStrategyExpressionDTO = alarmRuleDTO.getStrategyExpression();
        if (MonitorMetricsType.LAG.getName().equals(alarmStrategyExpressionDTO.getMetric())) {
            return condition(
                    Double.valueOf(data.getSumLag()),
                    Double.valueOf(alarmStrategyExpressionDTO.getThreshold()),
                    alarmStrategyExpressionDTO.getOpt()
            );
        }
        return MonitorMatchStatus.UNKNOWN;
    }
}