package com.xiaojukeji.know.streaming.km.common.converter;

import com.xiaojukeji.know.streaming.km.common.bean.entity.partition.Partition;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.*;
import java.util.stream.Collectors;

public class PartitionConverter {
    private PartitionConverter() {
    }

    public static List<Partition> convert2PartitionList(Long clusterPhyId, TopicDescription description) {
        List<Partition> partitionList = new ArrayList<>();
        for (TopicPartitionInfo partitionInfo: description.partitions()) {
            Partition partition = new Partition();
            partition.setClusterPhyId(clusterPhyId);
            partition.setTopicName(description.name());
            partition.setPartitionId(partitionInfo.partition());
            partition.setLeaderBrokerId(partitionInfo.leader().id());
            partition.setInSyncReplicaList(partitionInfo.isr().stream().map(elem -> elem.id()).collect(Collectors.toList()));
            partition.setAssignReplicaList(partitionInfo.replicas().stream().map(elem -> elem.id()).collect(Collectors.toList()));

            partitionList.add(partition);
        }

        return partitionList;
    }
}
