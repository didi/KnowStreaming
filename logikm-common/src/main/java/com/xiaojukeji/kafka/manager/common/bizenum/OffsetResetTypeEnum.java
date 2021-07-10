package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zengqiao
 * @date 20/10/26
 */
public enum OffsetResetTypeEnum {
    RESET_BY_TIME(0),

    RESET_BY_OFFSET(1);

    private final Integer code;

    OffsetResetTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "OffsetResetTypeEnum{" +
                "code=" + code +
                '}';
    }
}
