package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.OrderDO;
import com.xiaojukeji.kafka.manager.dao.OrderDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongyuankai
 * @date 2020/4/23
 */
@Repository("orderDao")
public class OrderDaoImpl implements OrderDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int directSaveHandledOrder(OrderDO orderDO) {
        return sqlSession.insert("OrderDao.directSaveHandled", orderDO);
    }

    @Override
    public int insert(OrderDO orderDO) {
        return sqlSession.insert("OrderDao.insert", orderDO);
    }

    @Override
    public OrderDO getById(Long id) {
        return sqlSession.selectOne("OrderDao.getById", id);
    }

    @Override
    public List<OrderDO> list() {
        return sqlSession.selectList("OrderDao.list");
    }

    @Override
    public int updateOrderStatusById(Long id, Integer status) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("id", id);
        map.put("status", status);
        return sqlSession.update("OrderDao.updateOrderStatusById", map);
    }

    @Override
    public int updateOrderById(OrderDO orderDO) {
        return sqlSession.update("OrderDao.updateOrderById", orderDO);
    }

    @Override
    public List<OrderDO> getByApplicantAndStatus(String applicant, Integer status) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("applicant", applicant);
        map.put("status", status);
        return sqlSession.selectList("OrderDao.getByApplicantAndStatus", map);
    }

    @Override
    public List<OrderDO> getByApproverAndStatus(String approver, Integer status) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("approver", approver);
        map.put("status", status);
        return sqlSession.selectList("OrderDao.getByApproverAndStatus", map);
    }

    @Override
    public List<OrderDO> getByStatus(Integer status) {
        return sqlSession.selectList("OrderDao.getByStatus", status);
    }

    @Override
    public List<OrderDO> getByGmtHandle(Date startTime) {
        return sqlSession.selectList("OrderDao.getByGmtHandle", startTime);
    }

    @Override
    public int updateExtensionsById(OrderDO orderDO) {
        return sqlSession.update("OrderDao.updateExtensionsById", orderDO);
    }

    @Override
    public List<OrderDO> getByHandleTime(Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("OrderDao.getByHandleTime", params);
    }
}
