package com.xiaojukeji.know.streaming.km.core.service.connect.worker;

import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.PaginationBaseDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectWorker;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.PaginationResult;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector.ClusterWorkerOverviewVO;

import java.util.List;

/**
 * Worker
 * @author didi
 */
public interface WorkerService {
    /**
     * 批量插入数据库
     * @param connectClusterId
     * @param workerList
     */
    void batchReplaceInDB(Long connectClusterId, List<ConnectWorker> workerList);

    /**
     * 从数据库中获取
     * @param connectClusterId
     * @return
     */
    List<ConnectWorker> listFromDB(Long connectClusterId);

    /**
     * 分页获取
     * @param kafkaClusterPhyId
     * @param dto
     * @return
     */
    PaginationResult<ClusterWorkerOverviewVO> pageWorkByKafkaClusterPhy(Long kafkaClusterPhyId, PaginationBaseDTO dto);

    List<ConnectWorker> listByKafkaClusterIdFromDB(Long kafkaClusterPhyId);
}
