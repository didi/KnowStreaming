package com.xiaojukeji.know.streaming.km.core.service.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.offset.KSOffsetSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.po.partition.PartitionPO;
import com.xiaojukeji.know.streaming.km.common.utils.Tuple;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PartitionService {
    /**
     * 从Kafka获取分区信息
     */
    Result<Map<String, List<Partition>>> listPartitionsFromKafka(ClusterPhy clusterPhy);
    Result<List<Partition>> listPartitionsFromKafka(ClusterPhy clusterPhy, String topicName);

    /**
     * 从DB获取分区信息
     */
    List<Partition> listPartitionByCluster(Long clusterPhyId);
    List<PartitionPO> listPartitionPOByCluster(Long clusterPhyId);
    List<Partition> listPartitionByTopic(Long clusterPhyId, String topicName);
    Partition getPartitionByTopicAndPartitionId(Long clusterPhyId, String topicName, Integer partitionId);
    
    /**
     * 优先从缓存获取分区信息，缓存中没有时，从DB获取分区信息
     */
    List<Partition> listPartitionFromCacheFirst(Long clusterPhyId);
    List<Partition> listPartitionFromCacheFirst(Long clusterPhyId, Integer brokerId);
    List<Partition> listPartitionFromCacheFirst(Long clusterPhyId, String topicName);
    Partition getPartitionFromCacheFirst(Long clusterPhyId, String topicName, Integer partitionId);

    /**
     * 获取分区Offset信息
     */
    Result<Map<TopicPartition, Long>> getAllPartitionOffsetFromKafka(Long clusterPhyId, KSOffsetSpec offsetSpec);
    Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, String topicName, KSOffsetSpec offsetSpec);
    Result<Tuple<Map<TopicPartition, Long>/*begin offset*/, Map<TopicPartition, Long>/*end offset*/>> getPartitionBeginAndEndOffsetFromKafka(Long clusterPhyId, String topicName);
    Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, String topicName, Integer partitionId, KSOffsetSpec offsetSpec);
    Result<Map<TopicPartition, Long>> getPartitionOffsetFromKafka(Long clusterPhyId, List<TopicPartition> tpList, KSOffsetSpec offsetSpec);

    /**
     * 修改分区信息
     */
    int updatePartitions(Long clusterPhyId, String topicName, List<Partition> kafkaPartitionList, List<PartitionPO> dbPartitionList);
    void deletePartitionsIfNotIn(Long clusterPhyId, Set<String> topicNameSet);
}
