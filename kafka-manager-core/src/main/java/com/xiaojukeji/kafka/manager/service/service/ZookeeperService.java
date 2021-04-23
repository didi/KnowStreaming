package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi.TopicJmxSwitch;

import java.util.List;

/**
 * ZK相关的接口
 * @author tukun
 * @date 2015/11/11.
 */
public interface ZookeeperService {
    /**
     * 开启JMX
     * @param clusterId 集群ID
     * @param topicName Topic名称
     * @param jmxSwitch JMX开关
     * @return 操作结果
     */
    Result openTopicJmx(Long clusterId, String topicName, TopicJmxSwitch jmxSwitch);

    /**
     * 获取优先被选举为controller的broker
     * @param clusterId 集群ID
     * @return 操作结果
     */
    Result<List<Integer>> getControllerPreferredCandidates(Long clusterId);

    /**
     * 增加优先被选举为controller的broker
     * @param clusterId 集群ID
     * @param brokerId brokerId
     * @return
     */
    Result addControllerPreferredCandidate(Long clusterId, Integer brokerId);

    /**
     * 减少优先被选举为controller的broker
     * @param clusterId 集群ID
     * @param brokerId brokerId
     * @return
     */
    Result deleteControllerPreferredCandidate(Long clusterId, Integer brokerId);
}
