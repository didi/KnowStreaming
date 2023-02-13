package com.xiaojukeji.kafka.manager.service.service.ha;

import com.xiaojukeji.kafka.manager.common.entity.Result;


/**
 * Topic主备关系管理
 * 不包括ACL，Gateway等信息
 */
public interface HaKafkaUserService {

    Result<Void> setNoneHAInKafka(String zookeeper,  String kafkaUser);

    /**
     * 暂停HA
     */
    Result<Void> stopHAInKafka(String zookeeper,  String kafkaUser);

    /**
     * 激活HA
     */
    Result<Void> activeHAInKafka(String zookeeper, Long activeClusterPhyId, String kafkaUser);
}
