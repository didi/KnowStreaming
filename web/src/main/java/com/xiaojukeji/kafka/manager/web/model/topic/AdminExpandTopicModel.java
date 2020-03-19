package com.xiaojukeji.kafka.manager.web.model.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/1/2
 */
@ApiModel(value = "AdminExpandTopicModel")
public class AdminExpandTopicModel {
    @ApiModelProperty(value = "集群ID")
    private Long clusterId;

    @ApiModelProperty(value = "topicName名称")
    private String topicName;

    @ApiModelProperty(value = "分区数")
    private Integer partitionNum;

    @ApiModelProperty(value = "brokerId列表")
    private List<Integer> brokerIdList;

    @ApiModelProperty(value = "regionId列表")
    private List<Long> regionIdList;

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

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public List<Integer> getBrokerIdList() {
        return brokerIdList;
    }

    public void setBrokerIdList(List<Integer> brokerIdList) {
        this.brokerIdList = brokerIdList;
    }

    public List<Long> getRegionIdList() {
        return regionIdList;
    }

    public void setRegionIdList(List<Long> regionIdList) {
        this.regionIdList = regionIdList;
    }

    @Override
    public String toString() {
        return "AdminExpandTopicModel{" +
                "clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", partitionNum=" + partitionNum +
                ", brokerIdList=" + brokerIdList +
                ", regionIdList=" + regionIdList +
                '}';
    }

    public Boolean legal() {
        if (clusterId == null
                || StringUtils.isEmpty(topicName)
                || partitionNum == null || partitionNum <= 0) {
            return false;
        }
        if ((brokerIdList == null || brokerIdList.isEmpty()) && (regionIdList == null || regionIdList.isEmpty())) {
            return false;
        }
        return true;
    }
}