package com.xiaojukeji.know.streaming.km.common.bean.vo.reassign.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "迁移任务stat信息")
public class RawReassignJobStatVO {
    @ApiModelProperty(value="总数", example = "10")
    private Integer totalSize;

    @ApiModelProperty(value="成功", example = "10")
    private Integer success;

    @ApiModelProperty(value="失败", example = "10")
    private Integer failed;

    @ApiModelProperty(value="运行中", example = "10")
    private Integer doing;
}
