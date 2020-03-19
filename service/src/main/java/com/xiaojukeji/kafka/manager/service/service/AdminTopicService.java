package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.bizenum.AdminTopicStatusEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.TopicDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.TopicMetadata;

import java.util.Properties;

/**
 * @author zengqiao
 * @date 19/11/21
 */
public interface AdminTopicService {
    AdminTopicStatusEnum createTopic(ClusterDO clusterDO, TopicMetadata topicMetadata, TopicDO topicDO, Properties topicConfig, String operator);

    AdminTopicStatusEnum deleteTopic(ClusterDO clusterDO, String topicName, String operator);

    AdminTopicStatusEnum modifyTopic(ClusterDO clusterDO, TopicDO topicDO, Properties topicConfig, String operator);

    AdminTopicStatusEnum expandTopic(ClusterDO clusterDO, TopicMetadata topicMetadata, String operator);
}
