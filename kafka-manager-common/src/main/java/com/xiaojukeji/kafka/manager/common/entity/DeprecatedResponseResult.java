package com.xiaojukeji.kafka.manager.common.entity;

/**
 * @author zengqiao
 * @date 20/7/27
 */
public class DeprecatedResponseResult<T> {
    public static final String SUCCESS_STATUS  = "success";

    public static final String FAILED_STATUS   = "failure";

    public static final String SUCCESS_MESSAGE = "process succeeded!";

    public static final String FAILED_MESSAGE  = "process failed!";

    private String             status;

    private String             message;

    private T                  data;

    public static <T> DeprecatedResponseResult<T> success(T data) {
        DeprecatedResponseResult<T> responseCommonResult = new DeprecatedResponseResult<T>();
        responseCommonResult.setMessage(SUCCESS_MESSAGE);
        responseCommonResult.setStatus(SUCCESS_STATUS);
        responseCommonResult.setData(data);
        return responseCommonResult;
    }

    public static <T> DeprecatedResponseResult<T> success() {
        DeprecatedResponseResult<T> responseCommonResult = new DeprecatedResponseResult<T>();
        responseCommonResult.setStatus(SUCCESS_STATUS);
        responseCommonResult.setMessage(SUCCESS_MESSAGE);
        return responseCommonResult;
    }

    public static <T> DeprecatedResponseResult<T> failure() {
        DeprecatedResponseResult<T> responseCommonResult = new DeprecatedResponseResult<T>();
        responseCommonResult.setMessage(FAILED_MESSAGE);
        responseCommonResult.setStatus(FAILED_STATUS);
        return responseCommonResult;
    }

    public static <T> DeprecatedResponseResult<T> failure(String message) {
        DeprecatedResponseResult<T> responseCommonResult = new DeprecatedResponseResult<T>();
        responseCommonResult.setMessage(message);
        responseCommonResult.setStatus(FAILED_STATUS);
        return responseCommonResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DeprecatedResponseResult{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}