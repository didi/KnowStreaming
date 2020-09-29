package com.xiaojukeji.kafka.manager.monitor.common.entry.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/4
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "监控告警")
public class MonitorRuleDTO {
    @ApiModelProperty(value = "所属AppID")
    private String appId;

    @ApiModelProperty(value = "ID, 修改时使用")
    private Long id;

    @ApiModelProperty(value = "报警名称")
    private String name;

    @ApiModelProperty(value = "报警等级，1级：电话+短信+钉钉+邮件，2：短信+钉钉+邮件 3：钉钉+邮件")
    private Integer priority;

    @ApiModelProperty(value = "报警生效时间，小时，24小时计，以逗号分隔")
    private String periodHoursOfDay;

    @ApiModelProperty(value = "报警生效时间，星期，7天计")
    private String periodDaysOfWeek;

    @ApiModelProperty(value = "报警规则")
    private List<MonitorStrategyExpressionDTO> strategyExpressionList;

    @ApiModelProperty(value = "过滤规则")
    private List<MonitorStrategyFilterDTO> strategyFilterList;

    @ApiModelProperty(value = "通知方式")
    private List<MonitorStrategyActionDTO> strategyActionList;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getPeriodHoursOfDay() {
        return periodHoursOfDay;
    }

    public void setPeriodHoursOfDay(String periodHoursOfDay) {
        this.periodHoursOfDay = periodHoursOfDay;
    }

    public String getPeriodDaysOfWeek() {
        return periodDaysOfWeek;
    }

    public void setPeriodDaysOfWeek(String periodDaysOfWeek) {
        this.periodDaysOfWeek = periodDaysOfWeek;
    }

    public List<MonitorStrategyExpressionDTO> getStrategyExpressionList() {
        return strategyExpressionList;
    }

    public void setStrategyExpressionList(List<MonitorStrategyExpressionDTO> strategyExpressionList) {
        this.strategyExpressionList = strategyExpressionList;
    }

    public List<MonitorStrategyFilterDTO> getStrategyFilterList() {
        return strategyFilterList;
    }

    public void setStrategyFilterList(List<MonitorStrategyFilterDTO> strategyFilterList) {
        this.strategyFilterList = strategyFilterList;
    }

    public List<MonitorStrategyActionDTO> getStrategyActionList() {
        return strategyActionList;
    }

    public void setStrategyActionList(List<MonitorStrategyActionDTO> strategyActionList) {
        this.strategyActionList = strategyActionList;
    }

    @Override
    public String toString() {
        return "MonitorRuleDTO{" +
                "appId='" + appId + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                ", periodHoursOfDay='" + periodHoursOfDay + '\'' +
                ", periodDaysOfWeek='" + periodDaysOfWeek + '\'' +
                ", strategyExpressionList=" + strategyExpressionList +
                ", strategyFilterList=" + strategyFilterList +
                ", strategyActionList=" + strategyActionList +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isBlank(appId)
                || ValidateUtils.isBlank(name)
                || ValidateUtils.isNull(priority)
                || ValidateUtils.isBlank(periodHoursOfDay)
                || ValidateUtils.isBlank(periodDaysOfWeek)
                || ValidateUtils.isEmptyList(strategyExpressionList)
                || ValidateUtils.isNull(strategyFilterList)
                || ValidateUtils.isEmptyList(strategyActionList)) {
            return false;
        }

        for (MonitorStrategyExpressionDTO dto: strategyExpressionList) {
            if (!dto.paramLegal()) {
                return false;
            }
        }

        for (MonitorStrategyFilterDTO dto: strategyFilterList) {
            if (!dto.paramLegal()) {
                return false;
            }
        }
        for (MonitorStrategyActionDTO dto: strategyActionList) {
            if (!dto.paramLegal()) {
                return false;
            }
        }
        return true;
    }
}