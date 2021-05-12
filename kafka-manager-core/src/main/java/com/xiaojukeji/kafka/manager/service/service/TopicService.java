package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.bizenum.OffsetPosEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.TopicOffsetChangedEnum;
import com.xiaojukeji.kafka.manager.common.entity.Result;
import com.xiaojukeji.kafka.manager.common.entity.ao.*;
import com.xiaojukeji.kafka.manager.common.entity.dto.normal.TopicDataSampleDTO;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.*;
import com.xiaojukeji.kafka.manager.common.entity.metrics.BaseMetrics;
import com.xiaojukeji.kafka.manager.common.entity.pojo.TopicMetricsDO;
import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicBrokerDTO;
import com.xiaojukeji.kafka.manager.common.zookeeper.znode.brokers.PartitionState;
import com.xiaojukeji.kafka.manager.common.entity.pojo.ClusterDO;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Topic相关的接口
 * @author tukun
 * @date 2015/11/11.
 */
public interface TopicService {
    /**
     * 从DB获取监控数据
     */
    List<TopicMetricsDO> getTopicMetricsFromDB(Long clusterId, String topicName, Date startTime, Date endTime);


    List<TopicMetricsDTO> getTopicMetricsFromDB(String appId,
                                                Long clusterId,
                                                String topicName,
                                                Date startTime,
                                                Date endTime);

    /**
     * 获取指定时间段内的峰值的均值流量
     */
    Double getMaxAvgBytesInFromDB(Long clusterId, String topicName, Date startTime, Date endTime);

    /**
     * 获取brokerId下所有的Topic及其对应的PartitionId
     */
    Map<String, List<Integer>> getTopicPartitionIdMap(Long clusterId, Integer brokerId);

    /**
     * 获取 Topic 的 basic-info 信息
     */
    TopicBasicDTO getTopicBasicDTO(Long clusterId, String topicName);

    /**
     * 获取Topic的PartitionState信息
     */
    List<TopicPartitionDTO> getTopicPartitionDTO(ClusterDO clusterDO, String topicName, Boolean needDetail);

    /**
     * 得到topic流量信息
     */
    BaseMetrics getTopicMetricsFromJMX(Long clusterId, String topicName, Integer metricsCode, Boolean byAdd);

    /**
     * 获取Topic的分区的offset
     */
    Map<TopicPartition, Long> getPartitionOffset(ClusterDO clusterDO, String topicName, OffsetPosEnum offsetPosEnum);

    /**
     * 获取Topic概览信息
     */
    List<TopicOverview> getTopicOverviewList(Long clusterId, Integer brokerId);
    List<TopicOverview> getTopicOverviewList(Long clusterId, List<String> topicNameList);

    /**
     * 获取指定时间的offset信息
     */
    List<PartitionOffsetDTO> getPartitionOffsetList(ClusterDO cluster, String topicName, Long timestamp);

    Map<String, List<PartitionState>> getTopicPartitionState(Long clusterId, Integer filterBrokerId);

    /**
     * 数据采样
     */
    List<String> fetchTopicData(ClusterDO clusterDO, String topicName, TopicDataSampleDTO reqObj);
    List<String> fetchTopicData(KafkaConsumer kafkaConsumer, Integer maxMsgNum, Integer timeout, Boolean truncated);

    /**
     * 采样指定分区最新的数据
     */
    List<String> fetchTopicData(KafkaConsumer kafkaConsumer,
                                Integer maxMsgNum,
                                Long maxWaitMs,
                                Boolean truncated,
                                List<TopicPartition> tpList);

    /**
     * 获取Topic历史耗时
     */
    List<TopicMetricsDO> getTopicRequestMetricsFromDB(Long clusterId, String topicName, Date startTime, Date endTime);

    /**
     * 获取topic的broker列表
     */
    List<TopicBrokerDTO> getTopicBrokerList(Long clusterId, String topicName);

    Result<TopicOffsetChangedEnum> checkTopicOffsetChanged(Long physicalClusterId, String topicName, Long latestTime);

}
