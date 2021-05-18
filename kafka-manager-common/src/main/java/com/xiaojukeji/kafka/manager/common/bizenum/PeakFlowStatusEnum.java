package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 峰值状态枚举
 * @author zengqiao
 * @date 20/5/11
 */
public enum PeakFlowStatusEnum {
    BETWEEN_ALL(0, "全部"),
    BETWEEN_00_60(1, "使用率0%-60%"),
    BETWEEN_60_80(2, "使用率60%-80%"),
    BETWEEN_80_100(3, "使用率80%-100%"),
    BETWEEN_100_PLUS(4, "使用率大于100%"),
    BETWEEN_EXCEPTION(5, "数据获取失败"),

    ;

    private Integer code;

    private String message;

    PeakFlowStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "PeakFlowStatusEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
