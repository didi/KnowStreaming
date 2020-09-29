package com.xiaojukeji.kafka.manager.bpm.common.handle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/20
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "工单审批－基本参数")
public class OrderHandleBaseDTO {
    @ApiModelProperty(value = "工单Id")
    private Long id;

    @ApiModelProperty(value = "审批状态, 1:通过, 2:拒绝")
    private Integer status;

    @ApiModelProperty(value = "审批意见")
    private String opinion;

    @ApiModelProperty(value = "工单具体的信息(json串)")
    private String detail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "OrderHandleBaseDTO{" +
                "id=" + id +
                ", status=" + status +
                ", opinion='" + opinion + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }

    public boolean paramLegal() {
        if (ValidateUtils.isNull(id) || ValidateUtils.isNull(status)) {
            return false;
        }
        return true;
    }
}