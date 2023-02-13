package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Topic的基本信息
 * @author zengqiao
 * @date 19/4/1
 */
@Data
@ApiModel(description = "Topic基本信息")
public class TopicBasicVO {
    @ApiModelProperty(value = "集群id")
    private Long clusterId;

    @ApiModelProperty(value = "应用id")
    private String appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "副本数")
    private Integer replicaNum;

    @ApiModelProperty(value = "负责人")
    private String principals;

    @ApiModelProperty(value = "存储时间(ms)")
    private Long retentionTime;

    @ApiModelProperty(value = "单分区数据保存大小(Byte)")
    private Long retentionBytes;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "健康分")
    private Integer score;

    @ApiModelProperty(value = "压缩格式")
    private String topicCodeC;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "集群地址")
    private String bootstrapServers;

    @ApiModelProperty(value = "所属region")
    private List<String> regionNameList;

    @ApiModelProperty(value = "高可用关系：1:主topic, 0:备topic , 其他:非主备topic")
    private Integer haRelation;

    @Override
    public String toString() {
        return "TopicBasicVO{" +
                "clusterId=" + clusterId +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", partitionNum=" + partitionNum +
                ", replicaNum=" + replicaNum +
                ", principals='" + principals + '\'' +
                ", retentionTime=" + retentionTime +
                ", retentionBytes=" + retentionBytes +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", score=" + score +
                ", topicCodeC='" + topicCodeC + '\'' +
                ", description='" + description + '\'' +
                ", bootstrapServers='" + bootstrapServers + '\'' +
                ", regionNameList=" + regionNameList +
                ", haRelation=" + haRelation +
                '}';
    }
}
