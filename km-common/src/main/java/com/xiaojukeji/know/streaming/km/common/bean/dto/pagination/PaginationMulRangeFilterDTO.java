package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationRangeFilterFieldDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="多字段范围过滤")
public class PaginationMulRangeFilterDTO extends PaginationBaseDTO {
    @ApiModelProperty(value="多字段范围过滤")
    private List<PaginationRangeFilterFieldDTO> rangeFilterDTOList ;
}
