package com.xiaojukeji.kafka.manager.web.vo.alarm;

import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyActionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyExpressionDTO;
import com.xiaojukeji.kafka.manager.common.entity.dto.alarm.AlarmStrategyFilterDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zengqiao
 * @date 19/5/12
 */
@ApiModel(value = "AlarmRuleVO", description = "告警规则")
public class AlarmRuleVO {
    @ApiModelProperty(value = "告警Id, 修改时必传")
    private Long id;

    @ApiModelProperty(value = "报警名称")
    private String alarmName;

    @ApiModelProperty(value = "策略表达式")
    private List<AlarmStrategyExpressionDTO> strategyExpressionList;

    @ApiModelProperty(value = "策略筛选")
    private List<AlarmStrategyFilterDTO> strategyFilterList;

    @ApiModelProperty(value = "策略响应")
    private List<AlarmStrategyActionDTO> strategyActionList;

    @ApiModelProperty(value = "负责人列表")
    private List<String> principalList;

    @ApiModelProperty(value = "是否启用[1:启用, 0:不启用, -1:已删除]")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Long gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private Long gmtModify;

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

    public List<AlarmStrategyExpressionDTO> getStrategyExpressionList() {
        return strategyExpressionList;
    }

    public void setStrategyExpressionList(List<AlarmStrategyExpressionDTO> strategyExpressionList) {
        this.strategyExpressionList = strategyExpressionList;
    }

    public List<AlarmStrategyFilterDTO> getStrategyFilterList() {
        return strategyFilterList;
    }

    public void setStrategyFilterList(List<AlarmStrategyFilterDTO> strategyFilterList) {
        this.strategyFilterList = strategyFilterList;
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

    public Long getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Long gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "AlarmRuleVO{" +
                "id=" + id +
                ", alarmName='" + alarmName + '\'' +
                ", strategyExpressionList=" + strategyExpressionList +
                ", strategyFilterList=" + strategyFilterList +
                ", strategyActionList=" + strategyActionList +
                ", principalList=" + principalList +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                '}';
    }
}