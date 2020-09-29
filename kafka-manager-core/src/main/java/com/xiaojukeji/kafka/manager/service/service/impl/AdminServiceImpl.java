package com.xiaojukeji.kafka.manager.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaojukeji.kafka.manager.common.bizenum.*;
import com.xiaojukeji.kafka.manager.common.entity.pojo.OperateRecordDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.gateway.AuthorityDO;
import com.xiaojukeji.kafka.manager.common.entity.ao.gateway.TopicQuota;
import com.xiaojukeji.kafka.manager.common.constant.Constant;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.BrokerMetadata;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicDO;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.*;
import com.xiaojukeji.kafka.manager.service.service.gateway.AuthorityService;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import com.xiaojukeji.kafka.manager.service.utils.TopicCommands;
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
 * @author zengqiao
 * @date 2019/11/26.
 */
@Service("adminService")
public class AdminServiceImpl implements AdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    private static final Long DEFAULT_DEAD_BROKER_LIMIT_NUM = 1L;

    @Autowired
    private TopicManagerService topicManagerService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private OperateRecordService operateRecordService;

    @Override
    public ResultStatus createTopic(ClusterDO clusterDO,
                                    TopicDO topicDO,
                                    Integer partitionNum,
                                    Integer replicaNum,
                                    Long regionId,
                                    List<Integer> brokerIdList,
                                    Properties properties,
                                    String applicant,
                                    String operator) {
        List<Integer> fullBrokerIdList = regionService.getFullBrokerIdList(clusterDO.getId(), regionId, brokerIdList);
        if (PhysicalClusterMetadataManager.getNotAliveBrokerNum(clusterDO.getId(), fullBrokerIdList) > DEFAULT_DEAD_BROKER_LIMIT_NUM) {
            return ResultStatus.BROKER_NOT_EXIST;
        }

        // step1 创建Topic
        ResultStatus rs = TopicCommands.createTopic(
                clusterDO,
                topicDO.getTopicName(),
                partitionNum,
                replicaNum,
                fullBrokerIdList,
                properties
        );
        if (!ResultStatus.SUCCESS.equals(rs)) {
            // 创建失败
            return rs;
        }
        // step2 记录操作
        Map<String, Object> content = new HashMap<>(4);
        content.put("clusterId", clusterDO.getId());
        content.put("topicName", topicDO.getTopicName());
        content.put("replicaNum", replicaNum);
        content.put("partitionNum", partitionNum);
        OperateRecordDO operateRecordDO = new OperateRecordDO();
        operateRecordDO.setModuleId(ModuleEnum.TOPIC.getCode());
        operateRecordDO.setOperateId(OperateEnum.ADD.getCode());
        operateRecordDO.setResource(topicDO.getTopicName());
        operateRecordDO.setContent(JSONObject.toJSONString(content));
        operateRecordDO.setOperator(operator);
        operateRecordService.insert(operateRecordDO);

        // step3 TopicDO写DB
        topicManagerService.addTopic(topicDO);

        // step4 添加权限及配额
        AuthorityDO authority = new AuthorityDO();
        authority.setClusterId(topicDO.getClusterId());
        authority.setTopicName(topicDO.getTopicName());
        authority.setAppId(topicDO.getAppId());
        authority.setAccess(TopicAuthorityEnum.READ_WRITE.getCode());
//        authority.setApplicant(applicant);

        TopicQuota topicQuotaDO = new TopicQuota();
        topicQuotaDO.setClusterId(topicDO.getClusterId());
        topicQuotaDO.setTopicName(topicDO.getTopicName());
        topicQuotaDO.setAppId(topicDO.getAppId());
        authorityService.addAuthorityAndQuota(authority, topicQuotaDO);
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus deleteTopic(ClusterDO clusterDO,
                                    String topicName,
                                    String operator) {
        // 1. 集群中删除topic
        ResultStatus rs = TopicCommands.deleteTopic(clusterDO, topicName);
        if (!ResultStatus.SUCCESS.equals(rs)) {
            return rs;
        }
        // 2. 记录操作
        Map<String, Object> content = new HashMap<>(2);
        content.put("clusterId", clusterDO.getId());
        content.put("topicName", topicName);

        OperateRecordDO operateRecordDO = new OperateRecordDO();
        operateRecordDO.setModuleId(ModuleEnum.TOPIC.getCode());
        operateRecordDO.setOperateId(OperateEnum.DELETE.getCode());
        operateRecordDO.setResource(topicName);
        operateRecordDO.setContent(JSONObject.toJSONString(content));
        operateRecordDO.setOperator(operator);
        operateRecordService.insert(operateRecordDO);

        // 3. 数据库中删除topic
        topicManagerService.deleteByTopicName(clusterDO.getId(), topicName);
        return rs;
    }

    @Override
    public TaskStatusEnum preferredReplicaElectionStatus(ClusterDO clusterDO) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(),
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );
            if (zkUtils.pathExists(ZkUtils.PreferredReplicaLeaderElectionPath())) {
                return TaskStatusEnum.RUNNING;
            }
        } catch (Exception e) {
            return TaskStatusEnum.UNKNOWN;
        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
        return TaskStatusEnum.SUCCEED;
    }

    @Override
    public ResultStatus preferredReplicaElection(ClusterDO clusterDO, String operator) {
        ZkUtils zkUtils = null;
        try {
            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(),
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );
            PreferredReplicaLeaderElectionCommand command =
                    new PreferredReplicaLeaderElectionCommand(zkUtils, zkUtils.getAllPartitions());
            command.moveLeaderToPreferredReplica();
        } catch (AdminOperationException e) {

        } catch (Throwable t) {

        } finally {
            if (null != zkUtils) {
                zkUtils.close();
            }
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus preferredReplicaElection(ClusterDO clusterDO, Integer brokerId, String operator) {
        BrokerMetadata brokerMetadata = PhysicalClusterMetadataManager.getBrokerMetadata(clusterDO.getId(), brokerId);
        if (null == brokerMetadata) {
            return ResultStatus.PARAM_ILLEGAL;
        }
        ZkUtils zkUtils = null;
        try {
            Map<String, List<Integer>> partitionMap = topicService.getTopicPartitionIdMap(clusterDO.getId(), brokerId);
            if (partitionMap == null || partitionMap.isEmpty()) {
                return ResultStatus.SUCCESS;
            }
            String preferredReplicaElectString = convert2preferredReplicaElectString(partitionMap);

            zkUtils = ZkUtils.apply(clusterDO.getZookeeper(),
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS,
                    JaasUtils.isZkSecurityEnabled()
            );
            PreferredReplicaLeaderElectionCommand preferredReplicaElectionCommand =
                    new PreferredReplicaLeaderElectionCommand(
                            zkUtils,
                            PreferredReplicaLeaderElectionCommand.parsePreferredReplicaElectionData(
                                    preferredReplicaElectString
                            )
                    );
            preferredReplicaElectionCommand.moveLeaderToPreferredReplica();
        } catch (Exception e) {
            return ResultStatus.OPERATION_FAILED;
        } finally {
            if (zkUtils != null) {
                zkUtils.close();
            }
        }
        return ResultStatus.SUCCESS;
    }

    @Override
    public ResultStatus expandPartitions(ClusterDO clusterDO,
                                         String topicName,
                                         Integer partitionNum,
                                         Long regionId,
                                         List<Integer> brokerIdList,
                                         String operator) {
        List<Integer> fullBrokerIdList = regionService.getFullBrokerIdList(clusterDO.getId(), regionId, brokerIdList);
        if (PhysicalClusterMetadataManager.getNotAliveBrokerNum(clusterDO.getId(), fullBrokerIdList) > DEFAULT_DEAD_BROKER_LIMIT_NUM) {
            return ResultStatus.BROKER_NOT_EXIST;
        }

        ResultStatus resultStatus = TopicCommands.expandTopic(
                clusterDO,
                topicName,
                partitionNum,
                fullBrokerIdList
        );
        if (!ResultStatus.SUCCESS.equals(resultStatus)) {
            return resultStatus;
        }

        //记录操作
        Map<String, Object> content = new HashMap<>(2);
        content.put("clusterId", clusterDO.getId());
        content.put("topicName", topicName);
        content.put("partitionNum", partitionNum);
        content.put("regionId", regionId);
        content.put("brokerIdList", brokerIdList);

        OperateRecordDO operateRecordDO = new OperateRecordDO();
        operateRecordDO.setModuleId(ModuleEnum.PARTITION.getCode());
        operateRecordDO.setOperateId(OperateEnum.ADD.getCode());
        operateRecordDO.setResource(topicName);
        operateRecordDO.setContent(JSONObject.toJSONString(content));
        operateRecordDO.setOperator(operator);
        operateRecordService.insert(operateRecordDO);
        return resultStatus;
    }


    private String convert2preferredReplicaElectString(Map<String, List<Integer>> topicNamePartitionIdMap) {
        List<Map<String, Object>> metaList = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : topicNamePartitionIdMap.entrySet()) {
            if (entry.getValue() == null || entry.getValue().isEmpty()) {
                continue;
            }
            for (Integer partitionId : entry.getValue()) {
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

    @Override
    public Properties getTopicConfig(ClusterDO clusterDO, String topicName) {
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterDO.getId());
        if (ValidateUtils.isNull(zkConfig)) {
            return null;
        }
        return KafkaZookeeperUtils.getTopicProperties(zkConfig, topicName);
    }

    @Override
    public ResultStatus modifyTopicConfig(ClusterDO clusterDO, String topicName, Properties properties, String operator) {
        ResultStatus rs = TopicCommands.modifyTopicConfig(clusterDO, topicName, properties);
        if (!ResultStatus.SUCCESS.equals(rs)) {
            return rs;
        }

        return rs;
    }
}
