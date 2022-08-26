package com.xiaojukeji.know.streaming.km.common.bean.vo.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "Kafka-BS检查结果")
public class KafkaBSValidateVO extends KafkaZKValidateVO {
    @ApiModelProperty(value = "ZK地址", example = "127.0.0.1:2181")
    private String zookeeper;
}
