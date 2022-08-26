package com.xiaojukeji.know.streaming.km.common.bean.vo.broker;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "BrokerReplica信息")
public class BrokerReplicaSummaryVO {
    @ApiModelProperty(value = "Topic名字", example = "know-streaming")
    private String topicName;

    @ApiModelProperty(value = "分区ID", example = "1")
    private Integer partitionId;

    @ApiModelProperty(value = "leaderBrokerId", example = "12")
    private Integer leaderBrokerId;

    @ApiModelProperty(value = "是否为leader副本", example = "true")
    private Boolean isLeaderReplace;

    @ApiModelProperty(value = "是否在Isr中", example = "true")
    private Boolean inSync;
}
