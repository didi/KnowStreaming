package com.xiaojukeji.know.streaming.km.common.enums.operaterecord;

import com.xiaojukeji.know.streaming.km.common.constant.Constant;

/**
 * 操作枚举
 * @author d06679
 * @date 2017/7/14
 */
public enum OperationEnum {
    UNKNOWN(Constant.INVALID_CODE, "unknown"),

    ADD(1, "新增"),

    DELETE(2, "删除"),

    EDIT(3, "修改"),

    ENABLE(4, "启用"),

    DISABLE(5, "禁用"),

    EXE(6, "执行"),

    SWITCH(7, "切换"),

    REPLACE(8, "替换(无则新建, 有则修改)"),

    SEARCH(9, "读取"),

    CANCEL(10, "取消"),

    RESTART(11, "重启"),

    TRUNCATE(12, "清空"),

    ;

    OperationEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;

    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OperationEnum valueOf(Integer code) {
        if (code == null) {
            return OperationEnum.UNKNOWN;
        }
        for (OperationEnum state : OperationEnum.values()) {
            if (state.getCode() == code) {
                return state;
            }
        }

        return OperationEnum.UNKNOWN;
    }

    public static boolean validate(Integer code) {
        if (code == null) {
            return false;
        }
        for (OperationEnum state : OperationEnum.values()) {
            if (state.getCode() == code) {
                return true;
            }
        }

        return false;
    }
}
