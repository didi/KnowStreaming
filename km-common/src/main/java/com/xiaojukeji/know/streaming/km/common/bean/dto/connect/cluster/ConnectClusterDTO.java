package com.xiaojukeji.know.streaming.km.common.bean.dto.connect.cluster;

import com.xiaojukeji.know.streaming.km.common.bean.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zengqiao
 * @date 2022-10-17
 */
@Data
@ApiModel(description = "集群Connector")
public class ConnectClusterDTO extends BaseDTO {
    @ApiModelProperty(value = "Connect集群ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "Connect集群名称", example = "know-streaming")
    private String name;

    @ApiModelProperty(value = "Connect集群URL", example = "http://127.0.0.1:8080")
    private String clusterUrl;

    @ApiModelProperty(value = "Connect集群版本", example = "2.5.1")
    private String version;

    @ApiModelProperty(value = "JMX配置", example = "")
    private String jmxProperties;
}
