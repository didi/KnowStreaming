package com.xiaojukeji.know.streaming.km.core.service.topic;

import com.xiaojukeji.know.streaming.km.common.bean.entity.cluster.ClusterPhy;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.TopicConfig;
import com.xiaojukeji.know.streaming.km.common.bean.po.topic.TopicPO;
import com.xiaojukeji.know.streaming.km.common.exception.AdminOperateException;
import com.xiaojukeji.know.streaming.km.common.exception.NotExistException;

import java.util.List;
import java.util.Map;

public interface TopicService {
    /**
     * 从Kafka获取数据
     */
    Result<List<Topic>> listTopicsFromKafka(ClusterPhy clusterPhy);
    Map<Integer, List<Integer>> getTopicPartitionMapFromKafka(Long clusterPhyId, String topicName) throws NotExistException, AdminOperateException;

    /**
     * 从DB获取数据
     */
    List<Topic> listTopicsFromDB(Long clusterPhyId);
    List<TopicPO> listTopicPOsFromDB(Long clusterPhyId);
    Topic getTopic(Long clusterPhyId, String topicName);
    List<String> listRecentUpdateTopicNamesFromDB(Long clusterPhyId, Integer time); // 获取集群最近新增Topic的topic名称：time单位为秒

    /**
     * 优先从缓存获取数据
     */
    List<Topic> listTopicsFromCacheFirst(Long clusterPhyId);
    Topic getTopicFromCacheFirst(Long clusterPhyId, String topicName);
    Integer getTopicSizeFromCacheFirst(Long clusterPhyId);
    Integer getReplicaSizeFromCacheFirst(Long clusterPhyId);

    /**
     * 操作DB
     */
    int addNewTopic2DB(TopicPO po);
    int deleteTopicInDB(Long clusterPhyId, String topicName);
    void batchReplaceMetadata(Long clusterPhyId, List<Topic> presentTopicList);
    int batchReplaceChangedConfig(Long clusterPhyId, List<TopicConfig> topicConfigList);
    Result<Void> updatePartitionNum(Long clusterPhyId, String topicName, Integer partitionNum);
}
