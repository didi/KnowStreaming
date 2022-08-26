package com.xiaojukeji.know.streaming.km.common.bean.dto.cluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zengqiao
 * @date 20/4/23
 */
@Data
@ApiModel(description = "集群信息接入")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClusterPhyAddDTO extends ClusterPhyBaseDTO {
    @NotBlank(message = "name不允许为空串")
    @ApiModelProperty(value="集群名称", example = "KnowStreaming")
    protected String name;

    @NotNull(message = "description不允许为空")
    @ApiModelProperty(value="描述", example = "测试")
    protected String description;

    @NotBlank(message = "kafkaVersion不允许为空")
    @ApiModelProperty(value="集群的kafka版本", example = "2.5.1")
    protected String kafkaVersion;
}
