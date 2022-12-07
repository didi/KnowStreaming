package com.xiaojukeji.know.streaming.km.biz.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.cluster.ClusterConnectorsOverviewDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect.ConnectStateVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ClusterConnectorOverviewVO;

/**
 * Kafka集群Connector概览
 */
public interface ClusterConnectorsManager {
    PaginationResult<ClusterConnectorOverviewVO> getClusterConnectorsOverview(Long clusterPhyId, ClusterConnectorsOverviewDTO dto);

    ConnectStateVO getClusterConnectorsState(Long clusterPhyId);
}
