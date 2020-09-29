package com.xiaojukeji.kafka.manager.dao.gateway.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.TopicReportDO;
import com.xiaojukeji.kafka.manager.dao.gateway.TopicReportDao;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/7/29
 */
@Repository("topicReportDao")
public class TopicReportDaoImpl implements TopicReportDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int replace(TopicReportDO topicReportDO) {
        return sqlSession.insert("TopicReportDao.replace", topicReportDO);
    }

    @Override
    public List<TopicReportDO> getNeedReportTopic(Long clusterId) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("now", new Date());
        return sqlSession.selectList("TopicReportDao.getNeedReportTopic", params);
    }
}