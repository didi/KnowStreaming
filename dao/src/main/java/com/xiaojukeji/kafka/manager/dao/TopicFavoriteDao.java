package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.po.TopicFavoriteDO;

import java.util.List;

public interface TopicFavoriteDao {
    int batchAdd(List<TopicFavoriteDO> topicFavoriteDOList);

    Boolean batchDelete(List<Long> idList);

    List<TopicFavoriteDO> getByUserName(String username);

    List<TopicFavoriteDO> getByUserNameAndClusterId(String username, Long clusterId);
}