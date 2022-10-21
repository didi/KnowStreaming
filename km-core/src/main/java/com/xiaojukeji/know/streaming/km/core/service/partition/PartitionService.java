package com.xiaojukeji.know.streaming.km.core.service.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.partition.PartitionPO;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PartitionService {
    Result<Map<String, List<Partition>>> listPartitionsFromKafka(ClusterPhy clusterPhy);

    List<Partition> listPartitionFromKafkaByClusterTopicName(Long clusterPhyId,String topicName);

    List<Partition> listPartitionByCluster(Long clusterPhyId);
    List<PartitionPO> listPartitionPOByCluster(Long clusterPhyId);

    /**
     * Topic下的分区列表
     */
    List<Partition> listPartitionByTopic(Long clusterPhyId, String topicName);


    /**
     * Broker下的分区列表
     */
    List<Partition> listPartitionByBroker(Long clusterPhyId, Integer brokerId);

    /**
     * 获取具体分区信息
     */
    Partition getPartitionByTopicAndPartitionId(Long clusterPhyId, String topicName, Integer partitionId);


    /**************************************************** 优先从缓存获取分区信息 ****************************************************/

    List<Partition> listPartitionFromCacheFirst(Long clusterPhyId, String topicName);
    Partition getPartitionFromCacheFirst(Long clusterPhyId, String topicName, Integer partitionId);

    /**
     * 获取集群下分区数
     */
    Integer getPartitionSizeByClusterId(Long clusterPhyId);

    Integer getLeaderPartitionSizeByClusterId(Long clusterPhyId);

    Integer getNoLeaderPartitionSizeByClusterId(Long clusterPhyId);

    Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, String topicName, OffsetSpec offsetSpec, Long timestamp);

    Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, String topicName, Integer partitionId, OffsetSpec offsetSpec, Long timestamp);

    int updatePartitions(Long clusterPhyId, String topicName, List<Partition> kafkaPartitionList, List<PartitionPO> dbPartitionList);

    void deletePartitionsIfNotIn(Long clusterPhyId, Set<String> topicNameSet);
}
