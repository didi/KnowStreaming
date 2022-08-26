package com.xiaojukeji.know.streaming.km.common.bean.vo.job;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "job 模块状态")
public class JobStateVO extends BaseVO {

    @ApiModelProperty(value = "任务总数")
    private Integer jobNu;

    @ApiModelProperty(value = "运行中的任务数")
    private Integer runningNu;

    @ApiModelProperty(value = "准备好待运行的任务数")
    private Integer waitingNu;

    @ApiModelProperty(value = "运行成功的任务数")
    private Integer successNu;

    @ApiModelProperty(value = "运行失败的任务数")
    private Integer failedNu;
}
