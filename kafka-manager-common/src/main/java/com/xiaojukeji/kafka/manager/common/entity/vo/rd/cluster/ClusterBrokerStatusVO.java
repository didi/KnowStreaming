package com.xiaojukeji.kafka.manager.common.entity.vo.rd.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/11
 */
@ApiModel(description="集群Broker状态")
public class ClusterBrokerStatusVO {
    @ApiModelProperty(value = "Broker副本同步状态: 总数, 已同步数, 未同步数")
    private List<Integer> brokerReplicaStatusList;

    @ApiModelProperty(value = "Broker峰值状态: 总数, 60-％, 60-80%, 80-100%, 100+%, 异常")
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
        return "ClusterBrokerStatusVO{" +
                "brokerReplicaStatusList=" + brokerReplicaStatusList +
                ", brokerBytesInStatusList=" + brokerBytesInStatusList +
                '}';
    }
}