package com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "迁移任务Job信息")
public class RawReassignJobOverviewVO {
    @ApiModelProperty(value="JobId")
    private Long rawReassignJobId;

    @ApiModelProperty(value="集群ID")
    private Long clusterId;

    @ApiModelProperty(value="Topic名称")
    private String topicName;

    @ApiModelProperty(value="所有分区列表")
    private List<Integer> allPartitionIdList;

    @ApiModelProperty(value="已迁移完成的分区列表")
    private List<Integer> finishedPartitionIdList;

    @ApiModelProperty(value="当前BrokerId列表")
    private List<Integer> currentBrokerIdList;

    @ApiModelProperty(value="迁移BrokerId列表")
    private List<Integer> reassignBrokerIdList;

    @ApiModelProperty(value="Topic当前数据保存时间", example = "10")
    private Long presentRetentionMs;

    @ApiModelProperty(value="Topic迁移时数据保存时间", example = "10")
    private Long reassignRetentionMs;

    @ApiModelProperty(value="需迁移LogSize", example = "100000")
    private Long reassignLogSizeUnitB;

    @ApiModelProperty(value="已迁移LogSize", example = "100000")
    private Long finishedLogSizeUnitB;

    @ApiModelProperty(value="任务状态", example = "10")
    private Integer status;

    @ApiModelProperty(value="预计剩余时长", example = "10")
    private Long remainTimeUnitSec;
}
