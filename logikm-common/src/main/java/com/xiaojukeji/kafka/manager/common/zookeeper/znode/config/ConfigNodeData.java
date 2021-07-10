package com.xiaojukeji.kafka.manager.common.zookeeper.znode.config;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class ConfigNodeData<T> {
    public static final Integer CONFIGDATA_VERSION = 1;

    private T config;

    private Integer version;

    public T getConfig() {
        return config;
    }

    public void setConfig(T config) {
        this.config = config;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "CommonDataDTO{" +
                "config=" + config +
                ", version=" + version +
                '}';
    }
}