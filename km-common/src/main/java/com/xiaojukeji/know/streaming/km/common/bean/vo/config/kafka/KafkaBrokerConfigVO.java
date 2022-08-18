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
public class KafkaBrokerConfigVO {
    @ApiModelProperty(value = "配置名", example = "retention.ms")
    private String name;

    @ApiModelProperty(value = "配置值", example = "1268888")
    private String value;

    /**
     * 配置源
     * @see org.apache.kafka.common.requests.DescribeConfigsResponse.ConfigSource
     */
    @ApiModelProperty(value = "配置源", example = "1268888")
    private Integer configSource;

    @ApiModelProperty(value = "敏感的配置", example = "false")
    private boolean sensitive;

    @ApiModelProperty(value = "只读的配置", example = "false")
    private boolean readOnly;

    /**
     * 配置源
     * @see org.apache.kafka.common.requests.DescribeConfigsResponse.ConfigType
     */
    @ApiModelProperty(value = "配置value类型", example = "false")
    private Integer configType;

    @ApiModelProperty(value = "配置说明", example = "保存时间")
    private String documentation;

    @ApiModelProperty(value = "已覆写配置", example = "false")
    private Boolean override;

    @ApiModelProperty(value = "独有配置", example = "false")
    private Boolean exclusive;

    @ApiModelProperty(value = "存在差异的配置", example = "false")
    private Boolean differentiated;

    @ApiModelProperty(value = "配置值", example = "1268888")
    private String defaultValue;
}
