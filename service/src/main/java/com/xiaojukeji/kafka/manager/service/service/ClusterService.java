package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.po.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.po.ControllerDO;

import java.util.Date;
import java.util.List;

/**
 * Cluster Service
 * @author zengqiao
 * @date 19/4/3
 */
public interface ClusterService {
    ClusterDO getById(Long clusterId);

    Result addNewCluster(ClusterDO clusterDO, String operator);

    Result updateCluster(ClusterDO newClusterDO, boolean reload, String operator);

    List<ClusterDO> listAll();

    List<ClusterMetricsDO> getClusterMetricsByInterval(long clusterId, Date startTime, Date endTime);

    List<ControllerDO> getKafkaControllerHistory(Long clusterId);
}
