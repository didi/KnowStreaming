package com.xiaojukeji.kafka.manager.common.entity.ao.gateway;

import java.util.Map;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public class SpRateConfig extends BaseGatewayConfig {
    private Map<String, Long> spRateMap;

    public SpRateConfig(Long version, Map<String, Long> spRateMap) {
        this.version = version;
        this.spRateMap = spRateMap;
    }

    public Map<String, Long> getSpRateMap() {
        return spRateMap;
    }

    public void setSpRateMap(Map<String, Long> spRateMap) {
        this.spRateMap = spRateMap;
    }

    @Override
    public String toString() {
        return "SpRateConfig{" +
                "spRateMap=" + spRateMap +
                ", version=" + version +
                '}';
    }
}