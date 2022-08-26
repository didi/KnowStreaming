package com.xiaojukeji.know.streaming.km.common.bean.vo.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/22
 */
@Data
@ApiModel(value = "Topic状态信息")
public class TopicStateVO {
    @ApiModelProperty(value = "分区数", example = "100")
    private Integer partitionCount;

    @ApiModelProperty(value = "所有分区都有Leader", example = "true")
    private Boolean allPartitionHaveLeader;

    @ApiModelProperty(value = "副本数", example = "3")
    private Integer replicaFactor;

    @ApiModelProperty(value = "所有副本在Isr中", example = "false")
    private Boolean allReplicaInSync;

    @ApiModelProperty(value = "最小InIsr数", example = "1")
    private Integer minimumIsr;

    @ApiModelProperty(value = "所有分区满足最小InIsr要求", example = "false")
    private Boolean allPartitionMatchAtMinIsr;

    @ApiModelProperty(value = "已compact", example = "false")
    private Boolean compacted;
}
