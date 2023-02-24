package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.PartitionMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import com.xiaojukeji.know.streaming.km.common.bean.entity.record.RecordHeaderKS;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res.ClusterPhyTopicsOverviewVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.TopicMetadataCombineExistVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metadata.TopicMetadataVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricLineVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.metrics.line.MetricMultiLinesVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.TopicRecordVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.topic.partition.TopicPartitionVO;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class TopicVOConverter {
    private TopicVOConverter() {
    }

    public static TopicMetadataCombineExistVO convert2TopicMetadataCombineExistVO(String topicName, Topic topic) {
        TopicMetadataCombineExistVO vo = new TopicMetadataCombineExistVO();
        vo.setTopicName(topicName);

        if (topic == null) {
            vo.setExist(false);
            return vo;
        }

        vo.setPartitionIdList(new ArrayList<>(topic.getPartitionMap().keySet()));
        vo.setReplicaNum(topic.getReplicaNum());
        vo.setPartitionNum(topic.getPartitionNum());
        vo.setType(topic.getType());
        vo.setExist(true);
        return vo;
    }

    public static TopicRecordVO convert2TopicRecordVO(String topicName, ConsumerRecord<String, String> consumerRecord) {
        TopicRecordVO vo = new TopicRecordVO();
        vo.setTopicName(topicName);
        vo.setPartitionId(consumerRecord.partition());
        vo.setOffset(consumerRecord.offset());
        vo.setTimestampUnitMs(consumerRecord.timestamp());
        vo.setKey(consumerRecord.key());
        vo.setValue(consumerRecord.value());
        vo.setHeaderList(new ArrayList<>());
        for (Header header : consumerRecord.headers().toArray()) {
            vo.getHeaderList().add(new RecordHeaderKS(header.key(), new String(header.value(), StandardCharsets.UTF_8)));
        }
        return vo;
    }

    public static List<TopicMetadataVO> convert2TopicMetadataVOList(List<Topic> topicList) {
        if (ValidateUtils.isEmptyList(topicList)) {
            return new ArrayList<>();
        }

        List<TopicMetadataVO> voList = new ArrayList<>();
        for (Topic topic: topicList) {
            voList.add(convert2TopicMetadataVO(topic));
        }
        return voList;
    }

    public static TopicMetadataVO convert2TopicMetadataVO(Topic topic) {
        TopicMetadataVO vo = new TopicMetadataVO();
        vo.setTopicName(topic.getTopicName());
        vo.setPartitionNum(topic.getPartitionNum());
        vo.setPartitionIdList(new ArrayList<>(topic.getPartitionMap().keySet()));
        vo.setReplicaNum(topic.getReplicaNum());
        vo.setType(topic.getType());
        return vo;
    }

    public static List<ClusterPhyTopicsOverviewVO> convert2ClusterPhyTopicsOverviewVOList(List<Topic> topicList, Map<String, TopicMetrics> metricsMap, Set<String> haTopicNameSet) {
        List<ClusterPhyTopicsOverviewVO> voList = new ArrayList<>();
        for (Topic topic: topicList) {
            ClusterPhyTopicsOverviewVO vo = new ClusterPhyTopicsOverviewVO();

            vo.setTopicName(topic.getTopicName());
            vo.setPartitionNum(topic.getPartitionNum());
            vo.setRetentionTimeUnitMs(topic.getRetentionMs() < 0? null: topic.getRetentionMs());
            vo.setReplicaNum(topic.getReplicaNum());
            vo.setDescription(topic.getDescription());
            vo.setCreateTime(new Date(topic.getCreateTime()));
            vo.setUpdateTime(new Date(topic.getUpdateTime()));

            vo.setLatestMetrics(metricsMap.getOrDefault(topic.getTopicName(), new TopicMetrics(topic.getTopicName(), topic.getClusterPhyId())));

            vo.setInMirror(haTopicNameSet.contains(topic.getTopicName()));
            voList.add(vo);
        }

        return voList;
    }

    public static List<ClusterPhyTopicsOverviewVO> supplyMetricLines(List<ClusterPhyTopicsOverviewVO> voList, List<MetricMultiLinesVO> metricMultiLinesVOList) {
        // <topicName, List<metricLine>>
        Map<String, List<MetricLineVO>> metricLineMap = new HashMap<>();
        if (metricMultiLinesVOList == null) {
            metricMultiLinesVOList = new ArrayList<>();
        }
        for (MetricMultiLinesVO multiLinesVO: metricMultiLinesVOList) {
            if (multiLinesVO.getMetricLines() == null) {
                continue;
            }
            for (MetricLineVO metricLineVO: multiLinesVO.getMetricLines()) {
                metricLineMap.putIfAbsent(metricLineVO.getName(), new ArrayList<>());
                metricLineMap.get(metricLineVO.getName()).add(metricLineVO);
            }
        }

        for (ClusterPhyTopicsOverviewVO vo: voList) {
            vo.setMetricLines(metricLineMap.getOrDefault(vo.getTopicName(), new ArrayList<>()));
        }

        return voList;
    }

    public static TopicPartitionVO convert2TopicPartitionVO(Partition partition, PartitionMetrics metrics) {
        TopicPartitionVO vo = new TopicPartitionVO();
        vo.setTopicName(partition.getTopicName());
        vo.setPartitionId(partition.getPartitionId());
        vo.setLatestMetrics(metrics);
        vo.setLeaderBrokerId(partition.getLeaderBrokerId());
        vo.setAssignReplicas(partition.getAssignReplicaList());
        vo.setInSyncReplicas(partition.getInSyncReplicaList());
        return vo;
    }
}
