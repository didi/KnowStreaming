package com.xiaojukeji.know.streaming.km.common.bean.entity.param.partition;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.TopicPartition;

import java.util.List;

@Data
@NoArgsConstructor
public class BatchPartitionParam extends ClusterPhyParam {
    private List<TopicPartition> tpList;

    public BatchPartitionParam(Long clusterPhyId, List<TopicPartition> tpList) {
        super(clusterPhyId);
        this.tpList = tpList;
    }
}
