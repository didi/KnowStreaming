package com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wyc
 * @date 2022/9/23
 */
@Data
public class ZnodeVO {

    @ApiModelProperty(value = "节点名称", example = "broker")
    private String name;

    @ApiModelProperty(value = "节点数据", example = "saassad")
    private String data;

    @ApiModelProperty(value = "节点属性", example = "")
    private ZnodeStatVO stat;

    @ApiModelProperty(value = "节点路径", example = "/cluster")
    private String namespace;

}
