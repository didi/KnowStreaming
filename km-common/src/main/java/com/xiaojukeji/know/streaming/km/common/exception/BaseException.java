package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 * 平台错误基类
 *
 * @author d06679
 * @date 2019/3/13
 */
public class BaseException extends Exception {

    private final ResultStatus resultType;

    public BaseException(String message, Throwable cause, ResultStatus resultType) {
        super(message, cause);
        this.resultType = resultType;
    }

    public BaseException(String message, ResultStatus resultType) {
        super(message);
        this.resultType = resultType;
    }

    public ResultStatus getResultStatus() {
        return resultType;
    }
}
