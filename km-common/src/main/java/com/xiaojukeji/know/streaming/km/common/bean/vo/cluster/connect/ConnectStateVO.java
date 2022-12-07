package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 集群Connectors状态信息
 * @author zengqiao
 * @date 22/10/17
 */
@Data
@ApiModel(description = "集群Connects状态信息")
public class ConnectStateVO extends BaseVO {
    @ApiModelProperty(value = "健康检查状态", example = "1")
    private Integer healthState;

    @ApiModelProperty(value = "健康检查通过数", example = "1")
    private Integer healthCheckPassed;

    @ApiModelProperty(value = "健康检查总数", example = "1")
    private Integer healthCheckTotal;

    @ApiModelProperty(value = "connect集群数", example = "1")
    private Integer connectClusterCount;

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
