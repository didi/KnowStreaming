package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "集群状态信息")
public class ClusterPhysStateVO {
    @ApiModelProperty(value = "存活集群数", example = "30")
    private Integer liveCount;

    @ApiModelProperty(value = "挂掉集群数", example = "10")
    private Integer downCount;

    @ApiModelProperty(value = "未知状态集群数", example = "10")
    private Integer unknownCount;

    @ApiModelProperty(value = "集群总数", example = "40")
    private Integer total;
}
