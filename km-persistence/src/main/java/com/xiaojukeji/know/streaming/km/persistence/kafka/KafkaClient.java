package com.xiaojukeji.know.streaming.km.persistence.kafka;

import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;

public interface KafkaClient<T> {
    /**
     * 获取Kafka客户端
     * @param clusterPhyId 物理集群ID
     * @return
     */
    T getClient(Long clusterPhyId) throws NotExistException;
}
