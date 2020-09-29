package com.xiaojukeji.kafka.manager.common.entity.ao.cluster;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/7/14
 */
public class ClusterBrokerStatus {
    private List<Integer> brokerReplicaStatusList;

    private List<Integer> brokerBytesInStatusList;

    public List<Integer> getBrokerReplicaStatusList() {
        return brokerReplicaStatusList;
    }

    public void setBrokerReplicaStatusList(List<Integer> brokerReplicaStatusList) {
        this.brokerReplicaStatusList = brokerReplicaStatusList;
    }

    public List<Integer> getBrokerBytesInStatusList() {
        return brokerBytesInStatusList;
    }

    public void setBrokerBytesInStatusList(List<Integer> brokerBytesInStatusList) {
        this.brokerBytesInStatusList = brokerBytesInStatusList;
    }

    @Override
    public String toString() {
        return "ClusterBrokerStatus{" +
                "brokerReplicaStatusList=" + brokerReplicaStatusList +
                ", brokerBytesInStatusList=" + brokerBytesInStatusList +
                '}';
    }
}