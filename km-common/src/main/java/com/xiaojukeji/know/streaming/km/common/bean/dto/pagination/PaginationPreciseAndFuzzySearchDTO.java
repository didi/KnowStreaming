package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationFuzzySearchFieldDTO;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationPreciseFilterFieldDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * 模糊搜索
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="多字段模糊搜索")
public class PaginationPreciseAndFuzzySearchDTO extends PaginationBaseDTO {
    @Valid
    @ApiModelProperty(value="模糊搜索字段, 传入的字段名同返回的VO里面的字段名")
    private List<PaginationFuzzySearchFieldDTO> fuzzySearchDTOList;

    @Valid
    @ApiModelProperty(value="多字段精确过滤, 传入的字段名同返回的VO里面的字段名")
    private List<PaginationPreciseFilterFieldDTO> preciseFilterDTOList ;
}
