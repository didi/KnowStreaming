package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "集群Topic状态信息")
public class ClusterTopicsStateVO extends BaseTimeVO {
    @ApiModelProperty(value = "Topic数", example = "1000")
    private Integer topicCount;

    @ApiModelProperty(value = "分区数", example = "50000")
    private Integer partitionCount;

    @ApiModelProperty(value = "无Leader分区数", example = "100")
    private Integer noLeaderPartitionCount;

    @ApiModelProperty(value = "处于最小Isr的分区数", example = "4000")
    private Integer atMinIsrPartitionCount;

    @ApiModelProperty(value = "未处于最小Isr的分区数", example = "323")
    private Integer underMinIsrPartitionCount;

    @ApiModelProperty(value = "未同步副本数", example = "234")
    private Integer underReplicatedPartitionCount;
}
