package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.po.OperationHistoryDO;
import com.xiaojukeji.kafka.manager.dao.OperationHistoryDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author arthur
 * @date 2017/7/20.
 */
@Repository("operationHistoryDao")
public class OperationHistoryDaoImpl implements OperationHistoryDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(OperationHistoryDO operationHistoryDO) {
        return sqlSession.insert("OperationHistoryDao.insert", operationHistoryDO);
    }
}
