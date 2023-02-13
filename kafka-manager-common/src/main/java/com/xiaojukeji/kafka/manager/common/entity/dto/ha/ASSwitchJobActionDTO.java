package com.xiaojukeji.kafka.manager.common.entity.dto.ha;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description="Topic信息")
public class ASSwitchJobActionDTO {
    /**
     * @see com.xiaojukeji.kafka.manager.common.bizenum.TaskActionEnum
     */
    @NotBlank(message = "action不允许为空")
    @ApiModelProperty(value = "动作, force")
    private String action;
}
