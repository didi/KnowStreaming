package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "集群健康状态信息")
public class ClusterPhysHealthStateVO {
    @ApiModelProperty(value = "未知", example = "30")
    private Integer unknownCount;

    @ApiModelProperty(value = "好", example = "30")
    private Integer goodCount;

    @ApiModelProperty(value = "中", example = "30")
    private Integer mediumCount;

    @ApiModelProperty(value = "差", example = "30")
    private Integer poorCount;

    @ApiModelProperty(value = "down", example = "30")
    private Integer deadCount;

    @ApiModelProperty(value = "总数", example = "150")
    private Integer total;
}
