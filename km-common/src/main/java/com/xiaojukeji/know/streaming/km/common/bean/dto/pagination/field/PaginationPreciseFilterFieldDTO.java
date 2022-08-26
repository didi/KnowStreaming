package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(description="列表分页查询-过滤字段信息")
public class PaginationPreciseFilterFieldDTO {
    @NotBlank(message = "fieldName不允许为空")
    @ApiModelProperty(value="过滤字段")
    private String fieldName;

    @NotNull(message = "fieldValueList不允许为空")
    @ApiModelProperty(value="过滤值")
    private List<String> fieldValueList;
}
