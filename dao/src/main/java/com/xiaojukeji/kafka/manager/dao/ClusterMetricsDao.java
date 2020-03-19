package com.xiaojukeji.kafka.manager.dao;

import com.xiaojukeji.kafka.manager.common.entity.po.ClusterMetricsDO;

import java.util.Date;
import java.util.List;

public interface ClusterMetricsDao {
    int batchAdd(List<ClusterMetricsDO> clusterMetricsList);

    List<ClusterMetricsDO> getClusterMetricsByInterval(long clusterId, Date startTime, Date endTime);

    int deleteBeforeTime(Date endTime);
}
