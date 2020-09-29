package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

/**
 * @author tukun
 * @date 2015/11/6.
 */
public interface BrokerMetricsDao {
    /**
     * 批量插入数据
     */
    int batchAdd(List<BrokerMetricsDO> doList);

    /**
     * 根据时间区间获取Broker监控数据
     */
    List<BrokerMetricsDO> getBrokerMetrics(Long clusterId, Integer brokerId, Date startTime, Date endTime);

    int deleteBeforeTime(Date endTime);
}
