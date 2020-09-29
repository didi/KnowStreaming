package com.xiaojukeji.kafka.manager.web.api.versionone.normal;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.monitor.common.entry.bizenum.MonitorMetricNameEnum;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorRuleDTO;
import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorSilenceDTO;
import com.xiaojukeji.kafka.manager.monitor.common.entry.vo.*;
import com.xiaojukeji.kafka.manager.monitor.common.monitor.*;
import com.xiaojukeji.kafka.manager.common.utils.CopyUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Alert;
import com.xiaojukeji.kafka.manager.monitor.common.entry.NotifyGroup;
import com.xiaojukeji.kafka.manager.monitor.common.entry.Silence;
import com.xiaojukeji.kafka.manager.common.entity.pojo.MonitorRuleDO;
import com.xiaojukeji.kafka.manager.service.service.gateway.AppService;
import com.xiaojukeji.kafka.manager.monitor.MonitorService;
import com.xiaojukeji.kafka.manager.common.utils.JsonUtils;
import com.xiaojukeji.kafka.manager.common.utils.SpringTool;
import com.xiaojukeji.kafka.manager.common.constant.ApiPrefix;
import com.xiaojukeji.kafka.manager.web.converters.MonitorRuleConverter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/5/4
 */
@Api(tags = "Normal-Monitor相关接口(REST)")
@RestController
@RequestMapping(ApiPrefix.API_V1_NORMAL_PREFIX)
public class NormalMonitorController {

    @Autowired
    private AppService appService;

    @Autowired
    private MonitorService monitorService;

    @ApiOperation(value = "监控枚举类", notes = "")
    @RequestMapping(value = "monitor-enums", method = RequestMethod.GET)
    @ResponseBody
    public Result getMonitorEnums() {
        Map<String, Object> data = new HashMap<>(1);
        data.put("metricNames", JsonUtils.toJson(MonitorMetricNameEnum.class));
        return new Result<>(data);
    }

    @ApiOperation(value = "添加监控策略", notes = "")
    @RequestMapping(value = "monitor-strategies", method = RequestMethod.POST)
    @ResponseBody
    public Result createMonitor(@RequestBody MonitorRuleDTO dto) {
        if (!dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(monitorService.createMonitorRule(dto, SpringTool.getUserName()));
    }

    @ApiOperation(value = "删除监控策略", notes = "")
    @RequestMapping(value = "monitor-strategies", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deleteMonitor(@RequestParam("monitorId") Long monitorId) {
        return Result.buildFrom(monitorService.deleteMonitorRule(monitorId, SpringTool.getUserName()));
    }

    @ApiOperation(value = "修改监控策略", notes = "")
    @RequestMapping(value = "monitor-strategies", method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyMonitors(@RequestBody MonitorRuleDTO dto) {
        if (!dto.paramLegal() || ValidateUtils.isNull(dto.getId())) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return Result.buildFrom(monitorService.modifyMonitorRule(dto, SpringTool.getUserName()));
    }

    @ApiOperation(value = "监控策略列表", notes = "")
    @RequestMapping(value = "monitor-strategies", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<MonitorRuleSummaryVO>> getMonitorRules() {
        List<MonitorRuleSummary> monitorRuleSummaryList =
                monitorService.getMonitorRules(SpringTool.getUserName());
        if (ValidateUtils.isEmptyList(monitorRuleSummaryList)) {
            return new Result<>(new ArrayList<>());
        }

        List<MonitorRuleSummaryVO> voList = new ArrayList<>();
        for (MonitorRuleSummary summary: monitorRuleSummaryList) {
            MonitorRuleSummaryVO vo = new MonitorRuleSummaryVO();
            CopyUtils.copyProperties(vo, summary);
            voList.add(vo);
        }
        return new Result<>(voList);
    }

    @ApiOperation(value = "监控策略详情", notes = "")
    @RequestMapping(value = "monitor-strategies/{monitorId}/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<MonitorRuleDetailVO> getMonitorDetail(@PathVariable("monitorId") Long monitorId) {
        MonitorRuleDO monitorRuleDO = monitorService.getById(monitorId);
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST);
        }
        Result<MonitorRuleDTO> monitorRuleDTOResult = monitorService.getMonitorRuleDetail(monitorRuleDO);
        if (!Constant.SUCCESS.equals(monitorRuleDTOResult.getCode())) {
            return new Result<>(monitorRuleDTOResult.getCode(), monitorRuleDTOResult.getMessage());
        }

        MonitorRuleDTO monitorRuleDTO = monitorRuleDTOResult.getData();
        AppDO appDO = appService.getByAppId(monitorRuleDTO.getAppId());
        return new Result<>(MonitorRuleConverter.convert2MonitorRuleDetailVO(monitorRuleDO, monitorRuleDTO, appDO));
    }

    @ApiOperation(value = "告警列表", notes = "")
    @RequestMapping(value = "monitor-alerts", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<MonitorAlertVO>> getMonitorAlertHistory(@RequestParam("monitorId") Long monitorId,
                                                               @RequestParam("startTime") Long startTime,
                                                               @RequestParam("endTime") Long endTime) {
        Result<List<Alert>> alertResult = monitorService.getMonitorAlertHistory(monitorId, startTime, endTime);
        if (!Constant.SUCCESS.equals(alertResult.getCode())) {
            return new Result<>(alertResult.getCode(), alertResult.getMessage());
        }
        return new Result<>(MonitorRuleConverter.convert2MonitorAlertVOList(alertResult.getData()));
    }

    @ApiOperation(value = "告警详情", notes = "")
    @RequestMapping(value = "monitor-alerts/{alertId}/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<MonitorAlertDetailVO> getMonitorAlertDetail(@PathVariable("alertId") Long alertId) {
        Result<MonitorAlertDetail> alertResult = monitorService.getMonitorAlertDetail(alertId);
        if (!Constant.SUCCESS.equals(alertResult.getCode())) {
            return new Result<>(alertResult.getCode(), alertResult.getMessage());
        }
        return new Result<>(MonitorRuleConverter.convert2MonitorAlertDetailVO(alertResult.getData()));
    }

    @ApiOperation(value = "告警屏蔽创建", notes = "")
    @RequestMapping(value = "monitor-silences", method = RequestMethod.POST)
    @ResponseBody
    public Result createMonitorSilences(@RequestBody MonitorSilenceDTO dto) {
        if (!dto.paramLegal()) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return monitorService.createSilence(dto, SpringTool.getUserName());
    }

    @ApiOperation(value = "告警屏蔽修改", notes = "")
    @RequestMapping(value = "monitor-silences", method = RequestMethod.PUT)
    @ResponseBody
    public Result modifyMonitorSilences(@RequestBody MonitorSilenceDTO dto) {
        if (!dto.paramLegal() || ValidateUtils.isNull(dto.getId())) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        return monitorService.modifySilence(dto, SpringTool.getUserName());
    }

    @ApiOperation(value = "告警屏蔽删除", notes = "")
    @RequestMapping(value = "monitor-silences", method = RequestMethod.DELETE)
    @ResponseBody
    public Result releaseMonitorSilences(@RequestParam("monitorId") Long monitorId,
                                         @RequestParam("silenceId") Long silenceId) {

        Boolean status = monitorService.releaseSilence(silenceId);
        if (ValidateUtils.isNull(status) || !status) {
            return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
        }
        return new Result();
    }

    @ApiOperation(value = "告警屏蔽列表", notes = "")
    @RequestMapping(value = "monitor-silences", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<MonitorSilenceVO>> getMonitorSilences(@RequestParam("monitorId") Long monitorId) {
        MonitorRuleDO monitorRuleDO = monitorService.getById(monitorId);
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST);
        }

        Result<List<Silence>> listResult = monitorService.getSilences(monitorRuleDO.getStrategyId());
        if (!Constant.SUCCESS.equals(listResult.getCode())) {
            return new Result<>(listResult.getCode(), listResult.getMessage());
        }
        return new Result<>(MonitorRuleConverter.convert2MonitorSilenceVOList(monitorRuleDO, listResult.getData()));
    }

    @ApiOperation(value = "告警屏蔽详情", notes = "")
    @RequestMapping(value = "monitor-silences/{silenceId}/detail", method = RequestMethod.GET)
    @ResponseBody
    public Result<MonitorSilenceVO> getMonitorSilence(@PathVariable("silenceId") Long silenceId) {
        Silence silence = monitorService.getSilenceById(silenceId);
        if (ValidateUtils.isNull(silence)) {
            return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
        }

        MonitorRuleDO monitorRuleDO = monitorService.getByStrategyId(silence.getStrategyId());
        if (ValidateUtils.isNull(monitorRuleDO)) {
            return Result.buildFrom(ResultStatus.MONITOR_NOT_EXIST);
        }

        return new Result<>(MonitorRuleConverter.convert2MonitorSilenceVO(monitorRuleDO, silence));
    }

    @ApiOperation(value = "告警组列表", notes = "")
    @RequestMapping(value = "monitor-notify-groups", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<MonitorNotifyGroupVO>> getNotifyGroups() {
        List<NotifyGroup> notifyGroupList = monitorService.getNotifyGroups();
        if (ValidateUtils.isNull(notifyGroupList)) {
            return Result.buildFrom(ResultStatus.CALL_MONITOR_SYSTEM_ERROR);
        }
        return new Result<>(MonitorRuleConverter.convert2MonitorNotifyGroupVOList(notifyGroupList));
    }
}