package com.xiaojukeji.kafka.manager.common.bizenum;

/**
 * 操作状态类型
 * @author zengqiao
 * @date 19/11/21
 */
public enum OperationStatusEnum {
    CREATE(0, "创建"),
    UPDATE(1, "更新"),
    DELETE(2, "删除"),
    ;

    private Integer code;

    private String message;

    OperationStatusEnum(Integer code, String message) {
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
        return "OperationStatusEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}