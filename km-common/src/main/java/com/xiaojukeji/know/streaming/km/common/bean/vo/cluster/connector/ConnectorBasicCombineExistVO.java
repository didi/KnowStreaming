package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.connector;

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
public class ConnectorBasicCombineExistVO extends ConnectorBasicVO {
    @ApiModelProperty(value="是否存在", example = "true")
    protected Boolean exist;
}
