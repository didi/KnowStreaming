package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connect;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * 集群的Connect集群信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "Connect集群基本信息")
public class ConnectClusterBasicCombineExistVO extends ConnectClusterBasicVO {
    @ApiModelProperty(value="是否存在", example = "true")
    protected Boolean exist;
}
