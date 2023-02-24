package com.xiaojukeji.know.streaming.km.biz.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhysHealthState;
import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhysState;
import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.MultiClusterDashboardDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyBaseVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.ClusterPhyDashboardVO;

import java.util.List;

/**
 * 多集群总体状态
 */
public interface MultiClusterPhyManager {
    /**
     * 获取所有集群的状态
     * @return
     */
    ClusterPhysState getClusterPhysState();

    ClusterPhysHealthState getClusterPhysHealthState();

    /**
     * 查询多集群大盘
     * @param dto 分页信息
     * @return
     */
    PaginationResult<ClusterPhyDashboardVO> getClusterPhysDashboard(MultiClusterDashboardDTO dto);

    Result<List<ClusterPhyBaseVO>> getClusterPhysBasic();
}
