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
@ApiModel(description = "指标过滤规则")
public class MonitorStrategyFilterDTO {
    @ApiModelProperty(value = "指标")
    private String tkey;

    @ApiModelProperty(value = "条件")
    private String topt;

    @ApiModelProperty(value = "值")
    private List<String> tval;

    public String getTkey() {
        return tkey;
    }

    public void setTkey(String tkey) {
        this.tkey = tkey;
    }

    public String getTopt() {
        return topt;
    }

    public void setTopt(String topt) {
        this.topt = topt;
    }

    public List<String> getTval() {
        return tval;
    }

    public void setTval(List<String> tval) {
        this.tval = tval;
    }

    @Override
    public String toString() {
        return "MonitorStrategyFilterDTO{" +
                "tkey='" + tkey + '\'' +
                ", topt='" + topt + '\'' +
                ", tval=" + tval +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isBlank(tkey)
                || ValidateUtils.isBlank(topt)
                || ValidateUtils.isEmptyList(tval)) {
            return false;
        }
        return true;
    }
}