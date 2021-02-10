package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.ListUtils;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkPathUtil;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi.TopicJmxSwitch;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/27
 */
@Service("zookeeperService")
public class ZookeeperServiceImpl implements ZookeeperService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceImpl.class);

    @Override
    public Result openTopicJmx(Long clusterId, String topicName, TopicJmxSwitch jmxSwitch) {
        if (ValidateUtils.isNull(clusterId) || ValidateUtils.isNull(topicName)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }

        TopicMetadata metadata = PhysicalClusterMetadataManager.getTopicMetadata(clusterId, topicName);
        if (ValidateUtils.isNull(metadata)) {
            return Result.buildFrom(ResultStatus.TOPIC_NOT_EXIST);
        }

        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterId);
        for (Integer brokerId: metadata.getBrokerIdSet()) {
            if (!KafkaZookeeperUtils.openBrokerTopicJmx(zkConfig, brokerId, topicName, jmxSwitch)) {
                return Result.buildFrom(ResultStatus.OPERATION_FAILED);
            }
        }
        return new Result();
    }

    @Override
    public Result<List<Integer>> getControllerPreferredCandidates(Long clusterId) {
        if (ValidateUtils.isNull(clusterId)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterId);
        if (ValidateUtils.isNull(zkConfig)) {
            return Result.buildFrom(ResultStatus.ZOOKEEPER_CONNECT_FAILED);
        }

        try {
            if (!zkConfig.checkPathExists(ZkPathUtil.D_CONTROLLER_CANDIDATES)) {
                return Result.buildSuc(new ArrayList<>());
            }
            List<String> brokerIdList = zkConfig.getChildren(ZkPathUtil.D_CONTROLLER_CANDIDATES);
            if (ValidateUtils.isEmptyList(brokerIdList)) {
                return Result.buildSuc(new ArrayList<>());
            }
            return Result.buildSuc(ListUtils.string2IntList(ListUtils.strList2String(brokerIdList)));
        } catch (Exception e) {
            LOGGER.error("class=ZookeeperServiceImpl||method=getControllerPreferredCandidates||clusterId={}||errMsg={}", clusterId, e.getMessage());
        }
        return Result.buildFrom(ResultStatus.ZOOKEEPER_READ_FAILED);
    }

    @Override
    public Result addControllerPreferredCandidate(Long clusterId, Integer brokerId) {
        if (ValidateUtils.isNull(clusterId)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterId);
        if (ValidateUtils.isNull(zkConfig)) {
            return Result.buildFrom(ResultStatus.ZOOKEEPER_CONNECT_FAILED);
        }

        try {
            if (zkConfig.checkPathExists(ZkPathUtil.getControllerCandidatePath(brokerId))) {
                // 节点已经存在, 则直接忽略
                return Result.buildSuc();
            }

            if (!zkConfig.checkPathExists(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE)) {
                zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONFIG_EXTENSION_ROOT_NODE, "");
            }

            if (!zkConfig.checkPathExists(ZkPathUtil.D_CONTROLLER_CANDIDATES)) {
                zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.D_CONTROLLER_CANDIDATES, "");
            }

            zkConfig.setOrCreatePersistentNodeStat(ZkPathUtil.getControllerCandidatePath(brokerId), "");
            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error("class=ZookeeperServiceImpl||method=addControllerPreferredCandidate||clusterId={}||brokerId={}||errMsg={}||", clusterId, brokerId, e.getMessage());
        }
        return Result.buildFrom(ResultStatus.ZOOKEEPER_WRITE_FAILED);
    }

    @Override
    public Result deleteControllerPreferredCandidate(Long clusterId, Integer brokerId) {
        if (ValidateUtils.isNull(clusterId)) {
            return Result.buildFrom(ResultStatus.PARAM_ILLEGAL);
        }
        ZkConfigImpl zkConfig = PhysicalClusterMetadataManager.getZKConfig(clusterId);
        if (ValidateUtils.isNull(zkConfig)) {
            return Result.buildFrom(ResultStatus.ZOOKEEPER_CONNECT_FAILED);
        }

        try {
            if (!zkConfig.checkPathExists(ZkPathUtil.getControllerCandidatePath(brokerId))) {
                return Result.buildSuc();
            }
            zkConfig.delete(ZkPathUtil.getControllerCandidatePath(brokerId));
            return Result.buildSuc();
        } catch (Exception e) {
            LOGGER.error("class=ZookeeperServiceImpl||method=deleteControllerPreferredCandidate||clusterId={}||brokerId={}||errMsg={}||", clusterId, brokerId, e.getMessage());
        }
        return Result.buildFrom(ResultStatus.ZOOKEEPER_DELETE_FAILED);
    }
}