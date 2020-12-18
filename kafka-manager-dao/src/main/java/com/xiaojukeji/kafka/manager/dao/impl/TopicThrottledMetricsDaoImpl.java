package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicThrottledMetricsDO;
import com.xiaojukeji.kafka.manager.dao.TopicThrottledMetricsDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author zhongyuankai
 * @date 20/4/3
 */
@Repository("topicThrottledMetricsDao")
public class TopicThrottledMetricsDaoImpl implements TopicThrottledMetricsDao {

    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insertBatch(List<TopicThrottledMetricsDO> topicThrottleDOList) {
        return sqlSession.insert("TopicThrottledMetricsDao.insertBatch", topicThrottleDOList);
    }

    @Override
    public List<TopicThrottledMetricsDO> getTopicThrottle(long clusterId, String topicName, String appId, Date startTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("clusterId", clusterId);
        map.put("topicName", topicName);
        map.put("appId", appId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return sqlSession.selectList("TopicThrottledMetricsDao.getTopicThrottle", map);
    }

    @Override
    public List<TopicThrottledMetricsDO> getAppIdThrottle(long clusterId, String appId, Date startTime, Date endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("clusterId", clusterId);
        map.put("appId", appId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return sqlSession.selectList("TopicThrottledMetricsDao.getAppIdThrottle", map);
    }

    @Override
    public List<TopicThrottledMetricsDO> getLatestTopicThrottledMetrics(Long clusterId, Date afterTime) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("afterTime", afterTime);
        List<TopicThrottledMetricsDO> doList =
                sqlSession.selectList("TopicThrottledMetricsDao.getLatestTopicThrottledMetrics", params);
        if (doList == null) {
            return new ArrayList<>();
        }
        Map<String, TopicThrottledMetricsDO> throttleMap = new HashMap<>(doList.size() / 2);
        for (TopicThrottledMetricsDO elem: doList) {
            String key = new StringBuilder()
                    .append(elem.getClusterId())
                    .append(elem.getTopicName())
                    .append(elem.getAppId()).toString();
            TopicThrottledMetricsDO throttleDO = throttleMap.get(key);
            if (throttleDO == null) {
                throttleMap.put(key, elem);
            } else if (throttleDO.getGmtCreate().getTime() < elem.getGmtCreate().getTime()) {
                throttleMap.put(key, throttleDO);
            }
        }
        return new ArrayList<>(throttleMap.values());
    }

    @Override
    public int deleteBeforeTime(Date endTime) {
        return sqlSession.delete("TopicThrottledMetricsDao.deleteBeforeTime", endTime);
    }
}
