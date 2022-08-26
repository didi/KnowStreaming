/*
 * Copyright (c) 2015, WINIT and/or its affiliates. All rights reserved. Use, Copy is subject to authorized license.
 */
package com.xiaojukeji.know.streaming.km.common.bean.dto.job;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * WorkTask Vo 对象
 * 
 * @author fengqiongfeng
 * @date 2020-12-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("任务id, 创建时不需要")
    private Long id;

    /**
     * @see com.xiaojukeji.know.streaming.km.common.enums.job.JobTypeEnum
     */
    @ApiModelProperty("任务类型")
    private Integer jobType;

    /**
     * @see com.xiaojukeji.know.streaming.km.common.enums.job.JobStatusEnum
     */
    @ApiModelProperty("任务状态")
    private Integer jobStatus;

    @ApiModelProperty("任务执行对象")
    private String target;

    @ApiModelProperty(value = "任务描述")
    private String jobDesc;

    @NotBlank(message = "creator不允许为空或空串")
    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("计划执行时间")
    private Date planTime;

    @NotBlank(message = "data不允许为空或空串")
    @ApiModelProperty("data")
    private String jobData;
}

