package com.xiaojukeji.kafka.manager.dao.gateway.impl;

import com.xiaojukeji.kafka.manager.dao.gateway.TopicConnectionDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicConnectionDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/7/6
 */
@Repository("topicConnectionDao")
public class TopicConnectionDaoImpl implements TopicConnectionDao {
    private final static Logger LOGGER = LoggerFactory.getLogger(TopicConnectionDaoImpl.class);

    @Autowired
    private SqlSessionTemplate sqlSession;

    @Override
    public int batchReplace(List<TopicConnectionDO> doList) {
        try {
            return sqlSession.insert("TopicConnectionDao.batchReplace", doList);
        } catch (DeadlockLoserDataAccessException e1) {
            return 0;
        } catch (Exception e) {
            LOGGER.error("add topic connections info failed", e);
        }
        return 0;
    }

    @Override
    public List<TopicConnectionDO> getByTopicName(Long clusterId,
                                                  String topicName,
                                                  Date startTime,
                                                  Date endTime) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("TopicConnectionDao.getByTopicName", params);
    }

    @Override
    public List<TopicConnectionDO> getByAppId(String appId, Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("appId", appId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("TopicConnectionDao.getByAppId", params);
    }
}