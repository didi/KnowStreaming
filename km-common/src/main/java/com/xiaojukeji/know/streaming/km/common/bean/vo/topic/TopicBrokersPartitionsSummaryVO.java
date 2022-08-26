package com.xiaojukeji.know.streaming.km.common.bean.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "Topic-BrokersPartitions概要信息")
public class TopicBrokersPartitionsSummaryVO {
    @ApiModelProperty(value = "总Broker数", example = "5")
    private Integer brokerCount;

    @ApiModelProperty(value = "存活Broker数", example = "4")
    private Integer liveBrokerCount;

    @ApiModelProperty(value = "挂掉Broker数", example = "1")
    private Integer deadBrokerCount;

    @ApiModelProperty(value = "总Partition数", example = "5")
    private Integer partitionCount;

    @ApiModelProperty(value = "无Leader的Partition数", example = "5")
    private Integer noLeaderPartitionCount;

    @ApiModelProperty(value = "未同步的Partition数", example = "5")
    private Integer underReplicatedPartitionCount;
}
