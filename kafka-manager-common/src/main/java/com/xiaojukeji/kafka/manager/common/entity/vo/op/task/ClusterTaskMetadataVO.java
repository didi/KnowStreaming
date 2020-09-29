package com.xiaojukeji.kafka.manager.common.entity.vo.op.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/27
 */
@ApiModel(value="任务元信息")
public class ClusterTaskMetadataVO {
    @ApiModelProperty(value="任务ID")
    private Long taskId;

    @ApiModelProperty(value="集群ID")
    private Long clusterId;

    @ApiModelProperty(value="集群名称")
    private String clusterName;

    @ApiModelProperty(value="升级的主机列表")
    private List<String> hostList;

    @ApiModelProperty(value="升级的主机暂停点")
    private List<String> pauseHostList;

    @ApiModelProperty(value="回滚主机列表")
    private List<String> rollbackHostList;

    @ApiModelProperty(value="回滚主机暂停点")
    private List<String> rollbackPauseHostList;

    @ApiModelProperty(value="kafka包名")
    private String kafkaPackageName;

    @ApiModelProperty(value="kafka包 MD5")
    private String kafkaPackageMd5;

    @ApiModelProperty(value="server配置文件Id")
    private Long serverPropertiesFileId;

    @ApiModelProperty(value="server配置名")
    private String serverPropertiesName;

    @ApiModelProperty(value="server配置 MD5")
    private String serverPropertiesMd5;

    @ApiModelProperty(value="操作人")
    private String operator;

    @ApiModelProperty(value="创建时间")
    private Long gmtCreate;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getHostList() {
        return hostList;
    }

    public void setHostList(List<String> hostList) {
        this.hostList = hostList;
    }

    public List<String> getPauseHostList() {
        return pauseHostList;
    }

    public void setPauseHostList(List<String> pauseHostList) {
        this.pauseHostList = pauseHostList;
    }

    public List<String> getRollbackHostList() {
        return rollbackHostList;
    }

    public void setRollbackHostList(List<String> rollbackHostList) {
        this.rollbackHostList = rollbackHostList;
    }

    public List<String> getRollbackPauseHostList() {
        return rollbackPauseHostList;
    }

    public void setRollbackPauseHostList(List<String> rollbackPauseHostList) {
        this.rollbackPauseHostList = rollbackPauseHostList;
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

    public Long getServerPropertiesFileId() {
        return serverPropertiesFileId;
    }

    public void setServerPropertiesFileId(Long serverPropertiesFileId) {
        this.serverPropertiesFileId = serverPropertiesFileId;
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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "ClusterTaskMetadataVO{" +
                "taskId=" + taskId +
                ", clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", hostList=" + hostList +
                ", pauseHostList=" + pauseHostList +
                ", rollbackHostList=" + rollbackHostList +
                ", rollbackPauseHostList=" + rollbackPauseHostList +
                ", kafkaPackageName='" + kafkaPackageName + '\'' +
                ", kafkaPackageMd5='" + kafkaPackageMd5 + '\'' +
                ", serverPropertiesFileId=" + serverPropertiesFileId +
                ", serverPropertiesName='" + serverPropertiesName + '\'' +
                ", serverPropertiesMd5='" + serverPropertiesMd5 + '\'' +
                ", operator='" + operator + '\'' +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}