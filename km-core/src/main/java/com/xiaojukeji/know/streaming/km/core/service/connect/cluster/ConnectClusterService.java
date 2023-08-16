package com.xiaojukeji.know.streaming.km.core.service.connect.cluster;


import com.xiaojukeji.know.streaming.km.common.bean.dto.connect.cluster.ConnectClusterDTO;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectCluster;
import com.xiaojukeji.know.streaming.km.common.bean.entity.connect.ConnectClusterMetadata;
import com.xiaojukeji.know.streaming.km.common.bean.entity.kafka.KSGroupDescription;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.core.service.meta.MetaDataService;

import java.util.List;

/**
 * Connect-Cluster
 */
public interface ConnectClusterService extends MetaDataService<KSGroupDescription> {
    Long replaceAndReturnIdInDB(ConnectClusterMetadata metadata);

    List<ConnectCluster> listByKafkaCluster(Long kafkaClusterPhyId);

    List<ConnectCluster> listAllClusters();

    ConnectCluster getById(Long connectClusterId);

    ConnectCluster getByName(Long clusterPhyId, String connectClusterName);

    String getClusterVersion(Long connectClusterId);

    String getClusterName(Long connectClusterId);

    Result<Void> deleteInDB(Long connectClusterId, String operator);

    Result<Void> batchModifyInDB(List<ConnectClusterDTO> dtoList, String operator);

    Boolean existConnectClusterDown(Long kafkaClusterPhyId);
}
