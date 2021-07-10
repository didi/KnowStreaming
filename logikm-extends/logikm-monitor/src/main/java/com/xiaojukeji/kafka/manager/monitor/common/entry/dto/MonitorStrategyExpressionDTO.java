package com.xiaojukeji.kafka.manager.monitor.common.entry.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/4
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "报警规则")
public class MonitorStrategyExpressionDTO {
    @ApiModelProperty(value = "指标名")
    private String metric;

    @ApiModelProperty(value = "计算规则")
    private String func;

    @ApiModelProperty(value = "操作符")
    private String eopt;

    @ApiModelProperty(value = "阈值")
    private Long threshold;

    @ApiModelProperty(value = "参数见规则描述")
    private String params;

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getEopt() {
        return eopt;
    }

    public void setEopt(String eopt) {
        this.eopt = eopt;
    }

    public Long getThreshold() {
        return threshold;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "MonitorStrategyExpressionDTO{" +
                "metric='" + metric + '\'' +
                ", func='" + func + '\'' +
                ", eopt='" + eopt + '\'' +
                ", threshold=" + threshold +
                ", params=" + params +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isBlank(metric)
                || ValidateUtils.isBlank(func)
                || ValidateUtils.isBlank(eopt)
                || ValidateUtils.isNull(threshold)
                || ValidateUtils.isBlank(params)) {
            return false;
        }
        return true;
    }
}