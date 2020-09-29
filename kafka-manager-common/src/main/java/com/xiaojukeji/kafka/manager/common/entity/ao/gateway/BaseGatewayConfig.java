package com.xiaojukeji.kafka.manager.common.entity.ao.gateway;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public class BaseGatewayConfig {
    protected Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "GatewayConfig{" +
                "version=" + version +
                '}';
    }
}