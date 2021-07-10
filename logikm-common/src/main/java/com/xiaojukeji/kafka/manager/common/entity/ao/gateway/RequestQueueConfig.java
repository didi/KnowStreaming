package com.xiaojukeji.kafka.manager.common.entity.ao.gateway;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public class RequestQueueConfig extends BaseGatewayConfig {
    private Long maxRequestQueueSize;

    public RequestQueueConfig(Long version, Long maxRequestQueueSize) {
        this.version = version;
        this.maxRequestQueueSize = maxRequestQueueSize;
    }

    public Long getMaxRequestQueueSize() {
        return maxRequestQueueSize;
    }

    public void setMaxRequestQueueSize(Long maxRequestQueueSize) {
        this.maxRequestQueueSize = maxRequestQueueSize;
    }

    @Override
    public String toString() {
        return "RequestQueueConfig{" +
                "maxRequestQueueSize=" + maxRequestQueueSize +
                ", version=" + version +
                '}';
    }
}