package com.xiaojukeji.kafka.manager.kcm.component.storage.common;

/**
 * 文件类型
 * @author zengqiao
 * @date 20/4/29
 */
public enum StorageEnum {
    GIFT(0, "gift"),
    GIT(1, "git"),
    S3(2, "S3"),
    ;

    private Integer code;

    private String message;

    StorageEnum(Integer code, String message) {
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
        return "StorageEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}