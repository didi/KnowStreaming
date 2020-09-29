package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/30
 */
public interface TopicStatisticsDao {
    int replace(TopicStatisticsDO topicStatisticsDO);

    TopicStatisticsDO getByTopicAndDay(Long clusterId, String topicName, String gmtDay);

    List<TopicStatisticsDO> getTopicStatistic(Long clusterId, String topicName, Date startTime, Date endTime);

    List<TopicStatisticsDO> getTopicStatisticData(Long clusterId, Date startTime, Double minMaxAvgBytesIn);

    Double getTopicMaxAvgBytesIn(Long clusterId, String topicName, Date startTime, Date endTime, Integer maxAvgDay);

    /**
     * 删除指定时间之前的数据
     * @param endTime
     * @return
     */
    int deleteBeforeTime(Date endTime);
}