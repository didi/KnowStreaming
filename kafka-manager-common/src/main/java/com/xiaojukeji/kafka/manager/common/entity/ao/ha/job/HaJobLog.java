package com.xiaojukeji.kafka.manager.common.entity.ao.ha.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Job日志")
public class HaJobLog {
    @ApiModelProperty(value = "日志信息")
    private String log;
}
