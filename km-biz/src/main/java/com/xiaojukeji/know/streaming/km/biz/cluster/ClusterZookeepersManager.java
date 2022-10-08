package com.xiaojukeji.know.streaming.km.biz.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterZookeepersOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ClusterZookeepersOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ClusterZookeepersStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper.ZnodeVO;

/**
 * 多集群总体状态
 */
public interface ClusterZookeepersManager {
    Result<ClusterZookeepersStateVO> getClusterPhyZookeepersState(Long clusterPhyId);

    PaginationResult<ClusterZookeepersOverviewVO> getClusterPhyZookeepersOverview(Long clusterPhyId, ClusterZookeepersOverviewDTO dto);

    Result<ZnodeVO> getZnodeVO(Long clusterPhyId, String path);
}
