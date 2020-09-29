package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhongyuankai
 * @date 20/4/7
 */
public interface TopicRequestMetricsDao {

    /**
     * 批量插入
     */
    int batchAdd(List<TopicMetricsDO> metricsDOList);

    int add(TopicMetricsDO metricsDO);

    /**
     * 依据获取指定时间段内Topic的发送消费请求耗时信息
     * @param clusterId 集群Id
     * @param topicName Topic名称
     * @param startTime 查询的起始时间
     * @param endTime 查询的截止时间
     * @return TopicRequestMetrics
     */
    List<TopicMetricsDO> selectByTime(Long clusterId, String topicName, Date startTime, Date endTime);

    /**
     * 删除指定时间之前的数据
     * @param endTime
     * @return
     */
    int deleteBeforeTime(Date endTime);

    int deleteBeforeId(Long id);

    List<TopicMetricsDO> getById(Long startId, Long endId);
}