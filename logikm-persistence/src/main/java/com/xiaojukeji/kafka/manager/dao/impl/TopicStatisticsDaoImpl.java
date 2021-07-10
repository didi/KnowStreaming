package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;
import com.xiaojukeji.kafka.manager.dao.TopicStatisticsDao;
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
@Repository("topicStatisticsDao")
public class TopicStatisticsDaoImpl implements TopicStatisticsDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int replace(TopicStatisticsDO topicStatisticsDO) {
        return sqlSession.insert("TopicStatisticsDao.replace", topicStatisticsDO);
    }

    @Override
    public TopicStatisticsDO getByTopicAndDay(Long clusterId, String topicName, String gmtDay) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("gmtDay", gmtDay);
        return sqlSession.selectOne("TopicStatisticsDao.getByTopicAndDay", params);
    }

    @Override
    public List<TopicStatisticsDO> getTopicStatistic(Long clusterId, String topicName, Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("TopicStatisticsDao.getTopicStatistic", params);

    }

    @Override
    public List<TopicStatisticsDO> getTopicStatisticData(Long clusterId, Date startTime, Double minMaxAvgBytesIn) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("clusterId", clusterId);
        params.put("startTime", startTime);
        params.put("minMaxAvgBytesIn", minMaxAvgBytesIn);
        return sqlSession.selectList("TopicStatisticsDao.getTopicStatisticData", params);
    }

    @Override
    public Double getTopicMaxAvgBytesIn(Long clusterId, String topicName, Date startTime, Date endTime, Integer maxAvgDay) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("maxAvgDay", maxAvgDay);
        return sqlSession.selectOne("TopicStatisticsDao.getTopicMaxAvgBytesIn", params);
    }

    @Override
    public int deleteBeforeTime(Date endTime) {
        return sqlSession.delete("TopicStatisticsDao.deleteBeforeTime", endTime);
    }
}