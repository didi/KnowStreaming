package com.xiaojukeji.know.streaming.km.common.bean.dto.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description="Kafka-BootstrapServers信息")
public class ValidateKafkaBSDTO {
    @NotBlank(message = "zookeeper不允许为空")
    @ApiModelProperty(value = "bootstrapServers地址", example = "127.0.0.1:9093")
    private String bootstrapServers;

    @NotNull(message = "clientProperties不允许为null")
    @ApiModelProperty(value = "客户端配置")
    private Properties clientProperties;
}
