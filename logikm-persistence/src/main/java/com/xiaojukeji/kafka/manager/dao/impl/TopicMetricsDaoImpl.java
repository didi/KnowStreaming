package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.TopicMetricsDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author zhongyuankai
 * @date 20/4/3
 */
@Repository("topicMetricDao")
public class TopicMetricsDaoImpl implements TopicMetricsDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int batchAdd(List<TopicMetricsDO> metricsList) {
        return sqlSession.insert("TopicMetricsDao.batchAdd", metricsList);
    }

    @Override
    public List<TopicMetricsDO> getTopicMetrics(Long clusterId, String topicName, Date startTime, Date endTime) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("TopicMetricsDao.getTopicMetrics", params);
    }

    @Override
    public List<TopicMetricsDO> getLatestTopicMetrics(Long clusterId, Date afterTime) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("afterTime", afterTime);
        List<TopicMetricsDO> metricsDOList =
                sqlSession.selectList("TopicMetricsDao.getLatestTopicMetrics", params);
        if (metricsDOList == null) {
            return new ArrayList<>();
        }

        Map<String, TopicMetricsDO> metricsMap = new HashMap<>(metricsDOList.size() / 2);
        for (TopicMetricsDO elem: metricsDOList) {
            TopicMetricsDO metricsDO = metricsMap.get(elem.getTopicName());
            if (metricsDO == null) {
                metricsMap.put(elem.getTopicName(), elem);
            } else if (metricsDO.getGmtCreate().getTime() <= elem.getGmtCreate().getTime()) {
                metricsMap.put(elem.getTopicName(), elem);
            }
        }
        return new ArrayList<>(metricsMap.values());
    }

    @Override
    public int deleteBeforeTime(Date endTime) {
        return sqlSession.delete("TopicMetricsDao.deleteBeforeTime", endTime);
    }
}
