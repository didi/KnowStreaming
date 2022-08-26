package com.xiaojukeji.know.streaming.km.common.bean.dto.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description="Kafka-ZK信息")
public class ValidateKafkaZKDTO {
    @NotBlank(message = "zookeeper不允许为空")
    @ApiModelProperty(value = "zk地址", example = "127.0.0.1:2181")
    private String zookeeper;
}
