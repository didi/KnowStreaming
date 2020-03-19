package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * dao实现
 * @author tukun
 * @date 2015/11/11.
 */
@Repository("topicMetricDao")
public class TopicMetricsDaoImpl implements TopicMetricsDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int batchAdd(List<TopicMetrics> topicMetricsList) {
        return sqlSession.insert("TopicMetricsDao.batchAdd", topicMetricsList);
    }

    @Override
    public List<TopicMetrics> getTopicMetricsByInterval(Long clusterId,
                                                        String topicName,
                                                        Date startTime,
                                                        Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("clusterId", clusterId);
        map.put("topicName", topicName);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return sqlSession.selectList("TopicMetricsDao.getTopicMetricsByInterval", map);
    }

    @Override
    public int deleteBeforeTime(Date endTime) {
        return sqlSession.delete("TopicMetricsDao.deleteBeforeTime", endTime);
    }
}
