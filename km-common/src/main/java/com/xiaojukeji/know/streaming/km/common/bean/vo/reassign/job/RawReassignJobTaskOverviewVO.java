package com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "迁移任务JobTask信息")
public class RawReassignJobTaskOverviewVO {
    @ApiModelProperty(value="JobId")
    private Long rawReassignJobId;

    @ApiModelProperty(value="TaskId")
    private Long rawReassignTaskId;

    @ApiModelProperty(value="集群ID")
    private Long clusterId;

    @ApiModelProperty(value="Topic名称")
    private String topicName;

    @ApiModelProperty(value="分区Id")
    private Integer partitionId;

    @ApiModelProperty(value="当前BrokerId列表")
    private List<Integer> currentBrokerIdList;

    @ApiModelProperty(value="迁移BrokerId列表")
    private List<Integer> reassignBrokerIdList;

    @ApiModelProperty(value="需迁移LogSize", example = "100000")
    private Long reassignLogSizeUnitB;

    @ApiModelProperty(value="已迁移LogSize", example = "100000")
    private Long finishedLogSizeUnitB;

    @ApiModelProperty(value="任务状态", example = "10")
    private Integer status;

    @ApiModelProperty(value="预计剩余时长", example = "10")
    private Long remainTimeUnitSec;
}
