package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Topic信息
 * @author zengqiao
 * @date 19/4/1
 */
@Data
@ApiModel(description = "Topic信息概览")
public class TopicOverviewVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "保存时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "每秒流入流量(B)")
    private Object byteIn;

    @ApiModelProperty(value = "每秒流出流量(B)")
    private Object byteOut;

    @ApiModelProperty(value = "发送请求数(个/秒)")
    private Object produceRequest;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用ID")
    private String appId;

    @ApiModelProperty(value = "说明")
    private String description;

    @ApiModelProperty(value = "Topic更新时间")
    private Long updateTime;

    @ApiModelProperty(value = "逻辑集群id")
    private Long logicalClusterId;

    @ApiModelProperty(value = "高可用关系：1:主topic, 0:备topic , 其他:非高可用topic")
    private Integer haRelation;

    @Override
    public String toString() {
        return "TopicOverviewVO{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", replicaNum=" + replicaNum +
                ", partitionNum=" + partitionNum +
                ", retentionTime=" + retentionTime +
                ", byteIn=" + byteIn +
                ", byteOut=" + byteOut +
                ", produceRequest=" + produceRequest +
                ", appName='" + appName + '\'' +
                ", appId='" + appId + '\'' +
                ", description='" + description + '\'' +
                ", updateTime=" + updateTime +
                ", logicalClusterId=" + logicalClusterId +
                ", haRelation=" + haRelation +
                '}';
    }
}
