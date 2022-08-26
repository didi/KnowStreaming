package com.xiaojukeji.know.streaming.km.common.bean.entity.param.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.entity.param.VersionItemParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterPhyParam extends VersionItemParam {
    protected Long clusterPhyId;
}
