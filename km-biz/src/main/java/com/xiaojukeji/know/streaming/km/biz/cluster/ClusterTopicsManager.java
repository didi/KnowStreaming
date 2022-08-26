package com.xiaojukeji.know.streaming.km.biz.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterTopicsOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res.ClusterPhyTopicsOverviewVO;

/**
 * 多集群总体状态
 */
public interface ClusterTopicsManager {
    PaginationResult<ClusterPhyTopicsOverviewVO> getClusterPhyTopicsOverview(Long clusterPhyId, ClusterTopicsOverviewDTO dto);
}
