package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.bizenum.PreferredReplicaElectEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;

public interface AdminPreferredReplicaElectService {
    PreferredReplicaElectEnum preferredReplicaElectionStatus(ClusterDO clusterDO);

    PreferredReplicaElectEnum preferredReplicaElection(ClusterDO clusterDO, String operator);

    PreferredReplicaElectEnum preferredReplicaElection(ClusterDO clusterDO, Integer brokerId, String operator);
}
