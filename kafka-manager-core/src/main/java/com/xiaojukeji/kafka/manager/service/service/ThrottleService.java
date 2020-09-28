package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.KafkaClientEnum;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicThrottledMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicThrottledMetricsDO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zhongyuankai
 * @date 20/4/3
 */
public interface ThrottleService {
    /**
     * 批量插入
     * @param topicThrottleDOList
     * @return
     */
    int insertBatch(List<TopicThrottledMetricsDO> topicThrottleDOList);

    /**
     * 查询Topic限流历史信息
     * @param clusterId
     * @param topicName
     * @param appId
     * @param startTime
     * @param endTime
     * @return
     */
    List<TopicThrottledMetricsDO> getTopicThrottleFromDB(Long clusterId,
                                                         String topicName,
                                                         String appId,
                                                         Date startTime,
                                                         Date endTime);

    /**
     * 查询topic限流历史信息
     * @param clusterId 集群ID
     * @param brokerIdSet BrokerId集合
     * @param kafkaClientList 查询字段
     * @return 限流信息
     */
    List<TopicThrottledMetrics> getThrottledTopicsFromJmx(Long clusterId,
                                                          Set<Integer> brokerIdSet,
                                                          List<KafkaClientEnum> kafkaClientList);
}
