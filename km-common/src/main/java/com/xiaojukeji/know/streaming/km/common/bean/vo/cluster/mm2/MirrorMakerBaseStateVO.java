package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 集群MM2状态信息
 * @author fengqiongfeng
 * @date 22/12/29
 */
@Data
@ApiModel(description = "集群MM2状态信息")
public class MirrorMakerBaseStateVO extends BaseVO {
    @ApiModelProperty(value = "worker数", example = "1")
    private Integer workerCount;

    @ApiModelProperty(value = "总Task数", example = "1")
    private Integer totalTaskCount;

    @ApiModelProperty(value = "存活Task数", example = "1")
    private Integer aliveTaskCount;
}
