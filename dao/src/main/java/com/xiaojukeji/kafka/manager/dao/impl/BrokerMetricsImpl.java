package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.metrics.BrokerMetrics;
import com.xiaojukeji.kafka.manager.dao.BrokerMetricsDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tukun
 * @date 2015/11/6.
 */
@Repository("brokerMetricsDao")
public class BrokerMetricsImpl implements BrokerMetricsDao {

    @Autowired
    private SqlSessionTemplate sqlSession;

    @Override
    public int batchAdd(List<BrokerMetrics> brokerMetricsList) {
        return sqlSession.insert("BrokerMetricsDao.batchAdd", brokerMetricsList);
    }

    @Override
    public List<BrokerMetrics> getBrokerMetricsByInterval(Long clusterId,
                                                          Integer brokerId,
                                                          Date startTime,
                                                          Date endTime) {
        Map<String, Object> params = new HashMap<>();
        params.put("clusterId", clusterId);
        params.put("brokerId", brokerId);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return sqlSession.selectList("BrokerMetricsDao.getBrokerMetricsByInterval", params);
    }

    @Override
    public int deleteBeforeTime(Date endTime) {
        return sqlSession.delete("BrokerMetricsDao.deleteBeforeTime", endTime);
    }
}
