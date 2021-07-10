package com.xiaojukeji.kafka.manager.task.config;

public class SyncTopic2DBConfig {
    /**
     * 默认的App
     */
    private String defaultAppId;

    /**
     * 进行同步的集群
     */
    private Long clusterId;

    /**
     * 是否增加权限信息, 默认不增加
     */
    private boolean addAuthority;

    public String getDefaultAppId() {
        return defaultAppId;
    }

    public void setDefaultAppId(String defaultAppId) {
        this.defaultAppId = defaultAppId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public boolean isAddAuthority() {
        return addAuthority;
    }

    public void setAddAuthority(boolean addAuthority) {
        this.addAuthority = addAuthority;
    }

    @Override
    public String toString() {
        return "SyncTopic2DBConfig{" +
                "defaultAppId='" + defaultAppId + '\'' +
                ", clusterId=" + clusterId +
                ", addAuthority=" + addAuthority +
                '}';
    }
}
