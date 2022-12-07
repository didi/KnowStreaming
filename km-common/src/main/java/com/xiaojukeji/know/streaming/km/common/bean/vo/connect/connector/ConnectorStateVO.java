package com.xiaojukeji.know.streaming.km.common.bean.vo.connect.connector;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wyb
 * @date 2022/11/15
 */
@Data
public class ConnectorStateVO {
    @ApiModelProperty(value = "connect集群ID", example = "1")
    private Long connectClusterId;

    @ApiModelProperty(value = "connector名称", example = "input1")
    private String name;

    @ApiModelProperty(value = "connector类型", example = "source")
    private String type;

    @ApiModelProperty(value = "connector状态", example = "running")
    private String state;

    @ApiModelProperty(value = "总Task数", example = "1")
    private Integer totalTaskCount;

    @ApiModelProperty(value = "存活Task数", example = "1")
    private Integer aliveTaskCount;

    @ApiModelProperty(value = "总Worker数", example = "1")
    private Integer totalWorkerCount;
}
