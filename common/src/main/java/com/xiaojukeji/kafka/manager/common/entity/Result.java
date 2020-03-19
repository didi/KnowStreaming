package com.xiaojukeji.kafka.manager.common.entity;

import com.alibaba.fastjson.JSON;
import com.xiaojukeji.kafka.manager.common.constant.StatusCode;

import java.io.Serializable;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-07-08
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -2772975319944108658L;

    private T data;
    private String message;
    private Integer code;

    public Result(T data) {
        this.data = data;
        this.code = StatusCode.SUCCESS;
        this.message = "成功";
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

    public Integer getCode()
    {
        return this.code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }
}
