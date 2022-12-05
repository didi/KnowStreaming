package com.xiaojukeji.know.streaming.km.common.converter;

import com.alibaba.fastjson.TypeReference;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.topic.TopicCreateParam;
import com.xiaojukeji.know.streaming.km.common.bean.entity.topic.Topic;
import com.xiaojukeji.know.streaming.km.common.bean.po.topic.TopicPO;
import com.xiaojukeji.know.streaming.km.common.enums.topic.TopicTypeEnum;
import com.xiaojukeji.know.streaming.km.common.utils.CommonUtils;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.TopicConfig;

import java.util.*;
import java.util.stream.Collectors;

public class TopicConverter {
    private TopicConverter() {
    }

    public static Topic convert2Topic(TopicPO po) {
        if (po == null) {
            return null;
        }

        Topic topic = ConvertUtil.obj2Obj(po, Topic.class);

        topic.setBrokerIdSet(new HashSet<>(CommonUtils.string2IntList(po.getBrokerIds())));
        topic.setCreateTime(po.getCreateTime().getTime());
        topic.setUpdateTime(po.getUpdateTime().getTime());

        Map<Integer, List<Integer>> partitionMap = ConvertUtil.str2ObjByJson( po.getPartitionMap(), new TypeReference<Map<Integer, List<Integer>>>(){} );
        if (partitionMap != null) {
            topic.setPartitionMap(partitionMap);
        } else {
            topic.setPartitionMap(new HashMap<>());
        }

        return topic;
    }

    public static List<Topic> convert2TopicList(List<TopicPO> poList) {
        if (poList == null) {
            return new ArrayList<>();
        }

        List<Topic> topicList = new ArrayList<>();
        for (TopicPO po: poList) {
            topicList.add(convert2Topic(po));
        }
        return topicList;
    }

    /**
     * 仅合并Topic的元信息部分，业务信息和配置信息部分不合并
     */
    public static TopicPO mergeAndOnlyMetadata2NewTopicPO(Topic newTopicData, TopicPO oldDBTopicPO) {
        TopicPO newTopicPO = new TopicPO();
        newTopicPO.setId(oldDBTopicPO != null? oldDBTopicPO.getId(): null);

        newTopicPO.setClusterPhyId(newTopicData.getClusterPhyId());
        newTopicPO.setTopicName(newTopicData.getTopicName());
        newTopicPO.setPartitionNum(newTopicData.getPartitionNum());
        newTopicPO.setReplicaNum(newTopicData.getReplicaNum());
        newTopicPO.setBrokerIds(CommonUtils.intList2String(new ArrayList<>(newTopicData.getBrokerIdSet())));
        newTopicPO.setType(newTopicData.getType());
        newTopicPO.setPartitionMap(ConvertUtil.obj2Json(newTopicData.getPartitionMap()));

        if (newTopicData.getCreateTime() != null) {
            newTopicPO.setCreateTime(new Date(newTopicData.getCreateTime()));
            newTopicPO.setUpdateTime(new Date(newTopicData.getUpdateTime()));
        } else {
            newTopicPO.setCreateTime(oldDBTopicPO != null? oldDBTopicPO.getCreateTime(): new Date());
            newTopicPO.setUpdateTime(oldDBTopicPO != null? oldDBTopicPO.getUpdateTime(): new Date());
        }

        newTopicPO.setDescription(oldDBTopicPO != null? oldDBTopicPO.getDescription(): null);
        newTopicPO.setRetentionMs(oldDBTopicPO != null? oldDBTopicPO.getRetentionMs(): null);
        return newTopicPO;
    }

    public static TopicPO convert2TopicPOAndIgnoreTime(TopicCreateParam createParam) {
        if (createParam == null) {
            return null;
        }

        TopicPO po = new TopicPO();

        // 元信息
        po.setClusterPhyId(createParam.getClusterPhyId());
        po.setTopicName(createParam.getTopicName());
        po.setPartitionNum(createParam.getAssignmentMap().size());
        po.setReplicaNum(createParam.getReplicaNum());
        po.setBrokerIds(CommonUtils.intList2String(new ArrayList<>(createParam.getBrokerIdSet())));
        po.setPartitionMap(ConvertUtil.obj2Json(createParam.getAssignmentMap()));
        po.setType(TopicTypeEnum.getTopicTypeCode(createParam.getTopicName()));

        // 配置
        Long retentionMs = ConvertUtil.string2Long(createParam.getConfig().get(TopicConfig.RETENTION_MS_CONFIG));
        po.setRetentionMs(retentionMs == null? -1L: retentionMs);

        // 业务信息
        po.setDescription(createParam.getDescription());

        return po;
    }

    public static Topic convert2Topic(Long clusterPhyId, TopicDescription description) {
        Map<Integer, List<Integer>> partitionMap = new HashMap<>();
        Set<Integer> brokerIdSet = new HashSet<>();

        for (TopicPartitionInfo partitionInfo: description.partitions()) {
            partitionMap.put(partitionInfo.partition(), partitionInfo.replicas().stream().map(elem -> elem.id()).collect(Collectors.toList()));
            brokerIdSet.addAll(partitionMap.get(partitionInfo.partition()));
        }

        Topic metadata = new Topic();
        metadata.setClusterPhyId(clusterPhyId);
        metadata.setTopicName(description.name());
        metadata.setPartitionMap(partitionMap);
        metadata.setBrokerIdSet(brokerIdSet);
        metadata.setReplicaNum(description.partitions().get(0).replicas().size());
        metadata.setPartitionNum(partitionMap.size());
        metadata.setType(TopicTypeEnum.getTopicTypeCode(description.name()));
        metadata.setUpdateTime(null);
        metadata.setCreateTime(null);
        return metadata;
    }
}
