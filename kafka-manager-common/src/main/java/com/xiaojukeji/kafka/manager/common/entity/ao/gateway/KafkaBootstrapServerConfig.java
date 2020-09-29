package com.xiaojukeji.kafka.manager.common.entity.ao.gateway;

import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public class KafkaBootstrapServerConfig extends BaseGatewayConfig {
    private Map<Long, List<String>> clusterIdBootstrapServersMap;

    public KafkaBootstrapServerConfig(Long version, Map<Long, List<String>> clusterIdBootstrapServersMap) {
        this.version = version;
        this.clusterIdBootstrapServersMap = clusterIdBootstrapServersMap;
    }

    public Map<Long, List<String>> getClusterIdBootstrapServersMap() {
        return clusterIdBootstrapServersMap;
    }

    public void setClusterIdBootstrapServersMap(Map<Long, List<String>> clusterIdBootstrapServersMap) {
        this.clusterIdBootstrapServersMap = clusterIdBootstrapServersMap;
    }

    @Override
    public String toString() {
        return "KafkaBootstrapServerConfig{" +
                "clusterIdBootstrapServersMap=" + clusterIdBootstrapServersMap +
                ", version=" + version +
                '}';
    }
}