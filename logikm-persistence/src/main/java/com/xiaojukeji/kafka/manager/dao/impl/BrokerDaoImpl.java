package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.BrokerDO;
import com.xiaojukeji.kafka.manager.dao.BrokerDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao_cn@163.com
 * @date 19/4/21
 */
@Repository("brokerDao")
public class BrokerDaoImpl implements BrokerDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int replace(BrokerDO brokerDO) {
        return sqlSession.insert("BrokerDao.replace", brokerDO);
    }

    @Override
    public int deleteById(Long clusterId, Integer brokerId) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("brokerId", brokerId);
        return sqlSession.delete("BrokerDao.deleteById", params);
    }

    @Override
    public List<BrokerDO> getDead(Long clusterId) {
        return sqlSession.selectList("BrokerDao.getDead", clusterId);
    }

    @Override
    public List<BrokerDO> listAll() {
        return sqlSession.selectList("BrokerDao.listAll");
    }

    @Override
    public List<BrokerDO> getByClusterId(Long clusterId) {
        return sqlSession.selectList("BrokerDao.getByClusterId", clusterId);
    }
}
