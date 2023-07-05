package com.xiaojukeji.know.streaming.km.common.bean.vo.health;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 22/03/01
 */
@Data
@ApiModel(description = "健康检查结果信息")
public class HealthScoreBaseResultVO extends BaseTimeVO {
    /**
     * @see com.xiaojukeji.know.streaming.km.common.enums.health.HealthCheckDimensionEnum
     */
    @ApiModelProperty(value="检查维度", example = "1")
    private Integer dimension;

    @ApiModelProperty(value="检查维度名称", example = "cluster")
    private String dimensionName;

    @ApiModelProperty(value="检查维度前端显示名称", example = "cluster")
    private String dimensionDisplayName;

    @ApiModelProperty(value="检查名称", example = "Group延迟")
    private String configName;

    @ApiModelProperty(value="检查项", example = "Group延迟")
    private String configItem;

    @ApiModelProperty(value="检查说明", example = "Group延迟")
    private String configDesc;

    @ApiModelProperty(value="结果", example = "true")
    private Boolean passed;

    @ApiModelProperty(value="检查配置", example = "")
    private HealthCheckConfigVO checkConfig;
}
