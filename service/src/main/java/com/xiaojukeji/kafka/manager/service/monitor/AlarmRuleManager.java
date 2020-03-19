package com.xiaojukeji.kafka.manager.service.monitor;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.DBStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmRuleDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyActionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyExpressionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyFilterDTO;
import com.xiaojukeji.kafka.manager.common.entity.po.AlarmRuleDO;
import com.xiaojukeji.kafka.manager.dao.AlarmRuleDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * 监控告警规则管理
 * @author zengqiao
 * @date 2019-05-05
 */
@Service
public class AlarmRuleManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(AlarmRuleManager.class);

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    private static long activeLastGmtModifyTime = 0L;

    private static volatile Map<Long, AlarmRuleDTO> activeAlarmRuleMap = new ConcurrentHashMap<>();

    @Scheduled(cron="0 0/1 * * * ?")
    public void flushAlarmRuleCache() {
        long startTime = System.currentTimeMillis();
        LOGGER.info("alarm rule flush, start.");
        try {
            flush();
        } catch (Throwable t) {
            LOGGER.error("alarm rule flush, throw exception.", t);
        }
        LOGGER.info("alarm rule flush, finished, costTime:{}ms.", System.currentTimeMillis() - startTime);
    }

    private void flush() {
        List<AlarmRuleDO> dbAlarmRuleDOList = alarmRuleDao.listAll();
        if(dbAlarmRuleDOList == null) {
            return;
        }

        long maxGmtModifyTime = activeLastGmtModifyTime;
        Set<Long> dbAlarmRuleIdSet = new HashSet<>();
        for (AlarmRuleDO alarmRuleDO: dbAlarmRuleDOList) {
            dbAlarmRuleIdSet.add(alarmRuleDO.getId());
            if (alarmRuleDO.getGmtModify().getTime() > maxGmtModifyTime) {
                maxGmtModifyTime = alarmRuleDO.getGmtModify().getTime();
            }

            if (!DBStatusEnum.PASSED.getStatus().equals(alarmRuleDO.getStatus())) {
                // 移除暂不生效的告警规则
                activeAlarmRuleMap.remove(alarmRuleDO.getId());
                continue;
            }

            if (!activeAlarmRuleMap.containsKey(alarmRuleDO.getId())
                    || activeLastGmtModifyTime < alarmRuleDO.getGmtModify().getTime()) {
                // 新增或修改的告警规则
                AlarmRuleDTO alarmRuleDTO = convert2AlarmRuleDTO(alarmRuleDO);
                if (alarmRuleDTO == null) {
                    LOGGER.error("alarm rule flush, convert 2 dto failed.");
                    continue;
                }
                activeAlarmRuleMap.put(alarmRuleDO.getId(), alarmRuleDTO);
            }
        }

        // 移除已被删除的告警规则
        Set<Long> activeAlarmRuleIdSet = new HashSet<>(activeAlarmRuleMap.keySet());
        activeAlarmRuleIdSet.removeAll(dbAlarmRuleIdSet);
        for (Long ruleId: activeAlarmRuleIdSet) {
            activeAlarmRuleMap.remove(ruleId);
        }

        // 更新最近更新时间
        activeLastGmtModifyTime = maxGmtModifyTime;
    }

    public static Map<Long, AlarmRuleDTO> getActiveAlarmRuleMap() {
        return activeAlarmRuleMap;
    }

    private AlarmRuleDTO convert2AlarmRuleDTO(AlarmRuleDO alarmRuleDO) {
        if (alarmRuleDO == null) {
            return null;
        }

        AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
        alarmRuleDTO.setId(alarmRuleDO.getId());
        alarmRuleDTO.setName(alarmRuleDO.getAlarmName());
        alarmRuleDTO.setDuration(0);
        alarmRuleDTO.setClusterId(null);
        alarmRuleDTO.setStrategyExpression(null);
        alarmRuleDTO.setStrategyFilterMap(new HashMap<>());
        alarmRuleDTO.setStrategyActionMap(new HashMap<>());
        alarmRuleDTO.setGmtModify(alarmRuleDO.getGmtModify().getTime());
        try {
            List<AlarmStrategyExpressionDTO> alarmStrategyExpressionDTOList = JSON.parseArray(alarmRuleDO.getStrategyExpressions(), AlarmStrategyExpressionDTO.class);
            if (alarmStrategyExpressionDTOList == null || alarmStrategyExpressionDTOList.size() != 1) {
                // 策略表达式不符合要求
                return null;
            }
            alarmRuleDTO.setStrategyExpression(alarmStrategyExpressionDTOList.get(0));

            List<AlarmStrategyFilterDTO> alarmStrategyFilterDTOList = JSON.parseArray(alarmRuleDO.getStrategyFilters(), AlarmStrategyFilterDTO.class);
            if (alarmStrategyFilterDTOList == null || alarmStrategyFilterDTOList.isEmpty()) {
                // 无过滤策略
                return null;
            }
            for (AlarmStrategyFilterDTO alarmStrategyFilterDTO: alarmStrategyFilterDTOList) {
                if ("clusterId".equals(alarmStrategyFilterDTO.getKey())) {
                    alarmRuleDTO.setClusterId(Long.valueOf(alarmStrategyFilterDTO.getValue()));
                    continue;
                }
                alarmRuleDTO.getStrategyFilterMap().put(alarmStrategyFilterDTO.getKey(), alarmStrategyFilterDTO.getValue());
            }

            List<AlarmStrategyActionDTO> alarmStrategyActionDTOList = JSON.parseArray(alarmRuleDO.getStrategyActions(), AlarmStrategyActionDTO.class);
            if (alarmStrategyActionDTOList == null || alarmStrategyActionDTOList.isEmpty()) {
                // 无告知方式
                return null;
            }
            for (AlarmStrategyActionDTO alarmStrategyActionDTO: alarmStrategyActionDTOList) {
                alarmRuleDTO.getStrategyActionMap().put(alarmStrategyActionDTO.getActionWay(), alarmStrategyActionDTO);
            }
        } catch (Exception e) {
            LOGGER.error("alarm rule flush, convert 2 object failed, alarmRuleDO:{}.", alarmRuleDO, e);
            return null;
        }

        return alarmRuleDTO;
    }
}
