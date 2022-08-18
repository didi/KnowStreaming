package com.xiaojukeji.know.streaming.km.common.bean.dto.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description="Kafka信息")
public class ValidateKafkaDTO {
    @ApiModelProperty(value = "bootstrapServers地址", example = "127.0.0.1:9093")
    private String bootstrapServers;

    @ApiModelProperty(value = "客户端配置")
    private Properties clientProperties;

    @ApiModelProperty(value = "zk地址", example = "127.0.0.1:2181")
    private String zookeeper;
}
