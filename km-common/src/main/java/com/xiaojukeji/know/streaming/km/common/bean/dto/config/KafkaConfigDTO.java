package com.xiaojukeji.know.streaming.km.common.bean.dto.config;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 22/02/28
 */
@Data
@ApiModel(description = "Kafka配置信息")
public class KafkaConfigDTO extends BaseDTO {
    @Min(value = 0, message = "clusterId不允许小于0")
    @ApiModelProperty(value = "集群ID", example = "6")
    private Long clusterId;

    @NotNull(message = "changedProps不允许为空")
    @ApiModelProperty(value = "配置值", example = "{}")
    private Properties changedProps;
}
