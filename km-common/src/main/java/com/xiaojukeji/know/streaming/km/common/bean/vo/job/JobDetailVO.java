package com.xiaojukeji.know.streaming.km.common.bean.vo.job;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import com.xiaojukeji.know.streaming.km.common.bean.vo.job.sub.SubJobVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(description = "job详情")
public class JobDetailVO extends BaseTimeVO {

    @ApiModelProperty(value = "任务id")
    private Long id;

    @ApiModelProperty(value = "任务类型")
    private Integer jobType;

    @ApiModelProperty(value = "任务名称")
    private String jobName;

    @ApiModelProperty(value = "任务状态")
    private Integer jobStatus;

    @ApiModelProperty(value = "任务描述")
    private String jobDesc;

    @ApiModelProperty(value = "任务计划执行时间")
    private Date   planTime;

    @ApiModelProperty(value = "任务开始执行时间")
    private Date   startTime;

    @ApiModelProperty(value = "任务完成执行时间")
    private Date   endTime;

    @ApiModelProperty(value = "任务限流值")
    private Double  flowLimit;

    @ApiModelProperty(value = "子任务总数")
    private Integer total;

    @ApiModelProperty(value = "子任务成功数")
    private Integer success;

    @ApiModelProperty(value = "子任务失败数")
    private Integer fail;

    @ApiModelProperty(value = "子任务进行数")
    private Integer doing;

    @ApiModelProperty(value = "子任务列表，不同的子任务的实现不一样，需要具体场景具体处理")
    private List<SubJobVO> subJobs;
}
