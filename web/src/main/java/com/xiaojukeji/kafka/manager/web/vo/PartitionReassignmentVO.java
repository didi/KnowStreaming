package com.xiaojukeji.kafka.manager.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 分区迁移分配
 * @author zengqiao_cn@163.com
 * @date 19/4/16
 */
@ApiModel(value = "PartitionReassignmentVO", description = "分区分配")
public class PartitionReassignmentVO {
    @ApiModelProperty(value = "Topic名称")
    private String topic;

    @ApiModelProperty(value = "分区Id")
    private Integer partition;

    @ApiModelProperty(value = "分配的副本")
    private List<Integer> replicas;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public List<Integer> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<Integer> replicas) {
        this.replicas = replicas;
    }

    @Override
    public String toString() {
        return "ReassignmentVO{" +
                "topic='" + topic + '\'' +
                ", partition=" + partition +
                ", replicas=" + replicas +
                '}';
    }
}