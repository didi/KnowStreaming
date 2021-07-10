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
 * 监控系统接口
 * @author zengqiao
 * @date 20/5/21
 */
public interface MonitorService {
    /**
     * 创建告警规则
     * @param monitorDTO 告警规则
     * @param operator 操作人
     * @return 操作状态结果
     */
    ResultStatus createMonitorRule(MonitorRuleDTO monitorDTO, String operator);

    /**
     * 删除告警规则
     * @param id 告警ID
     * @param operator 操作人
     * @return 操作状态结果
     */
    ResultStatus deleteMonitorRule(Long id, String operator);

    /**
     * 修改告警规则
     * @param monitorDTO 告警规则
     * @param operator 操作人
     * @return 操作状态结果
     */
    ResultStatus modifyMonitorRule(MonitorRuleDTO monitorDTO, String operator);

    /**
     * 获取告警规则
     * @param operator 操作人
     * @return 监控告警规则概要信息
     */
    List<MonitorRuleSummary> getMonitorRules(String operator);

    /**
     * 获取监控告警规则的详情信息
     * @param monitorRuleDO 本地存储的监控告警规则概要信息
     * @return
     */
    Result<MonitorRuleDTO> getMonitorRuleDetail(MonitorRuleDO monitorRuleDO);

    /**
     * 依据主键ID, 获取存储于MySQL中的监控告警规则基本信息
     * @param id 本地监控告警规则ID
     * @return 本地监控告警规则信息
     */
    MonitorRuleDO getById(Long id);

    /**
     * 依据策略ID, 获取存储于MySQL中的监控告警规则基本信息
     * @param strategyId  策略ID
     * @return 本地监控告警规则信息
     */
    MonitorRuleDO getByStrategyId(Long strategyId);

    /**
     * 获取告警历史
     * @param id 告警ID
     * @param startTime 查询的起始时间
     * @param endTime 查询的截止时间
     * @return 告警历史
     */
    Result<List<Alert>> getMonitorAlertHistory(Long id, Long startTime, Long endTime);

    /**
     * 查询告警详情
     * @param alertId 告警ID
     * @return 告警详情
     */
    Result<MonitorAlertDetail> getMonitorAlertDetail(Long alertId);

    /**
     * 屏蔽告警
     * @param monitorSilenceDTO 屏蔽的信息
     * @param operator 操作人
     * @return 屏蔽操作的结果
     */
    Result createSilence(MonitorSilenceDTO monitorSilenceDTO, String operator);

    /**
     * 删除屏蔽策略
     * @param silenceId 屏蔽ID
     * @return 删除屏蔽告警的操作结果
     */
    Boolean releaseSilence(Long silenceId);

    /**
     * 修改屏蔽告警的规则
     * @param monitorSilenceDTO 屏蔽告警的信息
     * @param operator 操作人
     * @return 操作结果
     */
    Result modifySilence(MonitorSilenceDTO monitorSilenceDTO, String operator);

    /**
     * 获取屏蔽策略
     * @param strategyId 告警策略ID
     * @return
     */
    Result<List<Silence>> getSilences(Long strategyId);

    /**
     * 获取屏蔽详情
     * @param silenceId 屏蔽ID
     * @return
     */
    Silence getSilenceById(Long silenceId);

    /**
     * 获取告警接收组
     * @return
     */
    List<NotifyGroup> getNotifyGroups();
}