package com.xiaojukeji.know.streaming.km.common.bean.vo.job;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description = "job列表")
public class JobModifyDetailVO extends BaseTimeVO {

    @ApiModelProperty(value = "任务id")
    private Long id;

    @ApiModelProperty(value = "任务类型")
    private Integer jobType;

    @ApiModelProperty(value = "任务状态")
    private Integer jobStatus;

    @ApiModelProperty(value = "任务描述")
    private String jobDesc;

    @ApiModelProperty(value = "任务数据")
    private String  jobData;

    @ApiModelProperty(value = "任务计划开始执行时间")
    private Date planTime;
}
