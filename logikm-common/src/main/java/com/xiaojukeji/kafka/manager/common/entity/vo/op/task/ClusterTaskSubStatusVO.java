package com.xiaojukeji.kafka.manager.common.entity.vo.op.task;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/27
 */
@ApiModel(value="子任务状态")
public class ClusterTaskSubStatusVO {
    @ApiModelProperty(value="主机名")
    private String hostname;

    @ApiModelProperty(value="子任务状态")
    private Integer status;

    @ApiModelProperty(value="角色")
    private String kafkaRoles;

    @ApiModelProperty(value="分组ID")
    private Integer groupId;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getKafkaRoles() {
        return kafkaRoles;
    }

    public void setKafkaRoles(String kafkaRoles) {
        this.kafkaRoles = kafkaRoles;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "ClusterTaskSubStatusVO{" +
                "hostname='" + hostname + '\'' +
                ", status=" + status +
                ", kafkaRoles='" + kafkaRoles + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}