package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.LogicalClusterDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.LogicalClusterDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/6/28
 */
@Repository("logicalClusterDao")
public class LogicalClusterDaoImpl implements LogicalClusterDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(LogicalClusterDO logicalClusterDO) {
        return sqlSession.insert("LogicalClusterDao.insert", logicalClusterDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("LogicalClusterDao.deleteById", id);
    }

    @Override
    public int updateById(LogicalClusterDO logicalClusterDO) {
        return sqlSession.update("LogicalClusterDao.updateById", logicalClusterDO);
    }

    @Override
    public LogicalClusterDO getById(Long id) {
        return sqlSession.selectOne("LogicalClusterDao.getById", id);
    }

    @Override
    public List<LogicalClusterDO> getByClusterId(Long clusterId) {
        return sqlSession.selectList("LogicalClusterDao.getByClusterId", clusterId);
    }

    @Override
    public List<LogicalClusterDO> listAll() {
        return sqlSession.selectList("LogicalClusterDao.listAll");
    }
}