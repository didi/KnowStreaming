package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.KafkaBillDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.KafkaBillDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/5/12
 */
@Repository("kafkaBillDao")
public class KafkaBillDaoImpl implements KafkaBillDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int replace(KafkaBillDO kafkaBillDO) {
        return sqlSession.insert("KafkaBillDao.replace", kafkaBillDO);
    }

    @Override
    public List<KafkaBillDO> getByTopicName(Long clusterId, String topicName, Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("KafkaBillDao.getByTopicName", params);
    }

    @Override
    public List<KafkaBillDO> getByPrincipal(String principal, Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("principal", principal);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("KafkaBillDao.getByPrincipal", params);
    }

    @Override
    public List<KafkaBillDO> getByTimeBetween(Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("KafkaBillDao.getByTimeBetween", params);
    }

    @Override
    public List<KafkaBillDO> getByGmtDay(String gmtDay) {
        return sqlSession.selectList("KafkaBillDao.getByGmtDay", gmtDay);
    }
}