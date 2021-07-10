package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ControllerDO;
import com.xiaojukeji.kafka.manager.dao.ControllerDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/7/15
 */
@Repository("controllerDao")
public class ControllerDaoImpl implements ControllerDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(ControllerDO controllerDO) {
        return sqlSession.insert("ControllerDao.insert", controllerDO);
    }

    @Override
    public List<ControllerDO> getByClusterId(Long clusterId) {
        return sqlSession.selectList("ControllerDao.getByClusterId", clusterId);
    }
}