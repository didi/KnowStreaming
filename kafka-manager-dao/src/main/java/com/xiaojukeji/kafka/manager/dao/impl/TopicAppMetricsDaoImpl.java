package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.dao.TopicAppMetricsDao;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/4/2
 */
@Repository("topicAppIdMetricDao")
public class TopicAppMetricsDaoImpl implements TopicAppMetricsDao {

    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int batchAdd(List<TopicMetricsDO> doList) {
        return sqlSession.insert("TopicAppMetricsDao.batchAdd", doList);
    }

    @Override
    public List<TopicMetricsDO> getTopicAppMetrics(Long clusterId,
                                                   String topicName,
                                                   String appId,
                                                   Date startTime,
                                                   Date endTime) {
        Map<String, Object> map = new HashMap<>(5);
        map.put("clusterId", clusterId);
        map.put("topicName", topicName);
        map.put("appId", appId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return sqlSession.selectList("TopicAppMetricsDao.getTopicAppMetrics", map);
    }

    @Override
    public int deleteBeforeTime(Date endTime) {
        return sqlSession.delete("TopicAppMetricsDao.deleteBeforeTime", endTime);
    }
}
