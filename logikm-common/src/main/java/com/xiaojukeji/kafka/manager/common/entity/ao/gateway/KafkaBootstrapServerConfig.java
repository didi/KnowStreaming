package com.xiaojukeji.kafka.manager.common.entity.ao.gateway;

import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public class KafkaBootstrapServerConfig extends BaseGatewayConfig {
    private Map<String, List<String>> clusterIdBootstrapServersMap;

    public KafkaBootstrapServerConfig(Long version, Map<String, List<String>> clusterIdBootstrapServersMap) {
        this.version = version;
        this.clusterIdBootstrapServersMap = clusterIdBootstrapServersMap;
    }

    public Map<String, List<String>> getClusterIdBootstrapServersMap() {
        return clusterIdBootstrapServersMap;
    }

    public void setClusterIdBootstrapServersMap(Map<String, List<String>> clusterIdBootstrapServersMap) {
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