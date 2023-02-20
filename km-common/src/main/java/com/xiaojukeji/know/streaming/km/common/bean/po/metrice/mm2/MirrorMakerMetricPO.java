package com.xiaojukeji.know.streaming.km.common.bean.po.metrice.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.po.metrice.BaseMetricESPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MirrorMakerMetricPO extends BaseMetricESPO {
    private Long        connectClusterId;

    private String      connectorName;

    /**
     * 用于es内部排序
     */
    private String      connectorNameAndClusterId;

    public MirrorMakerMetricPO(Long kafkaClusterPhyId, Long connectClusterId, String connectorName){
        super(kafkaClusterPhyId);
        this.connectClusterId               = connectClusterId;
        this.connectorName                  = connectorName;
        this.connectorNameAndClusterId      = connectorName + "#" + connectClusterId;
    }

    @Override
    public String getKey() {
        return "KCOR@" + clusterPhyId + "@" + connectClusterId + "@" + connectorName + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return String.valueOf(connectClusterId);
    }
}
