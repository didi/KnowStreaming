package com.xiaojukeji.kafka.manager.web.model.alarm;

import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyActionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyExpressionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyFilterDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "AlarmModel", description = "告警模型")
public class AlarmRuleModel {
    @ApiModelProperty(value = "告警Id, 修改时必传")
    private Long id;

    @ApiModelProperty(value = "告警名称")
    private String alarmName;

    @ApiModelProperty(value = "策略筛选")
    private List<AlarmStrategyFilterDTO> strategyFilterList;

    @ApiModelProperty(value = "策略表达式")
    private List<AlarmStrategyExpressionDTO> strategyExpressionList;

    @ApiModelProperty(value = "策略响应")
    private List<AlarmStrategyActionDTO> strategyActionList;

    @ApiModelProperty(value = "负责人")
    private List<String> principalList;

    @ApiModelProperty(value = "告警状态, 0:暂停, 1:启用")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public List<AlarmStrategyFilterDTO> getStrategyFilterList() {
        return strategyFilterList;
    }

    public void setStrategyFilterList(List<AlarmStrategyFilterDTO> strategyFilterList) {
        this.strategyFilterList = strategyFilterList;
    }

    public List<AlarmStrategyExpressionDTO> getStrategyExpressionList() {
        return strategyExpressionList;
    }

    public void setStrategyExpressionList(List<AlarmStrategyExpressionDTO> strategyExpressionList) {
        this.strategyExpressionList = strategyExpressionList;
    }

    public List<AlarmStrategyActionDTO> getStrategyActionList() {
        return strategyActionList;
    }

    public void setStrategyActionList(List<AlarmStrategyActionDTO> strategyActionList) {
        this.strategyActionList = strategyActionList;
    }

    public List<String> getPrincipalList() {
        return principalList;
    }

    public void setPrincipalList(List<String> principalList) {
        this.principalList = principalList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean legal() {
        if (StringUtils.isEmpty(alarmName)
                || principalList == null
                || strategyExpressionList == null || strategyExpressionList.isEmpty()
                || strategyFilterList == null || strategyFilterList.isEmpty()
                || strategyActionList == null || strategyActionList.isEmpty()) {
            return false;
        }
        for (AlarmStrategyFilterDTO model: strategyFilterList) {
            if (!model.legal()) {
                return false;
            }
        }
        for (AlarmStrategyExpressionDTO model: strategyExpressionList) {
            if (!model.legal()) {
                return false;
            }
        }
        for (AlarmStrategyActionDTO model: strategyActionList) {
            if (!model.legal()) {
                return false;
            }
        }
        return true;
    }
}