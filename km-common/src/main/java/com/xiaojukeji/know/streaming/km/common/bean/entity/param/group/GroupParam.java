package com.xiaojukeji.know.streaming.km.common.bean.entity.param.group;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster.ClusterPhyParam;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupParam extends ClusterPhyParam {
    protected String groupName;

    public GroupParam(Long clusterPhyId, String groupName) {
        super(clusterPhyId);
        this.groupName = groupName;
    }
}
