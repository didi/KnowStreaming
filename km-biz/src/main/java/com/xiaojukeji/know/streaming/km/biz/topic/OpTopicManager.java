package com.xiaojukeji.know.streaming.km.biz.topic;

import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicCreateDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.topic.TopicExpansionDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

public interface OpTopicManager {
    /**
     * 创建Topic
     */
    Result<Void> createTopic(TopicCreateDTO dto, String operator);

    /**
     * 删除Topic
     */
    Result<Void> deleteTopicCombineRes(Long clusterPhyId, String topicName, String operator);

    /**
     * 扩分区
     */
    Result<Void> expandTopic(TopicExpansionDTO dto, String operator);

    /**
     * 清空Topic
     */
    Result<Void> truncateTopic(Long clusterPhyId, String topicName, String operator);
}
