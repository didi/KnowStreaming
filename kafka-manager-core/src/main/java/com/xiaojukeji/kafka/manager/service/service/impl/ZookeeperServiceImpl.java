package com.xiaojukeji.kafka.manager.service.service.impl;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ResultStatus;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.zookeeper.ZkConfigImpl;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.TopicMetadata;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi.TopicJmxSwitch;
import com.xiaojukeji.kafka.manager.service.cache.PhysicalClusterMetadataManager;
import com.xiaojukeji.kafka.manager.service.service.ZookeeperService;
import com.xiaojukeji.kafka.manager.service.utils.KafkaZookeeperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
}