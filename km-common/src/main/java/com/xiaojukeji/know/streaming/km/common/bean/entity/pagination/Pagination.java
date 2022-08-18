package com.xiaojukeji.know.streaming.km.common.bean.entity.pagination;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "分页信息")
public class Pagination {
    @ApiModelProperty(value = "总记录数", example = "100")
    private long total;

    @ApiModelProperty(value = "当前页码", example = "0")
    private long pageNo;

    @ApiModelProperty(value = "单页大小", example = "10")
    private long pageSize;

    public Pagination(long total, long pageNo, long pageSize) {
        this.total = total;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
