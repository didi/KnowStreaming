package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author tukun
 * @date 2015/11/11.
 */
public interface TopicMetricsDao {
    /**
     * 批量插入数据
     */
    int batchAdd(List<TopicMetricsDO> metricsList);

    /**
     * 根据时间区间获取topic监控数据
     */
    List<TopicMetricsDO> getTopicMetrics(Long clusterId, String topicName, Date startTime, Date endTime);

    List<TopicMetricsDO> getLatestTopicMetrics(Long clusterId, Date afterTime);

    int deleteBeforeTime(Date endTime);
}
