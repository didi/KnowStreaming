package com.xiaojukeji.kafka.manager.monitor.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorRuleDTO;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorSilenceDTO;
import com.xiaojukeji.kafka.manager.monitor.common.monitor.MonitorAlertDetail;
import com.xiaojukeji.kafka.manager.monitor.common.monitor.MonitorRuleSummary;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.dao.MonitorRuleDao;
import com.xiaojukeji.kafka.manager.dao.gateway.AppDao;
import com.xiaojukeji.kafka.manager.monitor.common.utils.CommonConverter;
import com.xiaojukeji.kafka.manager.monitor.component.AbstractMonitorService;
import com.xiaojukeji.kafka.manager.monitor.common.entry.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.MonitorRuleDO;
import com.xiaojukeji.kafka.manager.monitor.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author zengqiao
 * @date 20/5/21
 */
@Service("monitorService")
public class MonitorServiceImpl implements MonitorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorServiceImpl.class);

    @Autowired
    private AbstractMonitorService abstractMonitor;

    @Autowired
    private MonitorRuleDao monitorRuleDao;

    @Autowired
    private AppDao appDao;

    @Override
    public ResultStatus createMonitorRule(MonitorRuleDTO monitorRuleDTO, String operator) {
        Integer strategyId = abstractMonitor.createStrategy(
                CommonConverter.convert2Strategy(null, monitorRuleDTO)
        );
        if (ValidateUtils.isNull(strategyId)) {
            return ResultStatus.CALL_MONITOR_SYSTEM_ERROR;
        }

        MonitorRuleDO monitorRuleDO = new MonitorRuleDO();
        monitorRuleDO.setAppId(monitorRuleDTO.getAppId());
        monitorRuleDO.setName(monitorRuleDTO.getName());
        monitorRuleDO.setOperator(operator);
        monitorRuleDO.setStrategyId(strategyId.longValue());
        try {
            if (monitorRuleDao.insert(monitorRuleDO) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("create monitor rule failed, monitorRuleDTO:{}.", monitorRuleDTO, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ResultStatus deleteMonitorRule(Long id, String operator) {
        MonitorRuleDO monitorRuleDO = this.getById(id);
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return ResultStatus.MONITOR_NOT_EXIST;
        }
        Boolean status = abstractMonitor.deleteStrategyById(monitorRuleDO.getStrategyId());
        if (!status) {
            return ResultStatus.CALL_MONITOR_SYSTEM_ERROR;
        }
        try {
            if (monitorRuleDao.deleteById(id) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("delete monitor rule failed, id:{}.", id, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public ResultStatus modifyMonitorRule(MonitorRuleDTO monitorRuleDTO, String operator) {
        MonitorRuleDO monitorRuleDO = this.getById(monitorRuleDTO.getId());
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return ResultStatus.MONITOR_NOT_EXIST;
        }
        Boolean status = abstractMonitor.modifyStrategy(
                CommonConverter.convert2Strategy(monitorRuleDO.getStrategyId(), monitorRuleDTO)
        );
        if (!status) {
            return ResultStatus.CALL_MONITOR_SYSTEM_ERROR;
        }

        try {
            if (monitorRuleDao.updateById(
                    monitorRuleDTO.getId(),
                    monitorRuleDTO.getName(),
                    monitorRuleDTO.getAppId(),
                    operator) > 0) {
                return ResultStatus.SUCCESS;
            }
        } catch (Exception e) {
            LOGGER.error("modify monitor rule failed, monitorRuleDTO:{}.", monitorRuleDTO, e);
        }
        return ResultStatus.MYSQL_ERROR;
    }

    @Override
    public List<MonitorRuleSummary> getMonitorRules(String operator) {
        List<MonitorRuleDO> monitorRuleDOList = this.listAll();
        if (ValidateUtils.isEmptyList(monitorRuleDOList)) {
            return new ArrayList<>();
        }

        List<AppDO> appDOList = appDao.getByPrincipal(operator);
        if (ValidateUtils.isEmptyList(appDOList)) {
            return new ArrayList<>();
        }
        Map<String, AppDO> appMap = new HashMap<>();
        for (AppDO appDO: appDOList) {
            appMap.put(appDO.getAppId(), appDO);
        }

        List<MonitorRuleSummary> summaryList = new ArrayList<>();
        for (MonitorRuleDO elem: monitorRuleDOList) {
            AppDO appDO = appMap.get(elem.getAppId());
            if (ValidateUtils.isNull(appDO)) {
                continue;
            }
            MonitorRuleSummary summary = new MonitorRuleSummary();
            summary.setId(elem.getId());
            summary.setName(elem.getName());
            summary.setAppId(elem.getAppId());
            summary.setAppName(appDO.getName());
            summary.setPrincipals(appDO.getPrincipals());
            summary.setOperator(elem.getOperator());
            summary.setCreateTime(elem.getCreateTime().getTime());
            summaryList.add(summary);
        }
        return summaryList;

    }

    @Override
    public Result<MonitorRuleDTO> getMonitorRuleDetail(MonitorRuleDO monitorRuleDO) {
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST);
        }
        Strategy strategy = abstractMonitor.getStrategyById(monitorRuleDO.getStrategyId());
        if (ValidateUtils.isNull(strategy)) {
            return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
        }

        MonitorRuleDTO monitorRuleDTO = CommonConverter.convert2MonitorRuleDTO(monitorRuleDO, strategy);
        monitorRuleDTO.setId(monitorRuleDO.getId());
        monitorRuleDTO.setAppId(monitorRuleDO.getAppId());
        return new Result<>(monitorRuleDTO);
    }

    @Override
    public Result<List<Alert>> getMonitorAlertHistory(Long id, Long startTime, Long endTime) {
        MonitorRuleDO monitorRuleDO = this.getById(id);
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST);
        }
        List<Alert> alertList =
                abstractMonitor.getAlerts(monitorRuleDO.getStrategyId(), startTime / 1000, endTime / 1000);
        if (ValidateUtils.isNull(alertList)) {
            return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
        }
        return new Result<>(alertList);
    }

    @Override
    public Result<MonitorAlertDetail> getMonitorAlertDetail(Long alertId) {
        Alert alert = abstractMonitor.getAlertById(alertId);
        if (ValidateUtils.isNull(alert)) {
            return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
        }

        MonitorRuleDO monitorRuleDO = this.getByStrategyId(alert.getStrategyId());
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST);
        }
        alert.setMonitorId(monitorRuleDO.getId());

        Metric metric = abstractMonitor.getMetrics(alert.getMetric(),
                (alert.getStartTime() - 3600) * 1000 ,
                (alert.getEndTime() + 3600) * 1000,
                60,
                alert.getTags());

        return new Result<>(new MonitorAlertDetail(alert, metric));
    }

    @Override
    public MonitorRuleDO getById(Long id) {
        try {
            return monitorRuleDao.getById(id);
        } catch (Exception e) {
            LOGGER.error("get monitor rule failed, id:{}.", id, e);
        }
        return null;
    }

    @Override
    public MonitorRuleDO getByStrategyId(Long strategyId) {
        try {
            return monitorRuleDao.getByStrategyId(strategyId);
        } catch (Exception e) {
            LOGGER.error("get monitor rule failed, strategyId:{}.", strategyId);
        }
        return null;
    }

    private List<MonitorRuleDO> listAll() {
        try {
            return monitorRuleDao.listAll();
        } catch (Exception e) {
            LOGGER.error("get all monitor rule failed.", e);
        }
        return null;
    }

    @Override
    public Result createSilence(MonitorSilenceDTO monitorSilenceDTO, String operator) {
        MonitorRuleDO monitorRuleDO = this.getById(monitorSilenceDTO.getMonitorId());
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST);
        }

        Boolean status = abstractMonitor.createSilence(
                CommonConverter.convert2Silence(monitorRuleDO, monitorSilenceDTO)
        );
        if (status) {
            return new Result();
        }
        return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
    }

    @Override
    public Boolean releaseSilence(Long silenceId) {
        if (ValidateUtils.isNull(silenceId)) {
            return Boolean.FALSE;
        }
        return abstractMonitor.releaseSilence(silenceId);
    }

    @Override
    public Result modifySilence(MonitorSilenceDTO monitorSilenceDTO, String operator) {
        MonitorRuleDO monitorRuleDO = this.getById(monitorSilenceDTO.getMonitorId());
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST);
        }
        Boolean status = abstractMonitor.modifySilence(
                CommonConverter.convert2Silence(monitorRuleDO, monitorSilenceDTO)
        );
        if (status) {
            return new Result();
        }
        return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
    }

    @Override
    public Result<List<Silence>> getSilences(Long monitorId) {
        List<Silence> silenceList = abstractMonitor.getSilences(monitorId);
        if (ValidateUtils.isNull(silenceList)) {
            return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
        }
        return new Result<>(silenceList);
    }

    @Override
    public Silence getSilenceById(Long silenceId) {
        if (ValidateUtils.isNull(silenceId)) {
            return null;
        }
        return abstractMonitor.getSilenceById(silenceId);
    }

    @Override
    public List<NotifyGroup> getNotifyGroups() {
        return abstractMonitor.getNotifyGroups();
    }
}