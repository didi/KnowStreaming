package com.xiaojukeji.kafka.manager.common.entity.vo.ha.job;

import com.xiaojukeji.kafka.manager.common.entity.ao.ha.job.HaJobState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Job状态")
public class HaJobStateVO {
    @ApiModelProperty(value = "任务总数")
    private Integer jobNu;

    @ApiModelProperty(value = "运行中的任务数")
    private Integer runningNu;

    @ApiModelProperty(value = "超时运行中的任务数")
    private Integer runningInTimeoutNu;

    @ApiModelProperty(value = "准备好待运行的任务数")
    private Integer waitingNu;

    @ApiModelProperty(value = "运行成功的任务数")
    private Integer successNu;

    @ApiModelProperty(value = "运行失败的任务数")
    private Integer failedNu;

    @ApiModelProperty(value = "进度，[0 - 100]")
    private Integer progress;

    public HaJobStateVO(HaJobState jobState) {
        this.jobNu = jobState.getTotal();
        this.runningNu = jobState.getDoing();
        this.runningInTimeoutNu = jobState.getDoingInTimeout();
        this.waitingNu = 0;
        this.successNu = jobState.getSuccess();
        this.failedNu = jobState.getFailed();

        this.progress = jobState.getProgress();
    }
}
