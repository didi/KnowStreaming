package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.dao.TopicDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 19/7/12
 */
@Repository("TopicDao")
public class TopicDaoImpl implements TopicDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    @Autowired
    private KafkaManagerProperties kafkaManagerProperties;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int replace(TopicDO topicDO) {
        if (kafkaManagerProperties.hasPG()) {
            return sqlSession.insert("TopicDao.replaceOnPG", topicDO);
        }
        return sqlSession.insert("TopicDao.replace", topicDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("TopicDao.deleteById", id);
    }

    @Override
    public int deleteByName(Long clusterId, String topicName) {
        Map<String, Object> params = new HashMap<>();
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        return sqlSession.delete("TopicDao.deleteByName", params);
    }

    @Override
    public TopicDO getByTopicName(Long clusterId, String topicName) {
        Map<String, Object> params = new HashMap<>();
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        return sqlSession.selectOne("TopicDao.getByTopicName", params);
    }

    @Override
    public List<TopicDO> getByClusterId(Long clusterId) {
        return sqlSession.selectList("TopicDao.getByClusterId", clusterId);
    }

    @Override
    public List<TopicDO> list() {
        return sqlSession.selectList("TopicDao.list");
    }
}