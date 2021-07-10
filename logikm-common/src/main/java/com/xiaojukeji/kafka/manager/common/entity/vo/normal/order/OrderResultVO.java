package com.xiaojukeji.kafka.manager.common.entity.vo.normal.order;

import com.xiaojukeji.kafka.manager.common.entity.Result;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "工单审批结果")
public class OrderResultVO {
    @ApiModelProperty(value = "工单ID")
    private Long id;

    @ApiModelProperty(value = "审批结果")
    private Result result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "OrderResultVO{" +
                "id=" + id +
                ", result=" + result +
                '}';
    }
}
