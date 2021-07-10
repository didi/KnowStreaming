package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 20/4/3
 */
public interface TopicThrottledMetricsDao {

    /**
     * 批量插入限流信息
     */
    int insertBatch(List<TopicThrottledMetricsDO> topicThrottleDOList);

    /**
     * 查询topic限流历史信息
     */
    List<TopicThrottledMetricsDO> getTopicThrottle(long clusterId,
                                                   String topicName,
                                                   String appId,
                                                   Date startTime,
                                                   Date endTime);

    /**
     * 查询appId限流历史
     */
    List<TopicThrottledMetricsDO> getAppIdThrottle(long clusterId, String appId, Date startTime, Date endTime);

    List<TopicThrottledMetricsDO> getLatestTopicThrottledMetrics(Long clusterId, Date afterTime);

    int deleteBeforeTime(Date endTime);
}
