package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.dao.ClusterDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/7/26
 */
@Repository("clusterDao")
public class ClusterDaoImpl implements ClusterDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(ClusterDO clusterDO) {
        return sqlSession.insert("ClusterDao.insert", clusterDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("ClusterDao.deleteById", id);
    }

    @Override
    public int updateById(ClusterDO clusterDO) {
        return sqlSession.update("ClusterDao.updateById", clusterDO);
    }

    @Override
    public ClusterDO getById(Long id) {
        return sqlSession.selectOne("ClusterDao.getById", id);
    }

    @Override
    public List<ClusterDO> list() {
        return sqlSession.selectList("ClusterDao.list");
    }

    @Override
    public List<ClusterDO> listAll() {
        return sqlSession.selectList("ClusterDao.listAll");
    }
}