package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.dao.ClusterMetricsDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("clusterMetricDao")
public class ClusterMetricsDaoImpl implements ClusterMetricsDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int batchAdd(List<ClusterMetricsDO> clusterMetricsList) {
        return sqlSession.insert("ClusterMetricsDao.batchAdd", clusterMetricsList);
    }

    @Override
    public List<ClusterMetricsDO> getClusterMetrics(long clusterId, Date startTime, Date endTime) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("clusterId", clusterId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return sqlSession.selectList("ClusterMetricsDao.getClusterMetrics", map);
    }

    @Override
    public int deleteBeforeTime(Date endTime) {
        return sqlSession.delete("ClusterMetricsDao.deleteBeforeTime", endTime);
    }
}
