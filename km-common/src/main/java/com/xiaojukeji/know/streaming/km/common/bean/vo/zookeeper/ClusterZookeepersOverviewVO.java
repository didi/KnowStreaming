package com.xiaojukeji.know.streaming.km.common.bean.vo.zookeeper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wyc
 * @date 2022/9/23
 */
@Data
@ApiModel(description = "Zookeeper信息概览")
public class ClusterZookeepersOverviewVO  {
    @ApiModelProperty(value = "主机ip", example = "121.0.0.1")
    private String host;

    @ApiModelProperty(value = "主机存活状态，1：Live，0：Down", example = "1")
    private Integer status;

    @ApiModelProperty(value = "端口号", example = "2416")
    private Integer port;

    @ApiModelProperty(value = "版本", example = "1.1.2")
    private String version;

    @ApiModelProperty(value = "角色", example = "Leader")
    private String role;

}
