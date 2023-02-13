package com.xiaojukeji.kafka.manager.common.entity.vo.rd.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Job日志")
public class JobLogVO {
    @ApiModelProperty(value = "日志ID")
    protected Long id;

    @ApiModelProperty(value = "业务类型")
    private Integer bizType;

    @ApiModelProperty(value = "业务关键字")
    private String bizKeyword;

    @ApiModelProperty(value = "打印时间")
    private Date printTime;

    @ApiModelProperty(value = "内容")
    private String content;
}
