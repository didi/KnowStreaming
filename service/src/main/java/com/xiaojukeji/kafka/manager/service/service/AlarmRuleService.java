package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.AlarmRuleDO;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/3
 */
public interface AlarmRuleService {
    Result addAlarmRule(AlarmRuleDO alarmRuleDO);

    Result deleteById(String operator, Long id);

    Result updateById(String operator, AlarmRuleDO alarmRuleDO);

    AlarmRuleDO getById(Long id);

    List<AlarmRuleDO> listAll();
}
