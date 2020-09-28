package com.xiaojukeji.kafka.manager.web.converters;

import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MetricPoint;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorRuleDTO;
import com.xiaojukeji.kafka.manager.monitor.common.entry.vo.*;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppSummaryVO;
import com.xiaojukeji.kafka.manager.monitor.common.monitor.MonitorAlertDetail;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.monitor.common.entry.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.MonitorRuleDO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/21
 */
public class MonitorRuleConverter {
    public static MonitorRuleDetailVO convert2MonitorRuleDetailVO(MonitorRuleDO monitorRuleDO,
                                                                  MonitorRuleDTO monitorRuleDTO,
                                                                  AppDO appDO) {
        MonitorRuleDetailVO vo = new MonitorRuleDetailVO();
        vo.setId(monitorRuleDO.getId());
        vo.setName(monitorRuleDO.getName());
        vo.setOperator(monitorRuleDO.getOperator());
        vo.setCreateTime(monitorRuleDO.getCreateTime().getTime());
        vo.setModifyTime(monitorRuleDO.getModifyTime().getTime());
        vo.setMonitorRule(monitorRuleDTO);
        if (ValidateUtils.isNull(appDO)) {
            return vo;
        }

        AppSummaryVO appSummaryVO = new AppSummaryVO();
        appSummaryVO.setAppId(appDO.getAppId());
        appSummaryVO.setName(appDO.getName());
        appSummaryVO.setPrincipals(appDO.getPrincipals());
        vo.setAppSummary(appSummaryVO);
        return vo;
    }

    public static List<MonitorAlertVO> convert2MonitorAlertVOList(List<Alert> alertList) {
        if (ValidateUtils.isNull(alertList)) {
            return new ArrayList<>();
        }

        List<MonitorAlertVO> voList = new ArrayList<>();
        for (Alert alert: alertList) {
            voList.add(convert2MonitorAlertVO(alert));
        }
        return voList;
    }

    public static MonitorAlertDetailVO convert2MonitorAlertDetailVO(MonitorAlertDetail monitorAlertDetail) {
        MonitorAlertDetailVO monitorAlertDetailVO = new MonitorAlertDetailVO();
        monitorAlertDetailVO.setMonitorAlert(convert2MonitorAlertVO(monitorAlertDetail.getAlert()));
        monitorAlertDetailVO.setMonitorMetric(convert2MonitorMetricVO(monitorAlertDetail.getMetric()));
        return monitorAlertDetailVO;
    }

    private static MonitorAlertVO convert2MonitorAlertVO(Alert alert) {
        if (ValidateUtils.isNull(alert)) {
            return null;
        }

        MonitorAlertVO vo = new MonitorAlertVO();
        vo.setAlertId(alert.getId());
        vo.setMonitorId(alert.getMonitorId());
        vo.setMonitorName(alert.getStrategyName());
        vo.setMonitorPriority(alert.getPriority());
        vo.setAlertStatus("alert".equals(alert.getType())? 0: 1);
        vo.setStartTime(alert.getStartTime() * 1000);
        vo.setEndTime(alert.getEndTime() * 1000);
        vo.setMetric(alert.getMetric());
        vo.setValue(alert.getValue());
        vo.setPoints(alert.getPoints());
        vo.setGroups(alert.getGroups());
        vo.setInfo(alert.getInfo());
        return vo;
    }

    private static MonitorMetricVO convert2MonitorMetricVO(Metric metric) {
        MonitorMetricVO vo = new MonitorMetricVO();
        vo.setMetric(metric.getMetric());
        vo.setStep(metric.getStep());
        vo.setValues(new ArrayList<>());
        vo.setComparison(metric.getComparison());
        vo.setDelta(metric.getDelta());
        vo.setOrigin(metric.getOrigin());

        for (MetricPoint metricPoint: metric.getValues()) {
            vo.getValues().add(new MonitorMetricPoint(metricPoint.getTimestamp(), metricPoint.getValue()));
        }
        return vo;
    }

    public static List<MonitorSilenceVO> convert2MonitorSilenceVOList(MonitorRuleDO monitorRuleDO,
                                                                      List<Silence> silenceList) {
        if (ValidateUtils.isNull(silenceList)) {
            return new ArrayList<>();
        }
        List<MonitorSilenceVO> voList = new ArrayList<>();
        for (Silence silence: silenceList) {
            voList.add(convert2MonitorSilenceVO(monitorRuleDO, silence));
        }
        return voList;
    }

    public static MonitorSilenceVO convert2MonitorSilenceVO(MonitorRuleDO monitorRuleDO, Silence silence) {
        if (ValidateUtils.isNull(silence)) {
            return null;
        }

        MonitorSilenceVO vo = new MonitorSilenceVO();
        vo.setSilenceId(silence.getSilenceId());
        vo.setMonitorId(monitorRuleDO.getId());
        vo.setMonitorName(monitorRuleDO.getName());
        vo.setStartTime(silence.getBeginTime());
        vo.setEndTime(silence.getEndTime());
        vo.setDescription(silence.getDescription());
        return vo;
    }

    public static List<MonitorNotifyGroupVO> convert2MonitorNotifyGroupVOList(List<NotifyGroup> notifyGroupList) {
        if (ValidateUtils.isEmptyList(notifyGroupList)) {
            return new ArrayList<>();
        }
        List<MonitorNotifyGroupVO> voList = new ArrayList<>();
        for (NotifyGroup notifyGroup: notifyGroupList) {
            MonitorNotifyGroupVO vo = new MonitorNotifyGroupVO();
            vo.setId(notifyGroup.getId());
            vo.setName(notifyGroup.getName());
            vo.setComment(notifyGroup.getComment());
            voList.add(vo);
        }
        return voList;
    }
}