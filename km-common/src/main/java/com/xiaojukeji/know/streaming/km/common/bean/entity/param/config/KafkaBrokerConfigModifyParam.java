package com.xiaojukeji.know.streaming.km.common.bean.entity.param.config;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaBrokerConfigModifyParam extends ClusterPhyParam {
    private Integer brokerId;

    private Map<String, String> changedProps;

    private Boolean applyAll;

    public KafkaBrokerConfigModifyParam(Long clusterPhyId, Integer brokerId, Map<String, String> changedProps, Boolean applyAll) {
        super(clusterPhyId);
        this.brokerId = brokerId;
        this.changedProps = changedProps;
        this.applyAll = applyAll;
    }

}
