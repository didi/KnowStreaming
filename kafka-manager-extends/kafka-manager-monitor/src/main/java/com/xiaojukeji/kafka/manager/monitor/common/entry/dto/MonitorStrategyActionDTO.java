package com.xiaojukeji.kafka.manager.monitor.common.entry.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "通知方式")
public class MonitorStrategyActionDTO {
    @ApiModelProperty(value = "报警组")
    private List<String> notifyGroup;

    @ApiModelProperty(value = "连续报警时间")
    private String converge;

    @ApiModelProperty(value = "回调地址")
    private String callback;

    public List<String> getNotifyGroup() {
        return notifyGroup;
    }

    public void setNotifyGroup(List<String> notifyGroup) {
        this.notifyGroup = notifyGroup;
    }

    public String getConverge() {
        return converge;
    }

    public void setConverge(String converge) {
        this.converge = converge;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    @Override
    public String toString() {
        return "MonitorStrategyActionDTO{" +
                "notifyGroup=" + notifyGroup +
                ", converge='" + converge + '\'' +
                ", callback='" + callback + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isEmptyList(notifyGroup)
                || ValidateUtils.isBlank(converge)) {
            return false;
        }
        callback = ValidateUtils.isNull(callback)? "": callback;
        return true;
    }
}