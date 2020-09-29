package com.xiaojukeji.kafka.manager.monitor;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorRuleDTO;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorSilenceDTO;
import com.xiaojukeji.kafka.manager.monitor.common.monitor.MonitorAlertDetail;
import com.xiaojukeji.kafka.manager.monitor.common.monitor.MonitorRuleSummary;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Alert;
import com.xiaojukeji.kafka.manager.monitor.common.entry.NotifyGroup;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Silence;
import com.xiaojukeji.kafka.manager.common.entity.pojo.MonitorRuleDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/21
 */
public interface MonitorService {
    ResultStatus createMonitorRule(MonitorRuleDTO monitorDTO, String operator);

    ResultStatus deleteMonitorRule(Long id, String operator);

    ResultStatus modifyMonitorRule(MonitorRuleDTO monitorDTO, String operator);

    List<MonitorRuleSummary> getMonitorRules(String operator);

    Result<MonitorRuleDTO> getMonitorRuleDetail(MonitorRuleDO monitorRuleDO);

    MonitorRuleDO getById(Long id);

    MonitorRuleDO getByStrategyId(Long strategyId);

    Result<List<Alert>> getMonitorAlertHistory(Long id, Long startTime, Long endTime);

    Result<MonitorAlertDetail> getMonitorAlertDetail(Long alertId);

    Result createSilence(MonitorSilenceDTO monitorSilenceDTO, String operator);

    Boolean releaseSilence(Long silenceId);

    Result modifySilence(MonitorSilenceDTO monitorSilenceDTO, String operator);

    Result<List<Silence>> getSilences(Long strategyId);

    Silence getSilenceById(Long silenceId);

    List<NotifyGroup> getNotifyGroups();
}