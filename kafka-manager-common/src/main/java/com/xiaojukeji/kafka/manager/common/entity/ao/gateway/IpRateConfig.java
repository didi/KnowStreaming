package com.xiaojukeji.kafka.manager.common.entity.ao.gateway;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public class IpRateConfig extends BaseGatewayConfig {
    private Long ipRateLimit;

    public IpRateConfig(Long version, Long ipRateLimit) {
        this.version = version;
        this.ipRateLimit = ipRateLimit;
    }

    public Long getIpRateLimit() {
        return ipRateLimit;
    }

    public void setIpRateLimit(Long ipRateLimit) {
        this.ipRateLimit = ipRateLimit;
    }

    @Override
    public String toString() {
        return "IpRateConfig{" +
                "ipRateLimit=" + ipRateLimit +
                ", version=" + version +
                '}';
    }
}