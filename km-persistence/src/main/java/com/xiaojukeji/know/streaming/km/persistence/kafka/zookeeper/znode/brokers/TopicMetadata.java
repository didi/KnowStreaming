package com.xiaojukeji.know.streaming.km.persistence.kafka.zookeeper.znode.brokers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

/**
 * 存储Topic的元信息, 元信息对应的ZK节点是/brokers/topics/${topicName}
 * @author zengqiao
 * @date 19/4/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TopicMetadata implements Serializable {
    /**
     * topic名称
     */
    private String topic;

    /**
     * partition所在的Broker
     */
    private PartitionMap partitionMap;

    /**
     * topic所在的broker, 由partitionMap获取得到
     */
    private Set<Integer> brokerIdSet;

    /**
     * 副本数
     */
    private Integer replicaNum;

    /**
     * 分区数
     */
    private Integer partitionNum;

    /**
     * 修改节点的时间
     */
    private Long modifyTime;

    /**
     * 创建节点的时间
     */
    private Long createTime;
}
