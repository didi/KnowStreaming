package com.xiaojukeji.kafka.manager.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.entity.bizenum.PreferredReplicaElectEnum;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.zookeeper.BrokerMetadata;
import com.xiaojukeji.kafka.manager.service.cache.ClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.AdminPreferredReplicaElectService;
import com.xiaojukeji.kafka.manager.service.service.TopicService;
import kafka.admin.AdminOperationException;
import kafka.admin.PreferredReplicaLeaderElectionCommand;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 优先副本选举
 * @author zengqiao
 * @date 2019/11/26.
 */
@Service("adminPreferredReplicaElectService")
public class AdminPreferredReplicaElectServiceImpl implements AdminPreferredReplicaElectService {
    private static final Logger logger = LoggerFactory.getLogger(AdminPreferredReplicaElectServiceImpl.class);

    private static final int DEFAULT_SESSION_TIMEOUT = 90000;

    @Autowired
    private TopicService topicService;

    @Override
    public PreferredReplicaElectEnum preferredReplicaElectionStatus(ClusterDO clusterDO) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(),
                    DEFAULT_SESSION_TIMEOUT,
                    DEFAULT_SESSION_TIMEOUT,
                    JaasUtils.isZkSecurityEnabled()
            );
            if (zkUtils.pathExists(ZkUtils.PreferredReplicaLeaderElectionPath())) {
                return PreferredReplicaElectEnum.RUNNING;
            }
            return PreferredReplicaElectEnum.SUCCESS;
        } catch (Exception e) {
            return PreferredReplicaElectEnum.UNKNOWN;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
    }

    @Override
    public PreferredReplicaElectEnum preferredReplicaElection(ClusterDO clusterDO, String operator) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(),
                    DEFAULT_SESSION_TIMEOUT,
                    DEFAULT_SESSION_TIMEOUT,
                    JaasUtils.isZkSecurityEnabled()
            );
            PreferredReplicaLeaderElectionCommand preferredReplicaElectionCommand = new PreferredReplicaLeaderElectionCommand(zkUtils, zkUtils.getAllPartitions());
            preferredReplicaElectionCommand.moveLeaderToPreferredReplica();
        } catch (AdminOperationException e) {

        } catch (Throwable t) {

        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
        return PreferredReplicaElectEnum.SUCCESS;
    }

    @Override
    public PreferredReplicaElectEnum preferredReplicaElection(ClusterDO clusterDO, Integer brokerId, String operator) {
        BrokerMetadata brokerMetadata = ClusterMetadataManager.getBrokerMetadata(clusterDO.getId(), brokerId);
        if (null == brokerMetadata) {
            return PreferredReplicaElectEnum.PARAM_ILLEGAL;
        }
        ZkUtils zkUtils = null;
        try {
            Map<String, List<Integer>> partitionMap = topicService.getTopicPartitionIdMap(clusterDO.getId(), brokerId);
            if (partitionMap == null || partitionMap.isEmpty()) {
                return PreferredReplicaElectEnum.SUCCESS;
            }
            String preferredReplicaElectString = convert2preferredReplicaElectString(partitionMap);
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(),
                    DEFAULT_SESSION_TIMEOUT,
                    DEFAULT_SESSION_TIMEOUT,
                    JaasUtils.isZkSecurityEnabled()
            );
            PreferredReplicaLeaderElectionCommand preferredReplicaElectionCommand = new PreferredReplicaLeaderElectionCommand(zkUtils, PreferredReplicaLeaderElectionCommand.parsePreferredReplicaElectionData(preferredReplicaElectString));
            preferredReplicaElectionCommand.moveLeaderToPreferredReplica();
        } catch (Exception e) {
            return PreferredReplicaElectEnum.UNKNOWN;
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
        return PreferredReplicaElectEnum.SUCCESS;
    }

    private String convert2preferredReplicaElectString(Map<String, List<Integer>> topicNamePartitionIdMap) {
        List<Map<String, Object>> metaList = new ArrayList<>();
        for(Map.Entry<String, List<Integer>> entry : topicNamePartitionIdMap.entrySet()){
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            for (Integer partitionId: entry.getValue()) {
                Map<String, Object> params = new HashMap<>();
                params.put("topic", entry.getKey());
                params.put("partition", partitionId);
                metaList.add(params);
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("partitions", metaList);
        return JSON.toJSONString(result);
    }
}
