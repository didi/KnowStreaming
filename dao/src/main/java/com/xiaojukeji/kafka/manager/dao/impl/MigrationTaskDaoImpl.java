package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.po.MigrationTaskDO;
import com.xiaojukeji.kafka.manager.dao.MigrationTaskDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * migrate topic task dao
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
@Repository("migrationTaskDao")
public class MigrationTaskDaoImpl implements MigrationTaskDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int addMigrationTask(MigrationTaskDO migrationTaskDO) {
        return sqlSession.insert("MigrationTaskDao.addMigrationTask", migrationTaskDO);
    }

    @Override
    public MigrationTaskDO getById(Long id) {
        return sqlSession.selectOne("MigrationTaskDao.getById", id);
    }

    @Override
    public List<MigrationTaskDO> listAll() {
        return sqlSession.selectList("MigrationTaskDao.listAll");
    }

    @Override
    public List<MigrationTaskDO> getByStatus(Integer status) {
        return sqlSession.selectList("MigrationTaskDao.getByStatus", status);
    }

    @Override
    public int updateById(Long id, Integer status, Long throttle) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);
        params.put("throttle", throttle);
        return sqlSession.update("MigrationTaskDao.updateById", params);
    }
}
