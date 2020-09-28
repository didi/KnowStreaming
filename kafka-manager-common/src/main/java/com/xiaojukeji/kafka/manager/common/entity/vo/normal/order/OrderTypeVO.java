package com.xiaojukeji.kafka.manager.common.entity.vo.normal.order;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhongyuankai
 * @date 20/4/24
 */
public class OrderTypeVO {
    @ApiModelProperty(value = "工单类型")
    private Integer type;

    @ApiModelProperty(value = "描述信息")
    private String message;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OrderTypeVO(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    public OrderTypeVO() {
    }

    @Override
    public String toString() {
        return "OrderTypeVO{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}
