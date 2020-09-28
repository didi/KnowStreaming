package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/16
 */
public class ReassignPartitionStatusVO {
    @ApiModelProperty(value = "分区Id")
    private Integer partitionId;

    @ApiModelProperty(value = "目标副本ID列表")
    private List<Integer> destReplicaIdList;

    @ApiModelProperty(value = "状态")
    private Integer status;

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(Integer partitionId) {
        this.partitionId = partitionId;
    }

    public List<Integer> getDestReplicaIdList() {
        return destReplicaIdList;
    }

    public void setDestReplicaIdList(List<Integer> destReplicaIdList) {
        this.destReplicaIdList = destReplicaIdList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ReassignPartitionStatusVO{" +
                "partitionId=" + partitionId +
                ", destReplicaIdList=" + destReplicaIdList +
                ", status=" + status +
                '}';
    }
}