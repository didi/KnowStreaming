package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicExpiredDO;
import com.xiaojukeji.kafka.manager.dao.TopicExpiredDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/3/30
 */
@Repository("topicExpiredDao")
public class TopicExpiredDaoImpl implements TopicExpiredDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<TopicExpiredDO> getExpiredTopics(Integer expiredDay) {
        return sqlSession.selectList("TopicExpiredDao.getExpiredTopics", expiredDay);
    }

    @Override
    public int modifyTopicExpiredTime(Long clusterId, String topicName, Date gmtRetain) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("gmtRetain", gmtRetain);
        return sqlSession.update("TopicExpiredDao.modifyTopicExpiredTime", params);
    }

    @Override
    public int replace(TopicExpiredDO expiredDO) {
        return sqlSession.update("TopicExpiredDao.replace", expiredDO);
    }

    @Override
    public TopicExpiredDO getByTopic(Long clusterId, String topicName) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        return sqlSession.selectOne("TopicExpiredDao.getByTopic", params);
    }
}