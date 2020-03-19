package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.po.AlarmRuleDO;
import com.xiaojukeji.kafka.manager.common.entity.po.query.AlarmRuleQueryOption;
import com.xiaojukeji.kafka.manager.dao.AlarmRuleDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/6/23
 */
@Repository("alarmRuleDao")
public class AlarmRuleDaoImpl implements AlarmRuleDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(AlarmRuleDO alarmRuleDO) {
        return sqlSession.insert("AlarmRuleDao.insert", alarmRuleDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("AlarmRuleDao.deleteById", id);
    }

    @Override
    public int updateById(AlarmRuleDO alarmRuleDO) {
        return sqlSession.update("AlarmRuleDao.updateById", alarmRuleDO);
    }

    @Override
    public AlarmRuleDO getById(Long id) {
        AlarmRuleQueryOption alarmRuleQueryOption = new AlarmRuleQueryOption();
        alarmRuleQueryOption.setId(id);
        List<AlarmRuleDO> alarmRuleDOList = sqlSession.selectList("AlarmRuleDao.getByOption", alarmRuleQueryOption);
        if (alarmRuleDOList == null || alarmRuleDOList.isEmpty()) {
            return null;
        }
        return alarmRuleDOList.get(0);
    }

    @Override
    public List<AlarmRuleDO> listAll() {
        return sqlSession.selectList("AlarmRuleDao.getByOption", new AlarmRuleQueryOption());
    }
}