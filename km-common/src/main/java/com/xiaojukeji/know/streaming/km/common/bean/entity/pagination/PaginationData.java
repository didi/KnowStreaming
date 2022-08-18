package com.xiaojukeji.know.streaming.km.common.bean.entity.pagination;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "分页数据")
public class PaginationData<T> {
    @ApiModelProperty(value = "业务数据")
    private List<T> bizData;

    @ApiModelProperty(value = "分页信息")
    private Pagination pagination;
}
