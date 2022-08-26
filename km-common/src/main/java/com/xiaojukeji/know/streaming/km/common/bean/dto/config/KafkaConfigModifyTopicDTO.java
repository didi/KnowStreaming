package com.xiaojukeji.know.streaming.km.common.bean.dto.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zengqiao
 * @date 22/02/28
 */
@Data
@ApiModel(description = "Kafka配置信息")
public class KafkaConfigModifyTopicDTO extends KafkaConfigDTO {
    @NotBlank(message = "topicName不允许为空")
    @ApiModelProperty(value = "配置名称", example = "know-streaming")
    private String topicName;
}
