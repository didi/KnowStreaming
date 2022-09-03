package com.xiaojukeji.know.streaming.km.common.bean.entity.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "Jmx配置")
public class JmxConfig implements Serializable {
    @ApiModelProperty(value="jmx端口", example = "8099")
    private Integer jmxPort;

    @ApiModelProperty(value="最大连接", example = "100")
    private Integer maxConn;

    @ApiModelProperty(value="是否开启SSL，如果开始则username 与 token 必须非空", example = "false")
    private Boolean openSSL;

    @ApiModelProperty(value="SSL情况下的username", example = "Ks-Km")
    private String username;

    @ApiModelProperty(value="SSL情况下的token", example = "KsKmCCY19")
    private String token;

    @ApiModelProperty(value="使用哪个endpoint网络", example = "EXTERNAL")
    private String useWhichEndpoint;
}


