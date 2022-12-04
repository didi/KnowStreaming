package com.xiaojukeji.know.streaming.km.common.bean.entity.param.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.offset.KSOffsetSpec;
import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import com.xiaojukeji.know.streaming.km.common.utils.Triple;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.TopicPartition;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PartitionOffsetParam extends ClusterPhyParam {
    private List<Triple<String, KSOffsetSpec, List<TopicPartition>>> offsetSpecList;

    public PartitionOffsetParam(Long clusterPhyId, String topicName, KSOffsetSpec ksOffsetSpec, List<TopicPartition> partitionList) {
        super(clusterPhyId);
        this.offsetSpecList = Collections.singletonList(new Triple<>(topicName, ksOffsetSpec, partitionList));
    }

    public PartitionOffsetParam(Long clusterPhyId, String topicName, List<KSOffsetSpec> specList, List<TopicPartition> partitionList) {
        super(clusterPhyId);
        this.offsetSpecList = new ArrayList<>();
        specList.forEach(elem -> offsetSpecList.add(new Triple<>(topicName, elem, partitionList)));
    }

    public PartitionOffsetParam(Long clusterPhyId, KSOffsetSpec offsetSpec, List<TopicPartition> partitionList) {
        super(clusterPhyId);
        Map<String, List<TopicPartition>> tpMap = new HashMap<>();
        partitionList.forEach(elem -> {
            tpMap.putIfAbsent(elem.topic(), new ArrayList<>());
            tpMap.get(elem.topic()).add(elem);
        });

        this.offsetSpecList = tpMap.entrySet().stream().map(elem -> new Triple<>(elem.getKey(), offsetSpec, elem.getValue())).collect(Collectors.toList());
    }
}
