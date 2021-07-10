package com.xiaojukeji.kafka.manager.dao.gateway.impl;

import com.xiaojukeji.kafka.manager.dao.gateway.GatewayConfigDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.GatewayConfigDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/7/28
 */
@Repository("gatewayConfigDao")
public class GatewayConfigDaoImpl implements GatewayConfigDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<GatewayConfigDO> getByConfigType(String configType) {
        return sqlSession.selectList("GatewayConfigDao.getByConfigType", configType);
    }

    @Override
    public GatewayConfigDO getByConfigTypeAndName(String configType, String configName) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("configType", configType);
        params.put("configName", configName);
        return sqlSession.selectOne("GatewayConfigDao.getByConfigTypeAndName", params);
    }

    @Override
    public List<GatewayConfigDO> list() {
        return sqlSession.selectList("GatewayConfigDao.list");
    }

    @Override
    public int insert(GatewayConfigDO gatewayConfigDO) {
        return sqlSession.insert("GatewayConfigDao.insert", gatewayConfigDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("GatewayConfigDao.deleteById", id);
    }

    @Override
    public int updateById(GatewayConfigDO gatewayConfigDO) {
        return sqlSession.update("GatewayConfigDao.updateById", gatewayConfigDO);
    }

    @Override
    public GatewayConfigDO getById(Long id) {
        return sqlSession.selectOne("GatewayConfigDao.getById", id);
    }
}