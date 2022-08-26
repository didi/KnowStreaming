package com.xiaojukeji.know.streaming.km.common.bean.entity.param.reassign;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExecuteReassignParam extends ClusterPhyParam {
    private String reassignmentJson;

    private Long throttleUnitB;

    public ExecuteReassignParam(Long clusterPhyId, String reassignmentJson, Long throttleUnitB) {
        super(clusterPhyId);
        this.reassignmentJson = reassignmentJson;
        this.throttleUnitB = throttleUnitB;
    }
}
