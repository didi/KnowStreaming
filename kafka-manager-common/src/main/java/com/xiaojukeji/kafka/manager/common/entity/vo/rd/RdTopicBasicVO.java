package com.xiaojukeji.kafka.manager.common.entity.vo.rd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/6/10
 */
@Data
@ApiModel(description = "Topic基本信息(RD视角)")
public class RdTopicBasicVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "保留时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "应用ID")
    private String appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "Topic属性")
    private Properties properties;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "所属region")
    private List<String> regionNameList;

    @ApiModelProperty(value = "高可用关系：1:主topic, 0:备topic , 其他:非主备topic")
    private Integer haRelation;

    @Override
    public String toString() {
        return "RdTopicBasicVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", retentionTime=" + retentionTime +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", properties=" + properties +
                ", description='" + description + '\'' +
                ", regionNameList=" + regionNameList +
                ", haRelation=" + haRelation +
                '}';
    }
}