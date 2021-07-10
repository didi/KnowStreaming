package com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/3/31
 */
@ApiModel(description="逻辑集群信息")
public class LogicClusterVO {
    @ApiModelProperty(value="逻辑集群ID")
    private Long clusterId;

    @ApiModelProperty(value="逻辑集群名称")
    private String clusterName;

    @ApiModelProperty(value="逻辑标识")
    private String clusterIdentification;

    @ApiModelProperty(value="逻辑集群类型, 0:共享集群, 1:独享集群, 2:独立集群")
    private Integer mode;

    @ApiModelProperty(value="逻辑Topic数量")
    private Integer topicNum;

    @ApiModelProperty(value="集群版本")
    private String clusterVersion;

    @ApiModelProperty(value="集群服务地址")
    private String bootstrapServers;

    @ApiModelProperty(value="描述")
    private String description;

    @ApiModelProperty(value="接入时间")
    private Long gmtCreate;

    @ApiModelProperty(value="修改时间")
    private Long gmtModify;

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

    public String getClusterIdentification() {
        return clusterIdentification;
    }

    public void setClusterIdentification(String clusterIdentification) {
        this.clusterIdentification = clusterIdentification;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public String getClusterVersion() {
        return clusterVersion;
    }

    public void setClusterVersion(String clusterVersion) {
        this.clusterVersion = clusterVersion;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Long gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "LogicClusterVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", clusterIdentification='" + clusterIdentification + '\'' +
                ", mode=" + mode +
                ", topicNum=" + topicNum +
                ", clusterVersion='" + clusterVersion + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", description='" + description + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}