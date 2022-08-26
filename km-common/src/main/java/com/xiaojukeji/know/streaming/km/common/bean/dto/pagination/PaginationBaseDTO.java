package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.constant.PaginationConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description="列表分页查询的BaseDTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationBaseDTO {
    @NotNull(message = "pageNo不允许为空")
    @ApiModelProperty(value="页面位置，默认1", example = "1")
    private Integer pageNo = PaginationConstant.DEFAULT_PAGE_NO;

    @NotNull(message = "pageSize不允许为空")
    @ApiModelProperty(value="页面大小，默认10", example = "10")
    private Integer pageSize = PaginationConstant.DEFAULT_PAGE_SIZE;

    @ApiModelProperty(value="模糊搜索", example = "")
    private String searchKeywords;
}
