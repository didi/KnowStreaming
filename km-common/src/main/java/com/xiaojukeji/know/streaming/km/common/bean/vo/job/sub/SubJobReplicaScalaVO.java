package com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Job-Topic扩缩副本-子任务详情")
public class SubJobReplicaScalaVO extends SubBrokerJobVO {
    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value = "原副本数")
    private Integer oldReplicaNu;

    @ApiModelProperty(value = "新副本数")
    private Integer newReplicaNu;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "需迁移MessageSize")
    private Double totalSize;

    @ApiModelProperty(value = "已完成MessageSize")
    private Double movedSize;

    @ApiModelProperty(value = "预计剩余时长")
    private Long remainTime;

    @ApiModelProperty(value = "子任务成功数")
    private Integer total;

    @ApiModelProperty(value = "子任务成功数")
    private Integer success;

    @ApiModelProperty(value = "子任务失败数")
    private Integer fail;

    @ApiModelProperty(value = "子任务进行数")
    private Integer doing;
}
