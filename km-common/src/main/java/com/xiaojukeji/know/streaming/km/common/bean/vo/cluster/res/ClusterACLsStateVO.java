package com.xiaojukeji.know.streaming.km.common.bean.vo.cluster.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 集群Topic信息
 * @author zengqiao
 * @date 22/02/23
 */
@Data
@ApiModel(description = "集群ACL状态信息")
public class ClusterACLsStateVO {
    @ApiModelProperty(value = "开启ACL", example = "false")
    private Boolean openAcl;
}
