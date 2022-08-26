package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.enums.SortTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="列表分页查询-带过滤条件")
public class PaginationSortDTO extends PaginationBaseDTO {
    @ApiModelProperty(value="排序字段, 传入的字段名同返回的VO里面的字段名", example = "topicName")
    private String sortField;

    @ApiModelProperty(value="排序类型[asc|desc]，默认desc", example = "desc")
    private String sortType = SortTypeEnum.DESC.getSortType();
}
