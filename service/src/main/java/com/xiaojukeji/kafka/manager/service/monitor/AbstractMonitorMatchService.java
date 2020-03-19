package com.xiaojukeji.kafka.manager.service.monitor;

import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorMatchStatus;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmRuleDTO;

import java.util.List;

/**
 * 告警检查
 * @author zengqiao
 * @date 20/3/18
 */
public abstract class AbstractMonitorMatchService<T>  {

    public abstract MonitorMatchStatus validate(AlarmRuleDTO alarmRuleDTO, List<T> dataList);

    protected MonitorMatchStatus condition(Double v1, Double v2, String cond) {
        switch (cond) {
            case ">": return v1.compareTo(v2) > 0 ? MonitorMatchStatus.YES: MonitorMatchStatus.NO;
            case "<": return v1.compareTo(v2) < 0 ? MonitorMatchStatus.YES: MonitorMatchStatus.NO;
            case "=": return v1.equals(v2) ? MonitorMatchStatus.YES: MonitorMatchStatus.NO;
            case "!=": return !v1.equals(v2) ? MonitorMatchStatus.YES: MonitorMatchStatus.NO;
            default: return MonitorMatchStatus.UNKNOWN;
        }
    }
}