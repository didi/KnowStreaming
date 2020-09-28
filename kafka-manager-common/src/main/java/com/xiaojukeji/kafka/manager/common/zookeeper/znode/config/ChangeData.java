package com.xiaojukeji.kafka.manager.common.zookeeper.znode.config;

/**
 * @author zengqiao
 * @date 20/5/14
 */
public class ChangeData {
    private static final Integer CHANGE_DATA_VERSION = 2;

    private String entity_path;

    private Integer version;

    public String getEntity_path() {
        return entity_path;
    }

    public void setEntity_path(String entity_path) {
        this.entity_path = entity_path;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public static ChangeData getChangeData(String entity_path) {
        ChangeData changeData = new ChangeData();
        changeData.setEntity_path(entity_path);
        changeData.setVersion(CHANGE_DATA_VERSION);
        return changeData;
    }

    @Override
    public String toString() {
        return "ConfigChangesData{" +
                "entity_path='" + entity_path + '\'' +
                ", version=" + version +
                '}';
    }
}