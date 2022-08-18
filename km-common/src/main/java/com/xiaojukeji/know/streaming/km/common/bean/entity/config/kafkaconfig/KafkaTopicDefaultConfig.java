package com.xiaojukeji.know.streaming.km.common.bean.entity.config.kafkaconfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/28
 */
@Data
@ApiModel(description = "Kafka配置信息")
public class KafkaTopicDefaultConfig {
    @ApiModelProperty(value = "配置名", example = "retention.ms")
    private String name;

    @ApiModelProperty(value = "默认配置值", example = "1268888")
    private String defaultValue;

    @ApiModelProperty(value = "只读的配置", example = "false")
    private Boolean readOnly;

    /**
     * 配置源
     * @see org.apache.kafka.common.requests.DescribeConfigsResponse.ConfigType
     */
    @ApiModelProperty(value = "配置value类型", example = "false")
    private Integer configType;

    @ApiModelProperty(value = "配置说明", example = "保存时间")
    private String documentation;
}
