package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/21
 */
public interface MonitorRuleDao {
    int insert(MonitorRuleDO monitorRuleDO);

    int deleteById(Long id);

    int updateById(Long id, String name, String appId, String operator);

    MonitorRuleDO getById(Long id);

    MonitorRuleDO getByStrategyId(Long strategyId);

    List<MonitorRuleDO> listAll();
}