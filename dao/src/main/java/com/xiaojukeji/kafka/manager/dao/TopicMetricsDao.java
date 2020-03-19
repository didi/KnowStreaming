package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;

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
    int batchAdd(List<TopicMetrics> topicMetricsDOList);

    /**
     * 根据时间区间获取topic监控数据
     */
    List<TopicMetrics> getTopicMetricsByInterval(Long clusterId, String topicName, Date startTime, Date endTime);

    int deleteBeforeTime(Date endTime);
}
