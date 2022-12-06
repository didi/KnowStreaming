package com.xiaojukeji.know.streaming.km.common.bean.entity.result;

import com.xiaojukeji.know.streaming.km.common.enums.valid.ValidateKafkaAddressErrorEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangyiminghappy@163.com
 * @date 2019-07-08
 */
@Data
@ApiModel(description = "调用结果")
public class Result<T> extends BaseResult {
    @ApiModelProperty(value = "数据")
    protected T data;

    private Result() {
        this.code = ResultStatus.SUCCESS.getCode();
        this.message = ResultStatus.SUCCESS.getMessage();
    }

    public static <T> Result<T> build(boolean succ) {
        if (succ) {
            return buildSuc();
        }
        return buildFail();
    }

    public static <T> Result<T> buildFail() {
        Result<T> result = new Result<>();
        result.setCode(ResultStatus.FAIL.getCode());
        result.setMessage(ResultStatus.FAIL.getMessage());
        return result;
    }

    public static <T> Result<T> build(boolean succ, T data) {
        Result<T> result = new Result<>();
        if (succ) {
            result.setCode(ResultStatus.SUCCESS.getCode());
            result.setMessage(ResultStatus.SUCCESS.getMessage());
            result.setData(data);
        } else {
            result.setCode(ResultStatus.FAIL.getCode());
            result.setMessage(ResultStatus.FAIL.getMessage());
        }
        return result;
    }

    public static <T> Result<T> buildSuc() {
        Result<T> result = new Result<>();
        result.setCode(ResultStatus.SUCCESS.getCode());
        result.setMessage(ResultStatus.SUCCESS.getMessage());
        return result;
    }

    public static <T> Result<T> buildSuc(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultStatus.SUCCESS.getCode());
        result.setMessage(ResultStatus.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> buildFailure(String message) {
        Result<T> result = new Result<>();
        result.setCode(ResultStatus.FAIL.getCode());
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static <T> Result<T> buildFailure(int code,  String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(msg);
        return result;
    }

    public static <T> Result<T> buildFailure(String message,  T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultStatus.FAIL.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> buildFailure(ResultStatus rs) {
        Result<T> result = new Result<>();
        result.setCode(rs.getCode());
        result.setMessage(rs.getMessage());
        result.setData(null);
        return result;
    }

    public static <T> Result<T> buildFrom(ResultStatus rs) {
        Result<T> result = new Result<>();
        result.setCode(rs.getCode());
        result.setMessage(rs.getMessage());
        return result;
    }

    public static <T> Result<T> buildFrom(Result ret) {
        Result<T> result = new Result<>();
        result.setCode(ret.getCode());
        result.setMessage(ret.getMessage());
        return result;
    }

    public static <T> Result<T> buildFrom(ValidateKafkaAddressErrorEnum errorEnum, String msg) {
        Result<T> result = new Result<>();
        result.setCode(errorEnum.getCode());
        result.setMessage(msg);
        return result;
    }

    public static <T> Result<T> buildFromRSAndMsg(ResultStatus resultStatus, String message) {
        Result<T> result = new Result<>();
        result.setCode(resultStatus.getCode());
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static <T> Result<T> buildFromRSAndData(ResultStatus rs, T data) {
        Result<T> result = new Result<>();
        result.setCode(rs.getCode());
        result.setMessage(rs.getMessage());
        result.setData(data);
        return result;
    }

    public static <T, U> Result<T> buildFromIgnoreData(Result<U> anotherResult) {
        Result<T> result = new Result<>();
        result.setCode(anotherResult.getCode());
        result.setMessage(anotherResult.getMessage());
        return result;
    }

    public static <T, U> Result<T> buildFromIgnoreData(PaginationResult<U> anotherResult) {
        Result<T> result = new Result<>();
        result.setCode(anotherResult.getCode());
        result.setMessage(anotherResult.getMessage());
        return result;
    }

    public static <T> Result<T> buildDuplicate(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultStatus.DUPLICATION.getCode());
        result.setMessage(msg);
        return result;
    }

    public static <T> Result<T> buildNotExist(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultStatus.NOT_EXIST.getCode());
        result.setMessage(msg);
        return result;
    }

    public static <T> Result<T> buildParamIllegal(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultStatus.PARAM_ILLEGAL.getCode());
        result.setMessage(ResultStatus.PARAM_ILLEGAL.getMessage() + ":" + msg + "，请检查后再提交！");
        return result;
    }

    public boolean hasData(){
        return !failed() && this.data != null;
    }

    @Override
    public String toString() {
        return "Result{" +
                "message='" + message + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
