package com.xiaojukeji.know.streaming.km.core.service.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig.KafkaConfigDetail;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.config.KafkaTopicConfigParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Kafka相关配置接口
 * @author zengqiao
 * @date 22/03/03
 */
public interface TopicConfigService {
    /**
     * 修改Topic配置
     * @param kafkaTopicConfigParam 修改信息
     * @param operator 操作人
     * @return
     */
    Result<Void> modifyTopicConfig(KafkaTopicConfigParam kafkaTopicConfigParam, String operator);

    /**
     * 从Kafka直接获取Topic配置
     * @param clusterPhyId 物理集群ID
     * @param topicName Topic名称
     * @return
     */
    Result<List<KafkaConfigDetail>> getTopicConfigDetailFromKafka(Long clusterPhyId, String topicName);

    Result<Map<String, String>> getTopicConfigFromKafka(Long clusterPhyId, String topicName);

    List<Properties> getConfigNamesAndDocs(Long clusterPhyId);
}
