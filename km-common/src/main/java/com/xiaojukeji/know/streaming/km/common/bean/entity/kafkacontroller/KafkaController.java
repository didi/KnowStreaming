package com.xiaojukeji.know.streaming.km.common.bean.entity.kafkacontroller;

import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class KafkaController implements Serializable {
    /**
     * 物理集群ID
     */
    private Long clusterPhyId;

    /**
     * brokerId
     */
    private Integer brokerId;

    /**
     * 被选举时间
     */
    private Long timestamp;

    public KafkaController(Long clusterPhyId, Integer brokerId, Long timestamp) {
        this.clusterPhyId = clusterPhyId;
        this.brokerId = brokerId;
        this.timestamp = timestamp;
    }

    public boolean alive() {
        return !brokerId.equals(Constant.INVALID_CODE);
    }
}
