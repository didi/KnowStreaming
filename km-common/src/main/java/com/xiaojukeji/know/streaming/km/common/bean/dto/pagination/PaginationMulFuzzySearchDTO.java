package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationFuzzySearchFieldDTO;
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
public class PaginationMulFuzzySearchDTO extends PaginationBaseDTO {
    @Valid
    @ApiModelProperty(value="模糊搜索字段")
    private List<PaginationFuzzySearchFieldDTO> fuzzySearchDTOList;
}
