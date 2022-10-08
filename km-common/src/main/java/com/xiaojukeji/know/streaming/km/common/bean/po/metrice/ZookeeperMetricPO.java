package com.xiaojukeji.know.streaming.km.common.bean.po.metrice;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

@Data
@NoArgsConstructor
public class ZookeeperMetricPO extends BaseMetricESPO {
    public ZookeeperMetricPO(Long clusterPhyId){
        super(clusterPhyId);
    }

    @Override
    public String getKey() {
        return "ZK@" + clusterPhyId + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return String.valueOf(clusterPhyId);
    }
}
