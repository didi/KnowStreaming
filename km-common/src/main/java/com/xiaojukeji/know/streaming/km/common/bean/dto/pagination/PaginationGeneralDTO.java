package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationPreciseFilterFieldDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationRangeFilterFieldDTO;
import com.xiaojukeji.know.streaming.km.common.enums.SortTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="通用分页信息")
public class PaginationGeneralDTO extends PaginationBaseDTO {
    @ApiModelProperty(value="排序字段, 传入的字段名同返回的VO里面的字段名")
    private String sortField;

    @ApiModelProperty(value="排序类型[asc|desc]，默认desc", example = "desc")
    private String sortType = SortTypeEnum.DESC.getSortType();

    @Valid
    @ApiModelProperty(value="多字段精确过滤")
    private List<PaginationPreciseFilterFieldDTO> preciseFilterDTOList;

    @Valid
    @ApiModelProperty(value="多字段范围过滤")
    private List<PaginationRangeFilterFieldDTO> rangeFilterDTOList;
}
