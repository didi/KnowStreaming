package com.xiaojukeji.kafka.manager.web.vo.alarm;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.AbstractMap;
import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/12
 */
@ApiModel(value = "AlarmConstantVO", description = "告警常数")
public class AlarmConstantVO {
    @ApiModelProperty(value = "条件类型列表")
    private List<AbstractMap.SimpleEntry<String, String>> conditionTypeList;

    @ApiModelProperty(value = "告警规则类型列表")
    private List<AbstractMap.SimpleEntry<Integer, String>> ruleTypeList;

    @ApiModelProperty(value = "通知规则类型列表")
    private List<AbstractMap.SimpleEntry<String, String>> notifyTypeList;

    @ApiModelProperty(value = "指标类型列表")
    private List<AbstractMap.SimpleEntry<String, String>> metricTypeList;

    public List<AbstractMap.SimpleEntry<String, String>> getConditionTypeList() {
        return conditionTypeList;
    }

    public void setConditionTypeList(List<AbstractMap.SimpleEntry<String, String>> conditionTypeList) {
        this.conditionTypeList = conditionTypeList;
    }

    public List<AbstractMap.SimpleEntry<Integer, String>> getRuleTypeList() {
        return ruleTypeList;
    }

    public void setRuleTypeList(List<AbstractMap.SimpleEntry<Integer, String>> ruleTypeList) {
        this.ruleTypeList = ruleTypeList;
    }

    public List<AbstractMap.SimpleEntry<String, String>> getNotifyTypeList() {
        return notifyTypeList;
    }

    public void setNotifyTypeList(List<AbstractMap.SimpleEntry<String, String>> notifyTypeList) {
        this.notifyTypeList = notifyTypeList;
    }

    public List<AbstractMap.SimpleEntry<String, String>> getMetricTypeList() {
        return metricTypeList;
    }

    public void setMetricTypeList(List<AbstractMap.SimpleEntry<String, String>> metricTypeList) {
        this.metricTypeList = metricTypeList;
    }

    @Override
    public String toString() {
        return "AlarmConstantVO{" +
                "conditionTypeList=" + conditionTypeList +
                ", ruleTypeList=" + ruleTypeList +
                ", notifyTypeList=" + notifyTypeList +
                ", metricTypeList=" + metricTypeList +
                '}';
    }
}