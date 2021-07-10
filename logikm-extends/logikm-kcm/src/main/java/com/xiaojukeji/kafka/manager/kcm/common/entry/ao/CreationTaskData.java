package com.xiaojukeji.kafka.manager.kcm.common.entry.ao;

import com.xiaojukeji.kafka.manager.common.entity.Result;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/20
 */
public class CreationTaskData {
    private String uuid;

    private Long clusterId;

    private List<String> hostList;

    private List<String> pauseList;

    private String taskType;

    private String kafkaPackageName;

    private String kafkaPackageMd5;

    private String kafkaPackageUrl;

    private String serverPropertiesName;

    private String serverPropertiesMd5;

    private String serverPropertiesUrl;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<String> getHostList() {
        return hostList;
    }

    public void setHostList(List<String> hostList) {
        this.hostList = hostList;
    }

    public List<String> getPauseList() {
        return pauseList;
    }

    public void setPauseList(List<String> pauseList) {
        this.pauseList = pauseList;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getKafkaPackageName() {
        return kafkaPackageName;
    }

    public void setKafkaPackageName(String kafkaPackageName) {
        this.kafkaPackageName = kafkaPackageName;
    }

    public String getKafkaPackageMd5() {
        return kafkaPackageMd5;
    }

    public void setKafkaPackageMd5(String kafkaPackageMd5) {
        this.kafkaPackageMd5 = kafkaPackageMd5;
    }

    public String getKafkaPackageUrl() {
        return kafkaPackageUrl;
    }

    public void setKafkaPackageUrl(String kafkaPackageUrl) {
        this.kafkaPackageUrl = kafkaPackageUrl;
    }

    public String getServerPropertiesName() {
        return serverPropertiesName;
    }

    public void setServerPropertiesName(String serverPropertiesName) {
        this.serverPropertiesName = serverPropertiesName;
    }

    public String getServerPropertiesMd5() {
        return serverPropertiesMd5;
    }

    public void setServerPropertiesMd5(String serverPropertiesMd5) {
        this.serverPropertiesMd5 = serverPropertiesMd5;
    }

    public String getServerPropertiesUrl() {
        return serverPropertiesUrl;
    }

    public void setServerPropertiesUrl(String serverPropertiesUrl) {
        this.serverPropertiesUrl = serverPropertiesUrl;
    }

    @Override
    public String toString() {
        return "CreationTaskData{" +
                "uuid='" + uuid + '\'' +
                ", clusterId=" + clusterId +
                ", hostList=" + hostList +
                ", pauseList=" + pauseList +
                ", taskType='" + taskType + '\'' +
                ", kafkaPackageName='" + kafkaPackageName + '\'' +
                ", kafkaPackageMd5='" + kafkaPackageMd5 + '\'' +
                ", kafkaPackageUrl='" + kafkaPackageUrl + '\'' +
                ", serverPropertiesName='" + serverPropertiesName + '\'' +
                ", serverPropertiesMd5='" + serverPropertiesMd5 + '\'' +
                ", serverPropertiesUrl='" + serverPropertiesUrl + '\'' +
                '}';
    }
}