package com.xiaojukeji.kafka.manager.common.entity.vo.op.reassign;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/16
 */
@ApiModel(value = "Topic迁移信息")
public class ReassignTopicStatusVO {
    @ApiModelProperty(value = "子任务ID")
    private Long subTaskId;

    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "实际限流(B/s)")
    private Long realThrottle;

    @ApiModelProperty(value = "限流上限(B/s)")
    private Long maxThrottle;

    @ApiModelProperty(value = "限流下限(B/s)")
    private Long minThrottle;

    @ApiModelProperty(value = "完成迁移分区数")
    private Integer completedPartitionNum;

    @ApiModelProperty(value = "总的分区数")
    private Integer totalPartitionNum;

    @ApiModelProperty(value = "分区迁移列表")
    private List<ReassignPartitionStatusVO> reassignList;

    public Long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(Long subTaskId) {
        this.subTaskId = subTaskId;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRealThrottle() {
        return realThrottle;
    }

    public void setRealThrottle(Long realThrottle) {
        this.realThrottle = realThrottle;
    }

    public Long getMaxThrottle() {
        return maxThrottle;
    }

    public void setMaxThrottle(Long maxThrottle) {
        this.maxThrottle = maxThrottle;
    }

    public Long getMinThrottle() {
        return minThrottle;
    }

    public void setMinThrottle(Long minThrottle) {
        this.minThrottle = minThrottle;
    }

    public Integer getCompletedPartitionNum() {
        return completedPartitionNum;
    }

    public void setCompletedPartitionNum(Integer completedPartitionNum) {
        this.completedPartitionNum = completedPartitionNum;
    }

    public Integer getTotalPartitionNum() {
        return totalPartitionNum;
    }

    public void setTotalPartitionNum(Integer totalPartitionNum) {
        this.totalPartitionNum = totalPartitionNum;
    }

    public List<ReassignPartitionStatusVO> getReassignList() {
        return reassignList;
    }

    public void setReassignList(List<ReassignPartitionStatusVO> reassignList) {
        this.reassignList = reassignList;
    }

    @Override
    public String toString() {
        return "ReassignTopicStatusVO{" +
                "subTaskId=" + subTaskId +
                ", clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", status=" + status +
                ", realThrottle=" + realThrottle +
                ", maxThrottle=" + maxThrottle +
                ", minThrottle=" + minThrottle +
                ", completedPartitionNum=" + completedPartitionNum +
                ", totalPartitionNum=" + totalPartitionNum +
                ", reassignList=" + reassignList +
                '}';
    }
}