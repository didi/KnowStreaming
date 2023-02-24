package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector;

import com.xiaojukeji.know.streaming.km.common.bean.vo.BaseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 集群Connector信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "Connector基本信息")
public class ConnectorBasicVO extends BaseVO {
    @ApiModelProperty(value = "Connect集群ID", example = "1")
    private Long connectClusterId;

    @ApiModelProperty(value = "Connect集群名称", example = "know-streaming")
    private String connectClusterName;

    @ApiModelProperty(value = "Connector名称", example = "know-streaming")
    private String connectorName;
}
