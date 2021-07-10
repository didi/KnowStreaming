package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.HeartbeatDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.HeartbeatDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/10
 */
@Repository("heartbeatDao")
public class HeartbeatDaoImpl implements HeartbeatDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int replace(HeartbeatDO heartbeatDO) {
        return sqlSession.insert("HeartbeatDao.replace", heartbeatDO);
    }

    @Override
    public List<HeartbeatDO> selectActiveHosts(Date afterTime) {
        return sqlSession.selectList("HeartbeatDao.selectActiveHosts", afterTime);
    }
}