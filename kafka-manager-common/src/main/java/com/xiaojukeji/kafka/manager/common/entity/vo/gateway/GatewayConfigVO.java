package com.xiaojukeji.kafka.manager.common.entity.vo.gateway;

/**
 * @author zengqiao
 * @date 20/7/28
 */
public class GatewayConfigVO {
    private String version;

    private String data;

    public GatewayConfigVO(String version, String data) {
        this.version = version;
        this.data = data;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GatewayConfigVO{" +
                "version='" + version + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}