package com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaUserParam extends ClusterPhyParam {
    protected String kafkaUserName;

    public KafkaUserParam(Long clusterPhyId, String kafkaUserName) {
        super(clusterPhyId);
        this.kafkaUserName = kafkaUserName;
    }
}
