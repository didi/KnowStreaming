package com.xiaojukeji.kafka.manager.common.entity.vo.normal.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/4/29
 */
@ApiModel(description="Topic元信息")
public class TopicMetadataVO {
    @ApiModelProperty(value="Topic名称")
    private String topicName;

    @ApiModelProperty(value="Topic分区列表")
    private List<Integer> partitionIdList;

    @ApiModelProperty(value="Topic分区数")
    private Integer partitionNum;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public List<Integer> getPartitionIdList() {
        return partitionIdList;
    }

    public void setPartitionIdList(List<Integer> partitionIdList) {
        this.partitionIdList = partitionIdList;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    @Override
    public String toString() {
        return "TopicMetadataVO{" +
                "topicName='" + topicName + '\'' +
                ", partitionIdList=" + partitionIdList +
                ", partitionNum=" + partitionNum +
                '}';
    }
}