package com.xiaojukeji.know.streaming.km.common.bean.dto.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.know.streaming.km.common.bean.dto.pagination.field.PaginationPreciseFilterFieldDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description="多字段精确过滤")
public class PaginationMulPreciseFilterDTO extends PaginationBaseDTO {
    // GET请求格式：
    // filterDTOList[0].fieldValueList=aaa,bbb,ccc&filterDTOList[0].fieldName=clusterName 还需要将这个请求转义才可以
    @ApiModelProperty(value="多字段精确过滤, 传入的字段名同返回的VO里面的字段名")
    private List<PaginationPreciseFilterFieldDTO> preciseFilterDTOList ;
}
