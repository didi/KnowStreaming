package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/2
 */
public interface TopicAppMetricsDao {

    /**
     * 批量插入数据
     */
    int batchAdd(List<TopicMetricsDO> doList);

    /**
     * 根据时间区间获取topic监控数据
     */
    List<TopicMetricsDO> getTopicAppMetrics(Long clusterId,
                                            String topicName,
                                            String appId,
                                            Date startTime,
                                            Date endTime);

    /**
     * 删除指定时间之前的数据
     * @param endTime
     * @return
     */
    int deleteBeforeTime(Date endTime);
}
