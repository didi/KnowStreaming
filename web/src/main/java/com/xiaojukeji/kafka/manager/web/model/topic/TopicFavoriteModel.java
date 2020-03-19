package com.xiaojukeji.kafka.manager.web.model.topic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/7/11
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "TopicFavoriteModel", description = "Topic收藏,取消收藏")
public class TopicFavoriteModel {
    private List<TopicFavorite> topicFavoriteList;

    public List<TopicFavorite> getTopicFavoriteList() {
        return topicFavoriteList;
    }

    public void setTopicFavoriteList(List<TopicFavorite> topicFavoriteList) {
        this.topicFavoriteList = topicFavoriteList;
    }

    @Override
    public String toString() {
        return "TopicFavoriteModel{" +
                "topicFavoriteList=" + topicFavoriteList +
                '}';
    }

    public boolean legal() {
        if (topicFavoriteList == null) {
            return false;
        }
        for (TopicFavorite topicFavorite: topicFavoriteList) {
            if (topicFavorite.getClusterId() == null
                    || topicFavorite.getClusterId() < 0
                    || StringUtils.isEmpty(topicFavorite.getTopicName())) {
                return false;
            }
        }
        return true;
    }
}