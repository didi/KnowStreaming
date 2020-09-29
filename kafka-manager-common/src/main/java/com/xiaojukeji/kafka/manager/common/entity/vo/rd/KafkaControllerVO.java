package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-03-27
 */
@ApiModel(value = "KafkaController信息")
public class KafkaControllerVO  {
    @ApiModelProperty(value = "节点ID")
    private Integer brokerId;

    @ApiModelProperty(value = "节点地址")
    private String host;

    @ApiModelProperty(value = "ZK消息版本")
    private Integer version;

    @ApiModelProperty(value = "变更时间")
    private Long timestamp;

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "KafkaControllerVO{" +
                "brokerId=" + brokerId +
                ", host='" + host + '\'' +
                ", version=" + version +
                ", timestamp=" + timestamp +
                '}';
    }
}