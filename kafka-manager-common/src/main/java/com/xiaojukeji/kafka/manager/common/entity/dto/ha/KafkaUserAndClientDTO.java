package com.xiaojukeji.kafka.manager.common.entity.dto.ha;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description="KafkaUser和ClientId信息")
public class KafkaUserAndClientDTO {
    @NotBlank(message = "kafkaUser不允许为空串")
    @ApiModelProperty(value = "kafkaUser")
    private String kafkaUser;

    @ApiModelProperty(value = "clientId")
    private String clientId;
}
