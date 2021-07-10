package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * @author zhongyuankai
 * @date 20/09/03
 */
public enum  OperateEnum {
    ADD(0, "新增"),

    DELETE(1, "删除"),

    EDIT(2, "修改"),

    UNKNOWN(-1, "unknown"),
        ;

    OperateEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int    code;

    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static OperateEnum valueOf(Integer code) {
        if (code == null) {
            return OperateEnum.UNKNOWN;
        }
        for (OperateEnum state : OperateEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return OperateEnum.UNKNOWN;
    }

    public static boolean validate(Integer code) {
        if (code == null) {
            return true;
        }
        for (OperateEnum state : OperateEnum.values()) {
            if (state.getCode() == code) {
                return true;
            }
        }

        return false;
    }
}
