package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.entity.ao.ClusterDetailDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.cluster.ControllerPreferredCandidate;
import com.xiaojukeji.kafka.manager.common.entity.dto.op.ControllerPreferredCandidateDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster.ClusterNameDTO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ControllerDO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Cluster Service
 * @author zengqiao
 * @date 19/4/3
 */
public interface ClusterService {
    ClusterDO getById(Long clusterId);

    ResultStatus addNew(ClusterDO clusterDO, String operator);

    ResultStatus updateById(ClusterDO clusterDO, String operator);

    ResultStatus modifyStatus(Long clusterId, Integer status, String operator);

    List<ClusterDO> list();

    Map<Long, ClusterDO> listMap();

    List<ClusterDO> listAll();

    List<ClusterMetricsDO> getClusterMetricsFromDB(Long clusterId, Date startTime, Date endTime);

    List<ControllerDO> getKafkaControllerHistory(Long clusterId);

    ClusterDetailDTO getClusterDetailDTO(Long clusterId, Boolean needDetail);

    List<ClusterDetailDTO> getClusterDetailDTOList(Boolean needDetail);

    ClusterNameDTO getClusterName(Long logicClusterId);

    ResultStatus deleteById(Long clusterId, String operator);

    /**
     * 获取优先被选举为controller的broker
     * @param clusterId 集群ID
     * @return void
     */
    Result<List<ControllerPreferredCandidate>> getControllerPreferredCandidates(Long clusterId);

    /**
     * 增加优先被选举为controller的broker
     * @param clusterId 集群ID
     * @param brokerIdList brokerId列表
     * @return
     */
    Result addControllerPreferredCandidates(Long clusterId, List<Integer> brokerIdList);

    /**
     * 减少优先被选举为controller的broker
     * @param clusterId 集群ID
     * @param brokerIdList brokerId列表
     * @return
     */
    Result deleteControllerPreferredCandidates(Long clusterId, List<Integer> brokerIdList);
}
