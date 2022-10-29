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
public class ClusterPhysHealthState {
    private Integer unknownCount;

    private Integer goodCount;

    private Integer mediumCount;

    private Integer poorCount;

    private Integer deadCount;

    private Integer total;

    public ClusterPhysHealthState(Integer total) {
        this.unknownCount = 0;
        this.goodCount = 0;
        this.mediumCount = 0;
        this.poorCount = 0;
        this.deadCount = 0;
        this.total = total;
    }
}
