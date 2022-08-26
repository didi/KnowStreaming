package com.xiaojukeji.know.streaming.km.common.bean.vo.config.kafka;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/02/28
 */
@Data
@ApiModel(description = "Kafka配置信息")
public class KafkaTopicDefaultConfigVO {
    @ApiModelProperty(value = "配置名", example = "retention.ms")
    protected String name;

    @ApiModelProperty(value = "默认配置值", example = "1268888")
    protected String defaultValue;

    @ApiModelProperty(value = "只读的配置", example = "false")
    protected Boolean readOnly;

    @ApiModelProperty(value = "配置说明", example = "保存时间")
    private String documentation;
}
