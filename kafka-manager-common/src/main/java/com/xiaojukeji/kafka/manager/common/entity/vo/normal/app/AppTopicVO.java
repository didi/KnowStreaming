package com.xiaojukeji.kafka.manager.common.entity.vo.normal.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/7
 */
@ApiModel(description="AppTopic")
public class AppTopicVO {
    @ApiModelProperty(value = "逻辑集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "逻辑集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "权限: 0:无权限, 1:可消费 2:可发送 3:可发送消费 4:可管理")
    private Integer access;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "权限授予时间")
    private Long gmtCreate;

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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
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
        return "AppTopicVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", access=" + access +
                ", operator='" + operator + '\'' +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}