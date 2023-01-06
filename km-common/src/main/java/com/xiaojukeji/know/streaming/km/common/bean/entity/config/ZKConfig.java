package com.xiaojukeji.know.streaming.km.common.bean.entity.config;

import com.xiaojukeji.know.streaming.km.common.constant.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 22/02/24
 */
@ApiModel(description = "ZK配置")
public class ZKConfig implements Serializable {
    @ApiModelProperty(value="ZK是否开启secure", example = "false")
    private Boolean openSecure = false;

    @ApiModelProperty(value="ZK的Session超时时间", example = "15000")
    private Integer sessionTimeoutUnitMs = 15000;

    @ApiModelProperty(value="ZK的Request超时时间", example = "5000")
    private Integer requestTimeoutUnitMs = 5000;

    @ApiModelProperty(value="ZK的Request超时时间")
    private Properties otherProps = new Properties();

    public Boolean getOpenSecure() {
        return openSecure != null && openSecure;
    }

    public void setOpenSecure(Boolean openSecure) {
        this.openSecure = openSecure;
    }

    public Integer getSessionTimeoutUnitMs() {
        return sessionTimeoutUnitMs == null? Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS: sessionTimeoutUnitMs;
    }

    public void setSessionTimeoutUnitMs(Integer sessionTimeoutUnitMs) {
        this.sessionTimeoutUnitMs = sessionTimeoutUnitMs;
    }

    public Integer getRequestTimeoutUnitMs() {
        return requestTimeoutUnitMs == null? Constant.DEFAULT_SESSION_TIMEOUT_UNIT_MS: requestTimeoutUnitMs;
    }

    public void setRequestTimeoutUnitMs(Integer requestTimeoutUnitMs) {
        this.requestTimeoutUnitMs = requestTimeoutUnitMs;
    }

    public Properties getOtherProps() {
        return otherProps == null? new Properties() : otherProps;
    }

    public void setOtherProps(Properties otherProps) {
        this.otherProps = otherProps;
    }
}
