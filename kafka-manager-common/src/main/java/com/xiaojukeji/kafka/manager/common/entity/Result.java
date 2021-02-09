package com.xiaojukeji.kafka.manager.common.entity;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.constant.Constant;

import java.io.Serializable;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-07-08
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -2772975319944108658L;

    private T data;
    private String message;
    private String tips;
    private int code;

    public Result(T data) {
        this.data = data;
        this.code = ResultStatus.SUCCESS.getCode();
        this.message = ResultStatus.SUCCESS.getMessage();
    }

    public Result() {
        this(null);
    }

    public Result(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

    public Result(Integer code, T data, String message) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public T getData()
    {
        return (T)this.data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public int getCode()
    {
        return this.code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }

    public static Result buildSuc() {
        Result result = new Result();
        result.setCode(ResultStatus.SUCCESS.getCode());
        result.setMessage(ResultStatus.SUCCESS.getMessage());
        return result;
    }

    public static <T> Result<T> buildSuc(T data) {
        Result<T> result = new Result<T>();
        result.setCode(ResultStatus.SUCCESS.getCode());
        result.setMessage(ResultStatus.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> buildGatewayFailure(String message) {
        Result<T> result = new Result<T>();
        result.setCode(ResultStatus.GATEWAY_INVALID_REQUEST.getCode());
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static <T> Result<T> buildFailure(String message) {
        Result<T> result = new Result<T>();
        result.setCode(ResultStatus.FAIL.getCode());
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static Result buildFrom(ResultStatus resultStatus) {
        Result result = new Result();
        result.setCode(resultStatus.getCode());
        result.setMessage(resultStatus.getMessage());
        return result;
    }

    public static Result buildFrom(ResultStatus resultStatus, Object data) {
        Result result = new Result();
        result.setCode(resultStatus.getCode());
        result.setMessage(resultStatus.getMessage());
        result.setData(data);
        return result;
    }

    public boolean failed() {
        return !Constant.SUCCESS.equals(code);
    }

}
