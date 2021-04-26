package com.xiaojukeji.kafka.manager.common.entity.dto.rd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.bizenum.ModuleEnum;
import com.xiaojukeji.kafka.manager.common.bizenum.OperateEnum;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author zhongyuankai_i
 * @date 20/09/03
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperateRecordDTO {
    @ApiModelProperty("模块ID")
    private Integer moduleId;

    @ApiModelProperty("操作ID")
    private Integer operateId;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("开始时间")
    private Long startTime;

    @ApiModelProperty("结束时间")
    private Long endTime;

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getOperateId() {
        return operateId;
    }

    public void setOperateId(Integer operateId) {
        this.operateId = operateId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "OperateRecordDTO{" +
                "moduleId=" + moduleId +
                ", operateId=" + operateId +
                ", operator='" + operator + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public boolean legal() {
        return !ValidateUtils.isNull(moduleId) && ModuleEnum.validate(moduleId) && OperateEnum.validate(operateId);
    }
}
