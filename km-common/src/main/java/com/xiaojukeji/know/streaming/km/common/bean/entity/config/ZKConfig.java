package com.xiaojukeji.know.streaming.km.common.bean.entity.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@Data
@ApiModel(description = "ZK配置")
public class ZKConfig implements Serializable {
    @ApiModelProperty(value="ZK的jmx配置")
    private JmxConfig jmxConfig;

    @ApiModelProperty(value="ZK是否开启secure", example = "false")
    private Boolean openSecure = false;

    @ApiModelProperty(value="ZK的Session超时时间", example = "15000")
    private Long sessionTimeoutUnitMs = 15000L;

    @ApiModelProperty(value="ZK的Request超时时间", example = "5000")
    private Long requestTimeoutUnitMs = 5000L;

    @ApiModelProperty(value="ZK的Request超时时间")
    private Properties otherProps = new Properties();
}
