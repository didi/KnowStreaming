package com.xiaojukeji.kafka.manager.dao.gateway.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.dao.gateway.AppDao;
import com.xiaojukeji.kafka.manager.task.Constant;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zengqiao
 * @date 20/7/28
 */
@Repository("appDao")
public class AppDaoImpl implements AppDao {
    @Autowired
    private SqlSessionTemplate sqlSession;

    /**
     * APP最近的一次更新时间, 更新之后的缓存
     */
    private static volatile long APP_CACHE_LATEST_UPDATE_TIME = Constant.START_TIMESTAMP;
    private static final Map<String, AppDO> APP_MAP = new ConcurrentHashMap<>();

    @Override
    public int insert(AppDO appDO) {
        return sqlSession.insert("AppDao.insert", appDO);
    }

    @Override
    public int deleteByName(String appName) {
        return sqlSession.delete("AppDao.deleteByName", appName);
    }

    @Override
    public List<AppDO> getByPrincipal(String principal) {
        return sqlSession.selectList("AppDao.getByPrincipal", principal);
    }

    @Override
    public AppDO getByName(String name) {
        return sqlSession.selectOne("AppDao.getByName", name);
    }

    @Override
    public AppDO getByAppId(String appId) {
        return sqlSession.selectOne("AppDao.getByAppId", appId);
    }

    @Override
    public List<AppDO> listAll() {
        updateTopicCache();
        return new ArrayList<>(APP_MAP.values());
    }

    @Override
    public int updateById(AppDO appDO) {
        return sqlSession.update("AppDao.updateById", appDO);
    }

    private void updateTopicCache() {
        long timestamp = System.currentTimeMillis();

        if (timestamp + 1000 <= APP_CACHE_LATEST_UPDATE_TIME) {
            // 近一秒内的请求不走db
            return;
        }

        Date afterTime = new Date(APP_CACHE_LATEST_UPDATE_TIME);
        List<AppDO> doList = sqlSession.selectList("AppDao.listAfterTime", afterTime);
        updateTopicCache(doList, timestamp);
    }

    /**
     * 更新APP缓存
     */
    private synchronized void updateTopicCache(List<AppDO> doList, long timestamp) {
        if (doList == null || doList.isEmpty() || APP_CACHE_LATEST_UPDATE_TIME >= timestamp) {
            // 本次无数据更新, 或者本次更新过时 时, 忽略本次更新
            return;
        }
        if (APP_CACHE_LATEST_UPDATE_TIME == Constant.START_TIMESTAMP) {
            APP_MAP.clear();
        }

        for (AppDO elem: doList) {
            APP_MAP.put(elem.getAppId(), elem);
        }
        APP_CACHE_LATEST_UPDATE_TIME = timestamp;
    }

    public static void resetCache() {
        APP_CACHE_LATEST_UPDATE_TIME = Constant.START_TIMESTAMP;
    }
}