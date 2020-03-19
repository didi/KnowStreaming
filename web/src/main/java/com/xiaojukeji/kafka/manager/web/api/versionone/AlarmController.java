package com.xiaojukeji.kafka.manager.web.api.versionone;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.AlarmRuleDO;
import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorConditionType;
import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorMetricsType;
import com.xiaojukeji.kafka.manager.common.constant.monitor.MonitorNotifyType;
import com.xiaojukeji.kafka.manager.service.service.AlarmRuleService;
import com.xiaojukeji.kafka.manager.service.utils.SpringContextHolder;
import com.xiaojukeji.kafka.manager.web.converters.AlarmConverter;
import com.xiaojukeji.kafka.manager.web.model.alarm.AlarmRuleModel;
import com.xiaojukeji.kafka.manager.web.vo.alarm.AlarmConstantVO;
import com.xiaojukeji.kafka.manager.web.vo.alarm.AlarmRuleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/12
 */
@Api(value = "AlarmController", description = "Alarm相关接口")
@Controller
@RequestMapping("api/v1/")
public class AlarmController {
    private final static Logger logger = LoggerFactory.getLogger(AlarmController.class);

    @Autowired
    private AlarmRuleService alarmRuleManagerService;

    @ApiOperation(value = "添加告警规则", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "alarms/alarm-rule", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result addAlarmRule(@RequestBody AlarmRuleModel alarmModel) {
        if (alarmModel == null || !alarmModel.legal()) {
            return new Result(StatusCode.PARAM_ERROR, "param illegal");
        }
        return alarmRuleManagerService.addAlarmRule(AlarmConverter.convert2AlarmRuleDO(SpringContextHolder.getUserName(), alarmModel));
    }

    @ApiOperation(value = "删除告警规则", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "alarms/alarm-rule", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result deleteAlarmRule(@RequestParam("alarmRuleId") Long alarmRuleId) {
        if (alarmRuleId == null) {
            return new Result(StatusCode.PARAM_ERROR, "param error");
        }
        return alarmRuleManagerService.deleteById(SpringContextHolder.getUserName(), alarmRuleId);
    }

    @ApiOperation(value = "修改告警规则", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = Result.class)
    @RequestMapping(value = "alarms/alarm-rule", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result modifyAlarmRule(@RequestBody AlarmRuleModel reqObj) {
        if (reqObj == null || !reqObj.legal() || reqObj.getId() == null || reqObj.getStatus() == null) {
            return new Result(StatusCode.PARAM_ERROR, "param error");
        }
        return alarmRuleManagerService.updateById(SpringContextHolder.getUserName(), AlarmConverter.convert2AlarmRuleDO(SpringContextHolder.getUserName(), reqObj));
    }

    @ApiOperation(value = "查询告警规则", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = AlarmRuleVO.class)
    @RequestMapping(value = "alarms/alarm-rules/{alarmRuleId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<AlarmRuleVO> getAlarmRule(@PathVariable Long alarmRuleId) {
        if (alarmRuleId == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "param error");
        }
        AlarmRuleDO alarmRuleDO = alarmRuleManagerService.getById(alarmRuleId);
        if (alarmRuleDO == null) {
            return new Result<>(StatusCode.PARAM_ERROR, "alarm not exist");
        }
        return new Result<>(AlarmConverter.convert2AlarmRuleVO(alarmRuleDO));
    }

    @ApiOperation(value = "查询告警规则列表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = AlarmRuleVO.class)
    @RequestMapping(value = "alarms/alarm-rules", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<List<AlarmRuleVO>> listAlarmRules() {
        List<AlarmRuleDO> alarmRuleDOList = alarmRuleManagerService.listAll();
        if (alarmRuleDOList == null) {
            return new Result<>(StatusCode.MY_SQL_SELECT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result<>(AlarmConverter.convert2AlarmRuleVOList(alarmRuleDOList));
    }

    @ApiOperation(value = "告警相关常量", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(code = 200, message = "success", response = AlarmConstantVO.class)
    @RequestMapping(value = "alarms/alarm/constant", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Result<AlarmConstantVO> getAlarmConstant() {
        AlarmConstantVO alarmConstantVO = new AlarmConstantVO();
        alarmConstantVO.setConditionTypeList(MonitorConditionType.toList());
        alarmConstantVO.setNotifyTypeList(MonitorNotifyType.toList());
        alarmConstantVO.setMetricTypeList(MonitorMetricsType.toList());
        return new Result<>(alarmConstantVO);
    }
}