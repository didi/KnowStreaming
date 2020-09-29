package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.MonitorRuleDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.MonitorRuleDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/5/21
 */
@Repository("monitorRuleDao")
public class MonitorRuleDaoImpl implements MonitorRuleDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(MonitorRuleDO monitorRuleDO) {
        return sqlSession.insert("MonitorRuleDao.insert", monitorRuleDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("MonitorRuleDao.deleteById", id);
    }

    @Override
    public int updateById(Long id, String name, String appId, String operator) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("id", id);
        params.put("appId", appId);
        params.put("name", name);
        params.put("operator", operator);
        return sqlSession.update("MonitorRuleDao.updateById", params);
    }

    @Override
    public MonitorRuleDO getById(Long id) {
        return sqlSession.selectOne("MonitorRuleDao.getById", id);
    }

    @Override
    public MonitorRuleDO getByStrategyId(Long strategyId) {
        return sqlSession.selectOne("MonitorRuleDao.getByStrategyId", strategyId);
    }

    @Override
    public List<MonitorRuleDO> listAll() {
        return sqlSession.selectList("MonitorRuleDao.listAll");
    }
}