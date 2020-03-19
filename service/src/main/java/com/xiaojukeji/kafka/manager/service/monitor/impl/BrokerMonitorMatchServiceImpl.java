package com.xiaojukeji.kafka.manager.service.monitor.impl;

import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorMatchStatus;
import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorMetricsType;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmRuleDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyExpressionDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.service.monitor.AbstractMonitorMatchService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/18
 */
@Service
public class BrokerMonitorMatchServiceImpl extends AbstractMonitorMatchService<BrokerMetrics> {
    @Override
    public MonitorMatchStatus validate(AlarmRuleDTO alarmRuleDTO, List<BrokerMetrics> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return MonitorMatchStatus.UNKNOWN;
        }
        for (BrokerMetrics data: dataList) {
            MonitorMatchStatus status = validate(alarmRuleDTO, data);
            if (!MonitorMatchStatus.UNKNOWN.equals(status)) {
                return status;
            }
        }
        return MonitorMatchStatus.UNKNOWN;
    }

    private MonitorMatchStatus validate(AlarmRuleDTO alarmRuleDTO, BrokerMetrics data) {
        if (!data.getBrokerId().equals(Integer.valueOf(alarmRuleDTO.getStrategyFilterMap().get("brokerId")))
                || !data.getClusterId().equals(alarmRuleDTO.getClusterId())) {
            // 数值不一致
            return MonitorMatchStatus.UNKNOWN;
        }
        AlarmStrategyExpressionDTO alarmStrategyExpressionDTO = alarmRuleDTO.getStrategyExpression();
        if (MonitorMetricsType.BYTES_IN.getName().equals(alarmStrategyExpressionDTO.getMetric())) {
            return condition(
                    data.getBytesInPerSec(),
                    Double.valueOf(alarmStrategyExpressionDTO.getThreshold()),
                    alarmStrategyExpressionDTO.getOpt()
            );
        } else if (MonitorMetricsType.BYTES_OUT.getName().equals(alarmStrategyExpressionDTO.getMetric())){
            return condition(
                    data.getBytesOutPerSec(),
                    Double.valueOf(alarmStrategyExpressionDTO.getThreshold()),
                    alarmStrategyExpressionDTO.getOpt()
            );
        }
        return MonitorMatchStatus.UNKNOWN;
    }
}