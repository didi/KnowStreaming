package com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Job-Topic迁移-子任务详情")
public class SubJobReplicaMoveVO extends SubBrokerJobVO {
    @ApiModelProperty(value = "topic 名称")
    private String topicName;

    @ApiModelProperty(value = "分区列表")
    private List<Integer> partitions;

    @ApiModelProperty(value = "当前数据保存时间")
    private Long currentTimeSpent;

    @ApiModelProperty(value = "迁移数据时间范围")
    private Long moveTimeSpent;

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
