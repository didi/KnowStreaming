package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "集群信息")
public class ClusterPhyBaseCombineExistVO extends ClusterPhyBaseVO {
    @ApiModelProperty(value="是否存在", example = "true")
    protected Boolean exist;
}
