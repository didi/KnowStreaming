package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ConfigDO;
import com.xiaojukeji.kafka.manager.dao.ConfigDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/19
 */
@Repository("configDao")
public class ConfigDaoImpl implements ConfigDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(ConfigDO configDO) {
        return sqlSession.insert("ConfigDao.insert", configDO);
    }

    @Override
    public int deleteByKey(String configKey) {
        return sqlSession.delete("ConfigDao.deleteByKey", configKey);
    }

    @Override
    public int updateByKey(ConfigDO configDO) {
        return sqlSession.update("ConfigDao.updateByKey", configDO);
    }

    @Override
    public ConfigDO getByKey(String configKey) {
        return sqlSession.selectOne("ConfigDao.getByKey", configKey);
    }

    @Override
    public List<ConfigDO> listAll() {
        return sqlSession.selectList("ConfigDao.listAll");
    }
}