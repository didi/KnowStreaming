package com.xiaojukeji.kafka.manager.dao.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.dao.TopicDao;
import com.xiaojukeji.kafka.manager.task.Constant;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengqiao
 * @date 19/7/12
 */
@Repository("TopicDao")
public class TopicDaoImpl implements TopicDao {
    /**
     * Topic最近的一次更新时间, 更新之后的缓存
     */
    private static volatile long TOPIC_CACHE_LATEST_UPDATE_TIME = Constant.START_TIMESTAMP;

    private static final Map<Long, Map<String, TopicDO>> TOPIC_MAP = new ConcurrentHashMap<>();

    @Autowired
    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public int insert(TopicDO topicDO) {
        return sqlSession.insert("TopicDao.insert", topicDO);
    }

    @Override
    public int deleteById(Long id) {
        return sqlSession.delete("TopicDao.deleteById", id);
    }

    @Override
    public int deleteByName(Long clusterId, String topicName) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        return sqlSession.delete("TopicDao.deleteByName", params);
    }

    @Override
    public int updateByName(TopicDO topicDO) {
        return sqlSession.update("TopicDao.updateByName", topicDO);
    }

    @Override
    public TopicDO getByTopicName(Long clusterId, String topicName) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        return sqlSession.selectOne("TopicDao.getByTopicName", params);
    }

    @Override
    public List<TopicDO> getByClusterIdFromCache(Long clusterId) {
        updateTopicCache();
        return new ArrayList<>(TOPIC_MAP.getOrDefault(clusterId, Collections.emptyMap()).values());
    }

    @Override
    public List<TopicDO> getByClusterId(Long clusterId) {
        return sqlSession.selectList("TopicDao.getByClusterId", clusterId);
    }

    @Override
    public List<TopicDO> getByAppId(String appId) {
        return sqlSession.selectList("TopicDao.getByAppId", appId);
    }

    @Override
    public List<TopicDO> listAll() {
        updateTopicCache();
        List<TopicDO> doList = new ArrayList<>();
        for (Long clusterId: TOPIC_MAP.keySet()) {
            doList.addAll(TOPIC_MAP.getOrDefault(clusterId, Collections.emptyMap()).values());
        }
        return doList;
    }

    @Override
    public TopicDO getTopic(Long clusterId, String topicName, String appId) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("appId", appId);
        return sqlSession.selectOne("TopicDao.getTopic", params);
    }

    private void updateTopicCache() {
        long timestamp = System.currentTimeMillis();

        if (timestamp + 1000 <= TOPIC_CACHE_LATEST_UPDATE_TIME) {
            // 近一秒内的请求不走db
            return;
        }

        Date afterTime = new Date(TOPIC_CACHE_LATEST_UPDATE_TIME);
        List<TopicDO> doList = sqlSession.selectList("TopicDao.listAfterTime", afterTime);
        updateTopicCache(doList, timestamp);
    }

    /**
     * 更新Topic缓存
     */
    private synchronized void updateTopicCache(List<TopicDO> doList, Long timestamp) {
        if (TOPIC_CACHE_LATEST_UPDATE_TIME == Constant.START_TIMESTAMP) {
            TOPIC_MAP.clear();
        }

        if (doList == null || doList.isEmpty() || TOPIC_CACHE_LATEST_UPDATE_TIME >= timestamp) {
            // 本次无数据更新, 或者本次更新过时 时, 忽略本次更新
            return;
        }

        for (TopicDO elem: doList) {
            Map<String, TopicDO> doMap = TOPIC_MAP.getOrDefault(elem.getClusterId(), new ConcurrentHashMap<>());
            doMap.put(elem.getTopicName(), elem);
            TOPIC_MAP.put(elem.getClusterId(), doMap);
        }
        TOPIC_CACHE_LATEST_UPDATE_TIME = timestamp;
    }

    public static void resetCache() {
        TOPIC_CACHE_LATEST_UPDATE_TIME = Constant.START_TIMESTAMP;
    }
}