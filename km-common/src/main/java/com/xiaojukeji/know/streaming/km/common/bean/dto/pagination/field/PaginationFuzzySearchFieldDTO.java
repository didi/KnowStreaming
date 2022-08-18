package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description="模糊搜索")
public class PaginationFuzzySearchFieldDTO {
    @NotBlank(message = "fieldName不允许为空")
    @ApiModelProperty(value="模糊搜索字段名", example = "kafkaUser")
    private String fieldName;

    @NotBlank(message = "fieldValue不允许为空")
    @ApiModelProperty(value="模糊搜索字段值", example = "know-streaming-kafka-user")
    private String fieldValue;
}
