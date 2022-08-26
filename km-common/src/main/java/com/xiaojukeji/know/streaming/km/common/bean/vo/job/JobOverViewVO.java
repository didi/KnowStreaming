package com.xiaojukeji.know.streaming.km.common.bean.vo.job;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "job列表")
public class JobOverViewVO extends BaseTimeVO {

    @ApiModelProperty(value = "任务id")
    private Long id;

    @ApiModelProperty(value = "任务类型")
    private Integer jobType;

    @ApiModelProperty(value = "任务状态")
    private Integer  jobStatus;

    @ApiModelProperty(value = "任务描述")
    private String jobDesc;

    @ApiModelProperty(value = "任务执行对象")
    private String target;

    @ApiModelProperty(value = "子任务运行成功数")
    private Integer success;

    @ApiModelProperty(value = "子任务运行失败数")
    private Integer fail;

    @ApiModelProperty(value = "子任务等待运行数")
    private Integer waiting;

    @ApiModelProperty(value = "子任务正在运行数")
    private Integer doing;

    @ApiModelProperty(value = "子任务总数")
    private Integer total;

    @ApiModelProperty(value = "任务计划执行时间")
    private Date   planTime;

    @ApiModelProperty(value = "任务开始执行时间")
    private Date   startTime;

    @ApiModelProperty(value = "任务提交人")
    private String creator;
}
