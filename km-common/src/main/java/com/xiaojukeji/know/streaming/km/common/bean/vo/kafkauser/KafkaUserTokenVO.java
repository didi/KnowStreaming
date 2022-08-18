package com.xiaojukeji.know.streaming.km.common.bean.vo.kafkauser;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KafkaUserTokenVO extends KafkaUserVO {
    @ApiModelProperty(value = "密钥", example = "1235")
    protected String token;

    @ApiModelProperty(value = "是否解密", example = "true")
    private Boolean decrypt;
}
