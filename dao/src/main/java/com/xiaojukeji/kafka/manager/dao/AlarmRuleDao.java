package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.po.AlarmRuleDO;

import java.util.List;

public interface AlarmRuleDao {
    int insert(AlarmRuleDO alarmRuleDO);

    int deleteById(Long id);

    int updateById(AlarmRuleDO alarmRuleDO);

    AlarmRuleDO getById(Long id);

    List<AlarmRuleDO> listAll();
}