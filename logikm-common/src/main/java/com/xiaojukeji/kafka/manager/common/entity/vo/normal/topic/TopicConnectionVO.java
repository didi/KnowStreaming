package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhongyuankai,zengqiao
 * @date 20/4/8
 */
@ApiModel(value = "Topic连接信息")
public class TopicConnectionVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "topic名称")
    private String topicName;

    @ApiModelProperty(value = "AppID")
    private String appId;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "主机名")
    private String hostname;

    @ApiModelProperty(value = "客户端类型[consume|produce]")
    private String clientType;

    @ApiModelProperty(value = "客户端版本")
    private String clientVersion;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    @Override
    public String toString() {
        return "TopicConnectionVO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", appId='" + appId + '\'' +
                ", ip='" + ip + '\'' +
                ", hostname='" + hostname + '\'' +
                ", clientType='" + clientType + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                '}';
    }
}
