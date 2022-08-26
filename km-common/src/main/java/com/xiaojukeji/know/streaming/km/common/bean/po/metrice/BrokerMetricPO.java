package com.xiaojukeji.know.streaming.km.common.bean.po.metrice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerMetricPO extends BaseMetricESPO {

    private Integer     brokerId;

    private String      host;

    private Integer     port;

    public BrokerMetricPO(Long clusterId, Integer brokerId){
        super(clusterId);
        this.brokerId   = brokerId;
    }

    @Override
    public String getKey() {
        return "B@" + clusterPhyId + "@" + brokerId + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return String.valueOf(brokerId);
    }
}
