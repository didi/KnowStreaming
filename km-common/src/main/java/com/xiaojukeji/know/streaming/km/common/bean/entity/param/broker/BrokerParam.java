package com.xiaojukeji.know.streaming.km.common.bean.entity.param.broker;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerParam extends ClusterPhyParam {
    protected Integer brokerId;

    public BrokerParam(Long clusterPhyId, Integer brokerId) {
        super(clusterPhyId);
        this.brokerId = brokerId;
    }
}
