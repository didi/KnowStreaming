package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description="列表分页查询-过滤字段信息")
public class PaginationRangeFilterFieldDTO {
    @NotBlank(message = "fieldName不允许为空")
    @ApiModelProperty(value="过滤字段", example = "healthScore")
    private String fieldName;

    @NotBlank(message = "fieldMinValue不允许为空")
    @ApiModelProperty(value="最小值", example = "2")
    private String fieldMinValue;

    @NotBlank(message = "fieldMaxValue不允许为空")
    @ApiModelProperty(value="最大值", example = "100")
    private String fieldMaxValue;
}
