package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author zengqiao
 * @date 20/6/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BrokerMetrics extends BaseMetrics {
    private Integer     brokerId;

    private String      host;

    private Integer     port;

    public BrokerMetrics(Long clusterPhyId, Integer brokerId){
        super(clusterPhyId);
        this.brokerId   = brokerId;
    }

    public BrokerMetrics(Long clusterPhyId, Integer brokerId, String host, Integer port){
        super(clusterPhyId);
        this.brokerId   = brokerId;
        this.host       = host;
        this.port       = port;
    }

    public static BrokerMetrics initWithMetric(Long clusterPhyId, Integer brokerId, String metric, Float value){
        BrokerMetrics brokerMetrics = new BrokerMetrics();
        brokerMetrics.setClusterPhyId( clusterPhyId );
        brokerMetrics.setBrokerId( brokerId );
        brokerMetrics.putMetric(metric, value);
        return brokerMetrics;
    }

    @Override
    public String unique() {
        return "B@" + clusterPhyId + "@" + brokerId;
    }
}