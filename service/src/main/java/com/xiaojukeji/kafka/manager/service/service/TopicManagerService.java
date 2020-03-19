package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicFavoriteDO;

import java.util.List;

/**
 * @author arthur
 * @date 2017/7/20.
 */
public interface TopicManagerService {
    List<TopicDO> getByClusterId(Long clusterId);

    TopicDO getByTopicName(Long clusterId, String topicName);

//    Properties getTopicProperties(Long clusterId, String topicName);

    /**
     * 收藏Topic
     */
    Boolean addFavorite(List<TopicFavoriteDO> topicFavoriteDOList);

    /**
     * 取消收藏Topic
     */
    Boolean delFavorite(List<TopicFavoriteDO> topicFavoriteDOList);

    /**
     * 获取收藏的Topic列表
     */
    List<TopicFavoriteDO> getFavorite(String username);

    /**
     * 获取收藏的Topic列表
     */
    List<TopicFavoriteDO> getFavorite(String username, Long clusterId);
}

