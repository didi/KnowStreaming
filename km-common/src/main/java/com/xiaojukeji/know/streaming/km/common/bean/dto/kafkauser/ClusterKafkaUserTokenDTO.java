package com.xiaojukeji.know.streaming.km.common.bean.dto.kafkauser;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description="kafkaUser密码信息")
public class ClusterKafkaUserTokenDTO extends ClusterKafkaUserDTO {
    @NotBlank(message = "token不允许为空串")
    @ApiModelProperty(value = "密码", example = "12313224cerce32r344rC")
    private String token;

    @NotNull(message = "authType不允许为空")
    @ApiModelProperty(value = "认证类型", example = "1300")
    private Integer authType;
}