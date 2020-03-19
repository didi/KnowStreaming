package com.xiaojukeji.kafka.manager.common.entity.bizenum;

/**
 * 工单类型
 * @author zengqiao
 * @date 19/6/23
 */
public enum OrderTypeEnum {
    UNKNOWN(-1),

    APPLY_TOPIC(0),

    APPLY_PARTITION(1);

    private Integer code;

    OrderTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static OrderTypeEnum getOrderTypeEnum(Integer code) {
        for (OrderTypeEnum elem: OrderTypeEnum.values()) {
            if (elem.getCode().equals(code)) {
                return elem;
            }
        }
        return OrderTypeEnum.UNKNOWN;
    }
}
