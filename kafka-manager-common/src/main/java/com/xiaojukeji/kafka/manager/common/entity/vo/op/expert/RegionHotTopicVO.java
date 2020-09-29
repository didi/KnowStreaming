package com.xiaojukeji.kafka.manager.common.entity.vo.op.expert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/3/20
 */
@ApiModel(description = "Region热点Topic")
public class RegionHotTopicVO {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "集群名称")
    private String clusterName;

//    @ApiModelProperty(value = "RegionID")
//    private Long regionId;
//
//    @ApiModelProperty(value = "Region名称")
//    private String regionName;

    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "失衡详情")
    private List<BrokerIdPartitionNumVO> detailList;

    @ApiModelProperty(value = "Topic保存时间")
    private Long retentionTime;

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

    public List<BrokerIdPartitionNumVO> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<BrokerIdPartitionNumVO> detailList) {
        this.detailList = detailList;
    }

    public Long getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Long retentionTime) {
        this.retentionTime = retentionTime;
    }

    @Override
    public String toString() {
        return "RegionHotTopicVO{" +
                "clusterId=" + clusterId +
                ", clusterName='" + clusterName + '\'' +
                ", topicName='" + topicName + '\'' +
                ", detailList=" + detailList +
                ", retentionTime=" + retentionTime +
                '}';
    }
}