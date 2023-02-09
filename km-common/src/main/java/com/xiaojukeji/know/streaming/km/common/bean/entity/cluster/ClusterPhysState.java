package com.xiaojukeji.know.streaming.km.common.bean.entity.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 集群状态信息
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterPhysState {
    private Integer liveCount;

    private Integer downCount;

    private Integer unknownCount;

    private Integer total;
}
