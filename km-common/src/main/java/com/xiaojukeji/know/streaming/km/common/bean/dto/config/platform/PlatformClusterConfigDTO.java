package com.xiaojukeji.know.streaming.km.common.bean.dto.config.platform;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class PlatformClusterConfigDTO extends BaseDTO {
    @Min(value = 0, message = "clusterId不允许小于0")
    @ApiModelProperty(value = "集群ID", example = "6")
    private Long clusterId;

    @NotBlank(message = "valueGroup不允许空")
    @ApiModelProperty(value = "配置组", example = "3423r43r")
    private String valueGroup;

    @NotBlank(message = "valueName不允许空")
    @ApiModelProperty(value = "配置项的名称", example = "3423r43r")
    private String valueName;

    @NotNull(message = "value不允许为null")
    @ApiModelProperty(value = "配置值", example = "3423r43r")
    private String value;

    @NotNull(message = "description不允许为null")
    @ApiModelProperty(value = "备注", example = "测试")
    private String description;
}
