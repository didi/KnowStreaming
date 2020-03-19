package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicFavoriteDO;
import com.xiaojukeji.kafka.manager.dao.TopicFavoriteDao;
import com.xiaojukeji.kafka.manager.dao.TopicDao;
import com.xiaojukeji.kafka.manager.service.service.TopicManagerService;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author arthur
 * @date 2017/7/21.
 */
@Service("topicManagerService")
public class TopicManagerServiceImpl implements TopicManagerService {
    private static final Logger logger = LoggerFactory.getLogger(TopicManagerServiceImpl.class);

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private TopicFavoriteDao topicFavoriteDao;

    @Autowired
    private ZookeeperService zookeeperService;

    @Override
    public List<TopicDO> getByClusterId(Long clusterId) {
        if (clusterId == null) {
            return new ArrayList<>();
        }
        return topicDao.getByClusterId(clusterId);
    }

    @Override
    public TopicDO getByTopicName(Long clusterId, String topicName) {
        if (StringUtils.isEmpty(topicName) || clusterId == null) {
            return null;
        }
        return topicDao.getByTopicName(clusterId, topicName);
    }

//    @Override
//    public Properties getTopicProperties(Long clusterId, String topicName) {
//        if (clusterId == null || StringUtils.isEmpty(topicName)) {
//            return new Properties();
//        }
//        try {
//            ZkConfigImpl zkConfig = ClusterMetadataManager.getZKConfig(clusterId);
//            return zookeeperService.getTopicProperties(zkConfig, topicName);
//        } catch (Exception e) {
//            logger.error("getTopicProperties@TopicManagerService, get properties failed, clusterId:{} topicName:{}.", clusterId, topicName, e);
//        }
//        return new Properties();
//    }

    @Override
    public Boolean addFavorite(List<TopicFavoriteDO> topicFavoriteDOList) {
        return topicFavoriteDao.batchAdd(topicFavoriteDOList) > 0;
    }

    @Override
    public Boolean delFavorite(List<TopicFavoriteDO> unFavoriteList) {
        if (unFavoriteList == null || unFavoriteList.isEmpty()) {
            return Boolean.TRUE;
        }
        List<TopicFavoriteDO> topicFavoriteDOList = topicFavoriteDao.getByUserName(unFavoriteList.get(0).getUsername());
        if (topicFavoriteDOList == null) {
            return Boolean.TRUE;
        }

        Set<String> unFavoriteSet = new HashSet<>();
        for (TopicFavoriteDO topicFavoriteDO: unFavoriteList) {
            unFavoriteSet.add(String.valueOf(topicFavoriteDO.getClusterId()) + "_" + topicFavoriteDO.getTopicName());
        }
        Set<Long> idSet = new HashSet<>();
        for (TopicFavoriteDO topicFavoriteDO: topicFavoriteDOList) {
            if (unFavoriteSet.contains(String.valueOf(topicFavoriteDO.getClusterId()) + "_" + topicFavoriteDO.getTopicName())) {
                idSet.add(topicFavoriteDO.getId());
            }
        }
        return topicFavoriteDao.batchDelete(new ArrayList<>(idSet));
    }

    @Override
    public List<TopicFavoriteDO> getFavorite(String username) {
        if (StringUtils.isEmpty(username)) {
            return new ArrayList<>();
        }
        try {
            return topicFavoriteDao.getByUserName(username);
        } catch (Exception e) {
            logger.error("getFavorite@TopicManangerServiceImpl, get favorite failed, username:{}.", username, e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<TopicFavoriteDO> getFavorite(String username, Long clusterId) {
        if (StringUtils.isEmpty(username) && clusterId == null) {
            return new ArrayList<>();
        }
        try {
            if (clusterId == null) {
                return topicFavoriteDao.getByUserName(username);
            }
            return topicFavoriteDao.getByUserNameAndClusterId(username, clusterId);
        } catch (Exception e) {
            logger.error("getFavorite@TopicManangerServiceImpl, get favorite failed, username:{} clusterId:{}.", username, clusterId, e);
        }
        return new ArrayList<>();
    }
}
