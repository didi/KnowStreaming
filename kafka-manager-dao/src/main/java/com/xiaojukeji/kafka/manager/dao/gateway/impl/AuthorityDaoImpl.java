package com.xiaojukeji.kafka.manager.dao.gateway.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.dao.gateway.AuthorityDao;
import com.xiaojukeji.kafka.manager.task.Constant;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhongyuankai
 * @date 2020/4/27
 */
@Repository("authorityDao")
public class AuthorityDaoImpl implements AuthorityDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    /**
     * Authority最近的一次更新时间, 更新之后的缓存
     * <AppID, <clusterId, <TopicName, AuthorityDO>>>
     */
    private static volatile long AUTHORITY_CACHE_LATEST_UPDATE_TIME = Constant.START_TIMESTAMP;

    private static final Map<String, Map<Long, Map<String, AuthorityDO>>> AUTHORITY_MAP = new ConcurrentHashMap<>();

    @Override
    public int insert(AuthorityDO authorityDO) {
        return sqlSession.insert("AuthorityDao.replace", authorityDO);
    }

    @Override
    public List<AuthorityDO> getAuthority(Long clusterId, String topicName, String appId) {
        Map<String, Object> params = new HashMap<>(3);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        params.put("appId", appId);
        return sqlSession.selectList("AuthorityDao.getAuthority", params);
    }

    @Override
    public List<AuthorityDO> getAuthorityByTopic(Long clusterId, String topicName) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        return sqlSession.selectList("AuthorityDao.getAuthorityByTopic", params);
    }

    @Override
    public List<AuthorityDO> getByAppId(String appId) {
        updateAuthorityCache();
        Map<Long, Map<String, AuthorityDO>> doMap = AUTHORITY_MAP.get(appId);
        if (doMap == null) {
            return new ArrayList<>();
        }

        List<AuthorityDO> authorityDOList = new ArrayList<>();
        for (Map.Entry<Long, Map<String, AuthorityDO>> entry: doMap.entrySet()) {
            authorityDOList.addAll(entry.getValue().values());
        }
        return authorityDOList;
    }

    @Override
    public List<AuthorityDO> listAll() {
        updateAuthorityCache();
        List<AuthorityDO> authorityDOList = new ArrayList<>();
        for (String appId: AUTHORITY_MAP.keySet()) {
            Map<Long, Map<String, AuthorityDO>> doMap = AUTHORITY_MAP.get(appId);
            for (Long clusterId: doMap.keySet()) {
                authorityDOList.addAll(doMap.get(clusterId).values());
            }
        }
        return authorityDOList;
    }

    @Override
    public Map<String, Map<Long, Map<String, AuthorityDO>>> getAllAuthority() {
        updateAuthorityCache();
        return AUTHORITY_MAP;
    }

    @Override
    public int deleteAuthorityByTopic(Long clusterId, String topicName) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("clusterId", clusterId);
        params.put("topicName", topicName);
        return sqlSession.delete("AuthorityDao.deleteByTopic", params);
    }


    private void updateAuthorityCache() {
        Long timestamp = System.currentTimeMillis();

        if (timestamp + 1000 <= AUTHORITY_CACHE_LATEST_UPDATE_TIME) {
            // 近一秒内的请求不走db
            return;
        }

        Date afterTime = new Date(AUTHORITY_CACHE_LATEST_UPDATE_TIME);
        List<AuthorityDO> doList = sqlSession.selectList("AuthorityDao.listAfterTime", afterTime);
        updateAuthorityCache(doList, timestamp);
    }

    /**
     * 更新Topic缓存
     */
    private synchronized void updateAuthorityCache(List<AuthorityDO> doList, Long timestamp) {
        if (doList == null || doList.isEmpty() || AUTHORITY_CACHE_LATEST_UPDATE_TIME >= timestamp) {
            // 本次无数据更新, 或者本次更新过时 时, 忽略本次更新
            return;
        }
        if (AUTHORITY_CACHE_LATEST_UPDATE_TIME == Constant.START_TIMESTAMP) {
            AUTHORITY_MAP.clear();
        }

        for (AuthorityDO elem: doList) {
            Map<Long, Map<String, AuthorityDO>> doMap =
                    AUTHORITY_MAP.getOrDefault(elem.getAppId(), new ConcurrentHashMap<>());
            Map<String, AuthorityDO> subDOMap = doMap.getOrDefault(elem.getClusterId(), new ConcurrentHashMap<>());
            subDOMap.put(elem.getTopicName(), elem);
            doMap.put(elem.getClusterId(), subDOMap);
            AUTHORITY_MAP.put(elem.getAppId(), doMap);
        }
        AUTHORITY_CACHE_LATEST_UPDATE_TIME = timestamp;
    }

    public static void resetCache() {
        AUTHORITY_CACHE_LATEST_UPDATE_TIME = Constant.START_TIMESTAMP;
    }
}
