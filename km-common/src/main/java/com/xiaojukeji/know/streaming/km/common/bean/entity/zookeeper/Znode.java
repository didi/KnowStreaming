package com.xiaojukeji.know.streaming.km.common.bean.entity.zookeeper;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.zookeeper.data.Stat;

@Data
public class Znode {
    @ApiModelProperty(value = "节点名称", example = "broker")
    private String name;

    @ApiModelProperty(value = "节点数据", example = "saassad")
    private String data;

    @ApiModelProperty(value = "节点属性", example = "")
    private Stat stat;

    @ApiModelProperty(value = "节点路径", example = "")
    private String namespace;
}
