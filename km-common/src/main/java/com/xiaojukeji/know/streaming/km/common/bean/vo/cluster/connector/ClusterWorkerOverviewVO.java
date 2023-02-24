package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 集群Worker信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "Worker概览信息")
public class ClusterWorkerOverviewVO extends BaseVO {
    @ApiModelProperty(value = "Connect集群ID", example = "1")
    private Long connectClusterId;

    @ApiModelProperty(value = "Connect集群名称", example = "know-streaming")
    private String connectClusterName;

    @ApiModelProperty(value = "worker主机", example = "know-streaming")
    private String workerHost;

    @ApiModelProperty(value = "Connector数", example = "10")
    private Integer connectorCount;

    @ApiModelProperty(value = "Task数", example = "10")
    private Integer taskCount;
}
