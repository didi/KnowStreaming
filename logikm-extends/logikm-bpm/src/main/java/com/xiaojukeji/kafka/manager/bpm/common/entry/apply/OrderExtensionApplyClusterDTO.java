package com.xiaojukeji.kafka.manager.bpm.common.entry.apply;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhongyuankai
 * @date 20/4/20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderExtensionApplyClusterDTO {
    @ApiModelProperty(value = "数据中心")
    private String idc;

    @ApiModelProperty(value = "流量流量(B/s)")
    private Long bytesIn;

    @ApiModelProperty(value = "集群模式")
    private Integer mode;

    @ApiModelProperty(value = "所属应用")
    private String appId;

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public Long getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(Long bytesIn) {
        this.bytesIn = bytesIn;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean paramLegal() {
        if (ValidateUtils.isExistBlank(idc) ||
                ValidateUtils.isNullOrLessThanZero(bytesIn) ||
                ValidateUtils.isExistBlank(appId) ||
                ValidateUtils.isNullOrLessThanZero(mode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "OrderExtensionApplyClusterDTO{" +
                "idc='" + idc + '\'' +
                ", bytesIn=" + bytesIn +
                ", mode=" + mode +
                ", appId='" + appId + '\'' +
                '}';
    }
}