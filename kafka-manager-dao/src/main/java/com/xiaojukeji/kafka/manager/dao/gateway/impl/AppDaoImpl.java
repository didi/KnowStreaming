package com.xiaojukeji.kafka.manager.dao.gateway.impl;

import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AppDO;
import com.xiaojukeji.kafka.manager.dao.gateway.AppDao;
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
    private static Long APP_CACHE_LATEST_UPDATE_TIME = 0L;
    private static final Map<String, AppDO> APP_MAP = new ConcurrentHashMap<>();

    @Override
    public int insert(AppDO appDO) {
        return sqlSession.insert("AppDao.insert", appDO);
    }

    @Override
    public int insertIgnoreGatewayDB(AppDO appDO) {
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
        Long timestamp = System.currentTimeMillis();

        Date afterTime = new Date(APP_CACHE_LATEST_UPDATE_TIME);
        List<AppDO> doList = sqlSession.selectList("AppDao.listAfterTime", afterTime);
        updateTopicCache(doList, timestamp);
    }

    /**
     * 更新APP缓存
     */
    synchronized private void updateTopicCache(List<AppDO> doList, Long timestamp) {
        if (doList == null || doList.isEmpty() || APP_CACHE_LATEST_UPDATE_TIME >= timestamp) {
            // 本次无数据更新, 或者本次更新过时 时, 忽略本次更新
            return;
        }
        for (AppDO elem: doList) {
            APP_MAP.put(elem.getAppId(), elem);
        }
        APP_CACHE_LATEST_UPDATE_TIME = timestamp;
    }

    @Override
    public List<AppDO> listNewAll() {
        return sqlSession.selectList("AppDao.listNewAll");
    }
}