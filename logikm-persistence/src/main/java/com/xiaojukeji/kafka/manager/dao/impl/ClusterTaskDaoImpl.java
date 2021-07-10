package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.ClusterTaskDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterTaskDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/5/19
 */
@Repository("clusterTaskDao")
public class ClusterTaskDaoImpl implements ClusterTaskDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(ClusterTaskDO clusterTaskDO) {
        return sqlSession.insert("ClusterTaskDao.insert", clusterTaskDO);
    }

    @Override
    public ClusterTaskDO getById(Long id) {
        return sqlSession.selectOne("ClusterTaskDao.getById", id);
    }

    @Override
    public List<ClusterTaskDO> listAll() {
        return sqlSession.selectList("ClusterTaskDao.listAll");
    }

    @Override
    public int updateTaskState(Long taskId, Integer taskStatus) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("taskId", taskId);
        params.put("taskStatus", taskStatus);
        return sqlSession.update("ClusterTaskDao.updateTaskState", params);
    }

    @Override
    public int updateRollback(ClusterTaskDO clusterTaskDO) {
        return sqlSession.update("ClusterTaskDao.updateRollback", clusterTaskDO);
    }
}