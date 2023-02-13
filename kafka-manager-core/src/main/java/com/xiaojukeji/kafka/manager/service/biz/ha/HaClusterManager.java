package com.xiaojukeji.kafka.manager.service.biz.ha;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.ClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;

import java.util.List;

/**
 * Ha Cluster管理
 */
public interface HaClusterManager {
    List<ClusterDetailDTO> getClusterDetailDTOList(Boolean needDetail);

    Result<Void> addNew(ClusterDO clusterDO, Long activeClusterId, String operator);

    Result<Void> deleteById(Long clusterId, String operator);

}
