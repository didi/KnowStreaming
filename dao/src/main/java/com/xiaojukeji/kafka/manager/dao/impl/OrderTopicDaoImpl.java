package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.po.OrderTopicDO;
import com.xiaojukeji.kafka.manager.dao.OrderTopicDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/6/21
 */
@Repository("orderTopicDao")
public class OrderTopicDaoImpl implements OrderTopicDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(OrderTopicDO orderTopicDO) {
        return sqlSession.insert("OrderTopicDao.insert", orderTopicDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("OrderTopicDao.deleteById", id);
    }

    @Override
    public int updateById(OrderTopicDO orderTopicDO) {
        return sqlSession.update("OrderTopicDao.updateById", orderTopicDO);
    }

    @Override
    public OrderTopicDO getById(Long id) {
        return sqlSession.selectOne("OrderTopicDao.getById", id);
    }

    @Override
    public List<OrderTopicDO> list() {
        return sqlSession.selectList("OrderTopicDao.list");
    }

    @Override
    public List<OrderTopicDO> getByUsername(String username) {
        return sqlSession.selectList("OrderTopicDao.getByUsername", username);
    }
}