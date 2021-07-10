package com.xiaojukeji.kafka.manager.kcm.common.entry.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.xiaojukeji.kafka.manager.kcm.common.entry.ClusterTaskConstant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/4/21
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = AbstractClusterTaskDTO.TASK_TYPE_PROPERTY_FIELD_NAME,
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ClusterRoleTaskDTO.class, name = ClusterTaskConstant.CLUSTER_ROLE_UPGRADE),
        @JsonSubTypes.Type(value = ClusterHostTaskDTO.class, name = ClusterTaskConstant.CLUSTER_HOST_UPGRADE),
        @JsonSubTypes.Type(value = ClusterHostTaskDTO.class, name = ClusterTaskConstant.CLUSTER_HOST_DEPLOY),
        @JsonSubTypes.Type(value = ClusterHostTaskDTO.class, name = ClusterTaskConstant.CLUSTER_HOST_EXPAND),
})
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="集群任务")
public abstract class AbstractClusterTaskDTO {
    /**
     * AbstractClusterTaskDTO 对象中必须存在 taskType 字段
     */
    public static final String TASK_TYPE_PROPERTY_FIELD_NAME = "taskType";

    @ApiModelProperty(value="集群ID")
    protected Long clusterId;

    @ApiModelProperty(value="任务类型")
    protected String taskType;

    @ApiModelProperty(value="Kafka包名称")
    protected String kafkaPackageName;

    @ApiModelProperty(value="Kafka包Md5")
    protected String kafkaPackageMd5;

    @ApiModelProperty(value="配置名称")
    protected String serverPropertiesName;

    @ApiModelProperty(value="配置Md5")
    protected String serverPropertiesMd5;

    @JsonIgnore
    private Map<String, List<String>> kafkaRoleBrokerHostMap;

    @JsonIgnore
    private String kafkaFileBaseUrl;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
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

    public Map<String, List<String>> getKafkaRoleBrokerHostMap() {
        return kafkaRoleBrokerHostMap;
    }

    public void setKafkaRoleBrokerHostMap(Map<String, List<String>> kafkaRoleBrokerHostMap) {
        this.kafkaRoleBrokerHostMap = kafkaRoleBrokerHostMap;
    }

    public String getKafkaFileBaseUrl() {
        return kafkaFileBaseUrl;
    }

    public void setKafkaFileBaseUrl(String kafkaFileBaseUrl) {
        this.kafkaFileBaseUrl = kafkaFileBaseUrl;
    }

    @Override
    public String toString() {
        return "AbstractClusterTaskDTO{" +
                "clusterId=" + clusterId +
                ", taskType='" + taskType + '\'' +
                ", kafkaPackageName='" + kafkaPackageName + '\'' +
                ", kafkaPackageMd5='" + kafkaPackageMd5 + '\'' +
                ", serverPropertiesName='" + serverPropertiesName + '\'' +
                ", serverPropertiesMd5='" + serverPropertiesMd5 + '\'' +
                ", kafkaRoleBrokerHostMap=" + kafkaRoleBrokerHostMap +
                ", kafkaFileBaseUrl='" + kafkaFileBaseUrl + '\'' +
                '}';
    }

    public abstract boolean paramLegal();
}