package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.AlarmRuleDO;
import com.xiaojukeji.kafka.manager.dao.AlarmRuleDao;
import com.xiaojukeji.kafka.manager.service.service.AlarmRuleService;
import com.xiaojukeji.kafka.manager.service.utils.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/4/3
 */
@Service("alarmRuleService")
public class AlarmRuleServiceImpl implements AlarmRuleService {
    private final static Logger logger = LoggerFactory.getLogger(AlarmRuleServiceImpl.class);

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    @Override
    public Result addAlarmRule(AlarmRuleDO alarmRuleDO) {
        try {
            alarmRuleDao.insert(alarmRuleDO);
        } catch (DuplicateKeyException e) {
            logger.info("addAlarmRule@AlarmRuleManagerServiceImpl, duplicate key, alarm rule:{}.", alarmRuleDO, e);
            return new Result(StatusCode.PARAM_ERROR, "duplicate alarm name");
        } catch (Exception e) {
            logger.error("addAlarmRule@AlarmRuleManagerServiceImpl, add failed, alarm rule:{}.", alarmRuleDO, e);
            return new Result(StatusCode.MY_SQL_INSERT_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
        return new Result();
    }

    @Override
    public Result deleteById(String operator, Long alarmRuleId) {
        try {
            AlarmRuleDO alarmRuleDO = alarmRuleDao.getById(alarmRuleId);
            if (alarmRuleDO == null) {
                return new Result(StatusCode.PARAM_ERROR, "param illegal, alarm rule not exist");
            }
            List<String> principalList = ListUtils.string2StrList(alarmRuleDO.getPrincipals());
            if (principalList == null || !principalList.contains(operator)) {
                return new Result(StatusCode.OPERATION_ERROR, "without authority to delete");
            }
            return alarmRuleDao.deleteById(alarmRuleId) > 0? new Result(): new Result(StatusCode.PARAM_ERROR, "alarm id illegal");
        } catch (Exception e) {
            logger.error("deleteById@AlarmRuleManagerServiceImpl, delete failed, alarmRuleId:{}.", alarmRuleId, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
    }

    @Override
    public Result updateById(String operator, AlarmRuleDO alarmRuleDO) {
        Result result = checkAlarmRuleDOIllegal(alarmRuleDO);
        if (!StatusCode.SUCCESS.equals(result.getCode())) {
            return result;
        }
        if (alarmRuleDO.getId() == null) {
            return new Result(StatusCode.PARAM_ERROR, "id is null");
        }
        try {
            AlarmRuleDO oldAlarmRuleDO = alarmRuleDao.getById(alarmRuleDO.getId());
            if (oldAlarmRuleDO == null) {
                return new Result(StatusCode.PARAM_ERROR, "param illegal, alarm rule not exist");
            }
            List<String> principalList = ListUtils.string2StrList(oldAlarmRuleDO.getPrincipals());
            if (principalList == null || !principalList.contains(operator)) {
                return new Result(StatusCode.OPERATION_ERROR, "without authority to delete");
            }
            if (alarmRuleDO.getStatus() == null) {
                alarmRuleDO.setStatus(oldAlarmRuleDO.getStatus());
            }
            return alarmRuleDao.updateById(alarmRuleDO)> 0? new Result(): new Result(StatusCode.PARAM_ERROR, "alarm id illegal");
        } catch (Exception e) {
            logger.error("updateById@AlarmRuleManagerServiceImpl, update failed, alarmRule:{}.", alarmRuleDO, e);
            return new Result(StatusCode.MY_SQL_UPDATE_ERROR, Constant.KAFKA_MANAGER_INNER_ERROR);
        }
    }

    @Override
    public AlarmRuleDO getById(Long id) {
        try {
            return alarmRuleDao.getById(id);
        } catch (Exception e) {
            logger.error("getById@AlarmRuleManagerServiceImpl, get failed, alarmId:{}.", id, e);
        }
        return null;
    }

    @Override
    public List<AlarmRuleDO> listAll() {
        try {
            return alarmRuleDao.listAll();
        } catch (Exception e) {
            logger.error("listAll@AlarmRuleManagerServiceImpl, list all failed.", e);
        }
        return null;
    }

    /**
     * 检查配置的参数是否合理
     */
    private Result checkAlarmRuleDOIllegal(AlarmRuleDO alarmRuleDO) {
        if (alarmRuleDO == null) {
            return new Result(StatusCode.PARAM_ERROR, "param empty");
        }
//        if (!RuleType.legal(alarmRuleDO.getRuleType())){
//            return new Result(StatusCode.PARAM_ERROR, "ruleType error");
//        }
//        if (!NotifyType.legal(alarmRuleDO.getNotifyType())){
//            return new Result(StatusCode.PARAM_ERROR, "notifyType error");
//        }
//        if (!MetricType.legal(alarmRuleDO.getMetricType())){
//            return new Result(StatusCode.PARAM_ERROR, "metricType error");
//        }
//        if (!ConditionType.legal(alarmRuleDO.getConditionType())){
//            return new Result(StatusCode.PARAM_ERROR, "conditionType error");
//        }
//        if (alarmRuleDO.getClusterId() == null) {
//            return new Result(StatusCode.PARAM_ERROR, "clusterId error");
//        }
        if (StringUtils.isEmpty(alarmRuleDO.getPrincipals())) {
            return new Result(StatusCode.PARAM_ERROR, "principals is empty");
        }
        return new Result();
    }
}