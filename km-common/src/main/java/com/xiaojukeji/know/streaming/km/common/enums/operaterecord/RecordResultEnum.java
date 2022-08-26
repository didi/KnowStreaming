package com.xiaojukeji.know.streaming.km.common.enums.operaterecord;

/**
 * 需要记录的操作结果
 * @author d06679
 * @date 2017/7/14
 */
public enum RecordResultEnum {
    SUCCESS(1, "仅记录成功"),

    FAILED(2, "仅记录失败"),

    ALL(Integer.MAX_VALUE, "记录所有"),

    ;

    RecordResultEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int    code;

    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public boolean needRecord(boolean success) {
        return (success && (this.equals(ALL) || this.equals(SUCCESS))) || (!success && this.equals(FAILED));
    }
}
