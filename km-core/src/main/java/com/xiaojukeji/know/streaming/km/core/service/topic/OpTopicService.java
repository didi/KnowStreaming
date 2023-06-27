package com.xiaojukeji.know.streaming.km.core.service.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicCreateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicPartitionExpandParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicTruncateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;

public interface OpTopicService {
    /**
     * 创建Topic
     */
    Result<Void> createTopic(TopicCreateParam createParam, String operator);

    /**
     * 删除Topic
     */
    Result<Void> deleteTopic(TopicParam param, String operator);

    /**
     * 扩分区
     */
    Result<Void> expandTopic(TopicPartitionExpandParam expandParam, String operator);

    /**
     * 清空topic消息
     */
    Result<Void> truncateTopic(TopicTruncateParam param, String operator);
}
