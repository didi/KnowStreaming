package com.xiaojukeji.know.streaming.km.common.bean.entity.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengqiao
 * @date 23/05/19
 */
@Data
@ApiModel(description = "Jmx配置")
public class JmxAuthConfig implements Serializable {
    @ApiModelProperty(value="最大连接", example = "100")
    protected Integer maxConn;

    @ApiModelProperty(value="是否开启SSL，如果开始则username 与 token 必须非空", example = "false")
    protected Boolean openSSL;

    @ApiModelProperty(value="SSL情况下的username", example = "Ks-Km")
    protected String username;

    @ApiModelProperty(value="SSL情况下的token", example = "KsKmCCY19")
    protected String token;
}


