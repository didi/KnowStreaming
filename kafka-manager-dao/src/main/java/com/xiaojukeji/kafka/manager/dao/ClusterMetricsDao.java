package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.pojo.*;

import java.util.Date;
import java.util.List;

public interface ClusterMetricsDao {
    int batchAdd(List<ClusterMetricsDO> clusterMetricsList);

    List<ClusterMetricsDO> getClusterMetrics(long clusterId, Date startTime, Date endTime);

    int deleteBeforeTime(Date endTime);
}
