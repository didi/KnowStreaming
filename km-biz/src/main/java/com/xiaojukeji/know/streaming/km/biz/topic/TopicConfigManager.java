package com.xiaojukeji.know.streaming.km.biz.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaTopicDefaultConfig;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;

public interface TopicConfigManager {
    /**
     * 获取Topic默认配置
     * @param clusterPhyId 物理集群ID
     * @return
     */
    Result<List<KafkaTopicDefaultConfig>> getDefaultTopicConfig(Long clusterPhyId);
}
