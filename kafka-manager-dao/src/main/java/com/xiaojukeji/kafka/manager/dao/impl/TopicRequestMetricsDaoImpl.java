package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.TopicRequestMetricsDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongyuankai
 * @date 20/4/7
 */
@Repository("topicRequestMetricsDAO")
public class TopicRequestMetricsDaoImpl implements TopicRequestMetricsDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int batchAdd(List<TopicMetricsDO> metricsDOList) {
        return sqlSession.insert("TopicRequestMetricsDao.batchAdd", metricsDOList);
    }

    @Override
    public int add(TopicMetricsDO metricsDO) {
        return sqlSession.insert("TopicRequestMetricsDao.add", metricsDO);
    }

    @Override
    public List<TopicMetricsDO> selectByTime(Long clusterId, String topicName, Date startTime, Date endTime) {
        Map<String, Object> param = new HashMap<>();
        param.put("clusterId", clusterId);
        param.put("topicName", topicName);
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        return sqlSession.selectList("TopicRequestMetricsDao.selectByTime", param);
    }

    @Override
    public int deleteBeforeTime(Date endTime, Integer limitSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("endTime", endTime);
        params.put("limitSize", limitSize);
        return sqlSession.delete("TopicRequestMetricsDao.deleteBeforeTime", params);
    }

    @Override
    public List<TopicMetricsDO> getById(Long startId, Long endId) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("startId", startId);
        params.put("endId", endId);
        return sqlSession.selectList("TopicRequestMetricsDao.getById", params);
    }
}