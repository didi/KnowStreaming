package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseTimeVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 集群Groups信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "集群Groups状态信息")
public class ClusterGroupsStateVO extends BaseTimeVO {
    @ApiModelProperty(value = "Group数", example = "100")
    private Integer groupCount;

    @ApiModelProperty(value = "active数", example = "50")
    private Integer activeCount;

    @ApiModelProperty(value = "empty数", example = "20")
    private Integer emptyCount;

    @ApiModelProperty(value = "reBalance数", example = "10")
    private Integer reBalanceCount;

    @ApiModelProperty(value = "dead数", example = "20")
    private Integer deadCount;
}
