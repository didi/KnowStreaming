package com.xiaojukeji.kafka.manager.monitor.common.entry.vo;

import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MonitorRuleDTO;
import com.xiaojukeji.kafka.manager.common.entity.vo.normal.app.AppSummaryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/18
 */
@ApiModel(description = "告警规则详情")
public class MonitorRuleDetailVO {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "名称")
    private String operator;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "修改时间")
    private Long modifyTime;

    @ApiModelProperty(value = "App概览")
    private AppSummaryVO appSummary;

    @ApiModelProperty(value = "App概览")
    private MonitorRuleDTO monitorRule;

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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public AppSummaryVO getAppSummary() {
        return appSummary;
    }

    public void setAppSummary(AppSummaryVO appSummary) {
        this.appSummary = appSummary;
    }

    public MonitorRuleDTO getMonitorRule() {
        return monitorRule;
    }

    public void setMonitorRule(MonitorRuleDTO monitorRule) {
        this.monitorRule = monitorRule;
    }

    @Override
    public String toString() {
        return "MonitorRuleDetailVO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                ", modifyTime=" + modifyTime +
                ", appSummary=" + appSummary +
                ", monitorRule=" + monitorRule +
                '}';
    }
}