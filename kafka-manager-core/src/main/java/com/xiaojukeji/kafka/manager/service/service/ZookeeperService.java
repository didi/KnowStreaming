package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi.TopicJmxSwitch;

/**
 * ZK相关的接口
 * @author tukun
 * @date 2015/11/11.
 */
public interface ZookeeperService {
    Result openTopicJmx(Long clusterId, String topicName, TopicJmxSwitch jmxSwitch);
}
