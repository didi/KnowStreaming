package com.xiaojukeji.know.streaming.km.common.bean.po.metrice.connect;

import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BaseMetricESPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectClusterMetricPO extends BaseMetricESPO {
    private Long     connectClusterId;

    public ConnectClusterMetricPO(Long kafkaClusterPhyId, Long connectClusterId){
        super(kafkaClusterPhyId);
        this.connectClusterId   = connectClusterId;
    }

    @Override
    public String getKey() {
        return "KCC@" + clusterPhyId + "@" + connectClusterId + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return String.valueOf(connectClusterId);
    }
}
