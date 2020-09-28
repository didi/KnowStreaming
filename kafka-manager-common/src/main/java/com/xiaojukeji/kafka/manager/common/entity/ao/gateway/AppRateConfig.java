package com.xiaojukeji.kafka.manager.common.entity.ao.gateway;

/**
 * @author zengqiao
 * @date 20/7/29
 */
public class AppRateConfig extends BaseGatewayConfig {
    private Long appRateLimit;

    public AppRateConfig(Long version, Long appRateLimit) {
        this.version = version;
        this.appRateLimit = appRateLimit;
    }

    public Long getAppRateLimit() {
        return appRateLimit;
    }

    public void setAppRateLimit(Long appRateLimit) {
        this.appRateLimit = appRateLimit;
    }

    @Override
    public String toString() {
        return "AppRateConfig{" +
                "appRateLimit=" + appRateLimit +
                ", version=" + version +
                '}';
    }
}