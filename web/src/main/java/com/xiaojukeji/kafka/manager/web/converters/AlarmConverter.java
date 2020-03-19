package com.xiaojukeji.kafka.manager.web.converters;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.web.model.alarm.AlarmRuleModel;
import com.xiaojukeji.kafka.manager.web.vo.alarm.AlarmRuleVO;
import com.xiaojukeji.kafka.manager.common.entity.po.AlarmRuleDO;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyActionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyExpressionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyFilterDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/12
 */
public class AlarmConverter {
    public static AlarmRuleDO convert2AlarmRuleDO(String applicant, AlarmRuleModel alarmRuleModel) {
        AlarmRuleDO alarmRuleDO = new AlarmRuleDO();
        alarmRuleDO.setId(alarmRuleModel.getId());
        alarmRuleDO.setAlarmName(alarmRuleModel.getAlarmName());
        alarmRuleDO.setStrategyExpressions(JSON.toJSONString(alarmRuleModel.getStrategyExpressionList()));
        alarmRuleDO.setStrategyFilters(JSON.toJSONString(alarmRuleModel.getStrategyFilterList()));
        alarmRuleDO.setStrategyActions(JSON.toJSONString(alarmRuleModel.getStrategyActionList()));
        if (!alarmRuleModel.getPrincipalList().contains(applicant)) {
            alarmRuleModel.getPrincipalList().add(applicant);
        }
        alarmRuleDO.setPrincipals(ListUtils.strList2String(alarmRuleModel.getPrincipalList()));
        alarmRuleDO.setStatus(alarmRuleModel.getStatus());
        return alarmRuleDO;
    }

    public static AlarmRuleVO convert2AlarmRuleVO(AlarmRuleDO alarmRuleDO) {
        if (alarmRuleDO == null) {
            return null;
        }
        AlarmRuleVO alarmRuleVO = new AlarmRuleVO();
        try {
            alarmRuleVO.setStrategyActionList(JSON.parseArray(alarmRuleDO.getStrategyActions(), AlarmStrategyActionDTO.class));
        } catch (Exception e) {
        }
        try {
            alarmRuleVO.setStrategyExpressionList(JSON.parseArray(alarmRuleDO.getStrategyExpressions(), AlarmStrategyExpressionDTO.class));
        } catch (Exception e) {
        }
        try {
            alarmRuleVO.setStrategyFilterList(JSON.parseArray(alarmRuleDO.getStrategyFilters(), AlarmStrategyFilterDTO.class));
        } catch (Exception e) {
        }
        alarmRuleVO.setId(alarmRuleDO.getId());
        alarmRuleVO.setAlarmName(alarmRuleDO.getAlarmName());
        alarmRuleVO.setPrincipalList(ListUtils.string2StrList(alarmRuleDO.getPrincipals()));
        alarmRuleVO.setStatus(alarmRuleDO.getStatus());
        alarmRuleVO.setGmtCreate(alarmRuleDO.getGmtCreate().getTime());
        alarmRuleVO.setGmtModify(alarmRuleDO.getGmtModify().getTime());
        return alarmRuleVO;
    }

    public static List<AlarmRuleVO> convert2AlarmRuleVOList(List<AlarmRuleDO> alarmRuleDOList) {
        if (alarmRuleDOList == null) {
            return new ArrayList<>();
        }
        List<AlarmRuleVO> alarmRuleVOList = new ArrayList<>();
        for (AlarmRuleDO alarmRuleDO: alarmRuleDOList) {
            alarmRuleVOList.add(convert2AlarmRuleVO(alarmRuleDO));
        }
        return alarmRuleVOList;
    }
}