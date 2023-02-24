package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 集群MM2状态信息
 * @author zengqiao
 * @date 22/12/12
 */
@Data
@ApiModel(description = "集群MM2状态信息")
public class MirrorMakerStateVO extends BaseVO {

    @ApiModelProperty(value = "MM2数", example = "1")
    private Integer mirrorMakerCount;

    @ApiModelProperty(value = "worker数", example = "1")
    private Integer workerCount;

    @ApiModelProperty(value = "总Connector数", example = "1")
    private Integer totalConnectorCount;

    @ApiModelProperty(value = "存活Connector数", example = "1")
    private Integer aliveConnectorCount;

    @ApiModelProperty(value = "总Task数", example = "1")
    private Integer totalTaskCount;

    @ApiModelProperty(value = "存活Task数", example = "1")
    private Integer aliveTaskCount;
}
