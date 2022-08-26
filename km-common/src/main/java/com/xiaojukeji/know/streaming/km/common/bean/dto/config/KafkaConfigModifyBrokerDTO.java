package com.xiaojukeji.know.streaming.km.common.bean.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @author zengqiao
 * @date 22/02/28
 */
@Data
@ApiModel(description = "Kafka配置信息")
public class KafkaConfigModifyBrokerDTO extends KafkaConfigDTO {
    @Min(value = 0, message = "brokerId不允许小于0")
    @ApiModelProperty(value = "BrokerId", example = "1")
    private Integer brokerId;

    @ApiModelProperty(value = "应用到全部", example = "false")
    private Boolean applyAll;
}
