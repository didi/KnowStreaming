package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.RegionDO;
import com.xiaojukeji.kafka.manager.dao.RegionDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/6/23
 */
@Repository("regionDao")
public class RegionDaoImpl implements RegionDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(RegionDO regionDO) {
        return sqlSession.insert("RegionDao.insert", regionDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("RegionDao.deleteById", id);
    }

    @Override
    public int updateById(RegionDO regionDO) {
        return sqlSession.update("RegionDao.updateById", regionDO);
    }

    @Override
    public int updateCapacityById(RegionDO regionDO) {
        return sqlSession.update("RegionDao.updateCapacityById", regionDO);
    }

    @Override
    public RegionDO getById(Long id) {
        return sqlSession.selectOne("RegionDao.getById", id);
    }

    @Override
    public List<RegionDO> getByClusterId(Long clusterId) {
        return sqlSession.selectList("RegionDao.getByClusterId", clusterId);
    }

    @Override
    public List<RegionDO> listAll() {
        return sqlSession.selectList("RegionDao.listAll");
    }
}