package com.xiaojukeji.kafka.manager.service.service;

import com.xiaojukeji.kafka.manager.common.entity.zookeeper.PartitionState;
import com.xiaojukeji.kafka.manager.common.entity.dto.PartitionOffsetDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicBasicDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicOverviewDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.TopicPartitionDTO;
import com.xiaojukeji.kafka.manager.common.entity.metrics.TopicMetrics;
import com.xiaojukeji.kafka.manager.common.entity.po.ClusterDO;
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
    Long calTopicMaxAvgBytesIn(List<TopicMetrics> topicMetricsList, Integer maxAvgBytesInDuration);

    /**
     * 根据时间区间获取Topic监控数据
     */
    List<TopicMetrics> getTopicMetricsByInterval(Long clusterId, String topic, Date startTime, Date endTime);

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
    List<TopicPartitionDTO> getTopicPartitionDTO(ClusterDO cluster, String topicName, Boolean needOffsets);

    /**
     * 得到topic流量信息
     */
    TopicMetrics getTopicMetrics(Long clusterId, String topicName, List<String> specifiedFieldList);

    /**
     * 获取Topic的分区的offset
     */
    Map<TopicPartition, Long> getTopicPartitionOffset(ClusterDO cluster, String topicName);

    /**
     * 获取Topic概览信息
     */
    List<TopicOverviewDTO> getTopicOverviewDTOList(Long clusterId,
                                                   Integer filterBrokerId,
                                                   List<String> filterTopicNameList);

    /**
     * 获取指定时间的offset信息
     */
    List<PartitionOffsetDTO> getPartitionOffsetList(ClusterDO cluster, String topicName, Long timestamp);

    Map<String, List<PartitionState>> getTopicPartitionState(Long clusterId, Integer filterBrokerId);

    /**
     * 数据采样
     */
    List<String> fetchTopicData(ClusterDO cluster, List<TopicPartition> topicPartitionList, int timeout, int maxMsgNum, long offset, boolean truncate);
}
