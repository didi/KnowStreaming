package com.xiaojukeji.know.streaming.km.common.bean.entity.param.kafkauser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaUserReplaceParam extends KafkaUserParam {
    private String kafkaUserToken;

    public KafkaUserReplaceParam(Long clusterPhyId, String kafkaUserName, String kafkaUserToken) {
        super(clusterPhyId, kafkaUserName);
        this.kafkaUserToken = kafkaUserToken;
    }
}
