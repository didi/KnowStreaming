package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.po.OrderPartitionDO;
import com.xiaojukeji.kafka.manager.dao.OrderPartitionDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/6/21
 */
@Repository("orderPartitionDao")
public class OrderPartitionDaoImpl implements OrderPartitionDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(OrderPartitionDO orderPartitionDO) {
        return sqlSession.insert("OrderPartitionDao.insert", orderPartitionDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("OrderPartitionDao.deleteById", id);
    }

    @Override
    public int updateById(OrderPartitionDO orderPartitionDO) {
        return sqlSession.update("OrderPartitionDao.updateById", orderPartitionDO);
    }

    @Override
    public OrderPartitionDO getById(Long id) {
        return sqlSession.selectOne("OrderPartitionDao.getById", id);
    }

    @Override
    public List<OrderPartitionDO> list() {
        return sqlSession.selectList("OrderPartitionDao.list");
    }
}