package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 * ES错误基类
 *
 * @author d06679
 * @date 2019/3/13
 */
public class ESRunTimeException extends RuntimeException {

    private ResultStatus resultType;

    public ESRunTimeException(String message, Throwable cause, ResultStatus resultType) {
        super(message, cause);
        this.resultType = resultType;
    }

    public ESRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ESRunTimeException(String message, ResultStatus resultType) {
        super(message);
        this.resultType = resultType;
    }

    public ESRunTimeException(String message) {
        super(message);
    }

    public ResultStatus getResultStatus() {
        return resultType;
    }

    public void setResultStatus(ResultStatus resultType) {
        this.resultType = resultType;
    }
}
