package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * offset获取的位置
 * @author zengqiao
 * @date 19/5/29
 */
public enum OffsetPosEnum {
    NONE(0),

    BEGINNING(1),

    END(2),

    BOTH(3);

    public final Integer code;

    OffsetPosEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public static OffsetPosEnum getOffsetPosEnum(Integer code) {
        for (OffsetPosEnum offsetPosEnum : values()) {
            if (offsetPosEnum.getCode().equals(code)) {
                return offsetPosEnum;
            }
        }
        return NONE;
    }

    @Override
    public String toString() {
        return "OffsetPosEnum{" +
                "code=" + code +
                '}';
    }
}
