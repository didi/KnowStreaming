package com.xiaojukeji.kafka.manager.monitor.component;

import com.xiaojukeji.kafka.manager.monitor.common.entry.*;

import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/4/30
 */
public abstract class AbstractMonitorService {
    /**
     * 监控策略的增删改查
     */
    public abstract Integer createStrategy(Strategy strategy);
    public abstract Boolean deleteStrategyById(Long strategyId);
    public abstract Boolean modifyStrategy(Strategy strategy);
    public abstract List<Strategy> getStrategies();
    public abstract Strategy getStrategyById(Long strategyId);

    /**
     * 告警被触发后, 告警信息的查询
     */
    public abstract List<Alert> getAlerts(Long strategyId, Long startTime, Long endTime);
    public abstract Alert getAlertById(Long alertId);

    /**
     * 告警被触发之后, 进行屏蔽时, 屏蔽策略的增删改查
     */
    public abstract Boolean createSilence(Silence silence);
    public abstract Boolean releaseSilence(Long silenceId);
    public abstract Boolean modifySilence(Silence silence);
    public abstract List<Silence> getSilences(Long strategyId);
    public abstract Silence getSilenceById(Long silenceId);

    /**
     * 告警组获取
     */
    public abstract List<NotifyGroup> getNotifyGroups();

    /**
     * 监控指标的上报和查询
     */
    public abstract Boolean sinkMetrics(List<MetricSinkPoint> pointList);
    public abstract Metric getMetrics(String metric, Long startTime, Long endTime, Integer step, Properties tags);
}