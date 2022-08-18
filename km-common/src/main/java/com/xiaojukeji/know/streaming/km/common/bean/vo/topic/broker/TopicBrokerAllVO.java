package com.xiaojukeji.know.streaming.km.common.bean.vo.topic.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "Topic所有的Broker信息")
public class TopicBrokerAllVO {
    @ApiModelProperty(value = "总Broker数", example = "5")
    private Integer total;

    @ApiModelProperty(value = "存活Broker数", example = "4")
    private Integer live;

    @ApiModelProperty(value = "挂掉Broker数", example = "1")
    private Integer dead;

    @ApiModelProperty(value = "总Partition数", example = "5")
    private Integer partitionCount;

    @ApiModelProperty(value = "noLeader分区Id列表", example = "[1, 2, 3]")
    private List<Integer> noLeaderPartitionIdList;

    @ApiModelProperty(value = "未同步分区Id列表", example = "[1, 2, 3, 4]")
    private List<Integer> underReplicatedPartitionIdList;

    @ApiModelProperty(value = "Broker的Partition状态信息")
    private List<TopicBrokerSingleVO> brokerPartitionStateList;
}
