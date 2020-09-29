package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ReassignTaskDO;
import com.xiaojukeji.kafka.manager.dao.ReassignTaskDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * migrate topic task dao
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
@Repository("reassignTaskDao")
public class ReassignTaskDaoImpl implements ReassignTaskDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int batchCreate(List<ReassignTaskDO> doList) {
        return sqlSession.insert("ReassignTaskDao.batchCreate", doList);
    }

    @Override
    public List<ReassignTaskDO> getByTaskId(Long taskId) {
        return sqlSession.selectList("ReassignTaskDao.getByTaskId", taskId);
    }

    @Override
    public ReassignTaskDO getSubTask(Long subTaskId) {
        return sqlSession.selectOne("ReassignTaskDao.getSubTask", subTaskId);
    }

    @Override
    public List<ReassignTaskDO> listAll() {
        return sqlSession.selectList("ReassignTaskDao.listAll");
    }

    @Override
    public List<ReassignTaskDO> listAfterTime(Date gmtCreate) {
        return sqlSession.selectList("ReassignTaskDao.listAfterTime", gmtCreate);
    }

    @Override
    public int updateById(ReassignTaskDO reassignTaskDO) {
        return sqlSession.update("ReassignTaskDao.updateById", reassignTaskDO);
    }

    @Override
    @Transactional
    public void batchUpdate(List<ReassignTaskDO> doList) {
        for (ReassignTaskDO elem: doList) {
            updateById(elem);
        }
    }
}
