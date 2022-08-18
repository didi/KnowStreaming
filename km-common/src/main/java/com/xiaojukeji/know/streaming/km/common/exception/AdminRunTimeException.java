package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 * 平台错误基类
 *
 * @author d06679
 * @date 2019/3/13
 */
public class AdminRunTimeException extends RuntimeException {

    private final ResultStatus resultType;

    public AdminRunTimeException(String message, Throwable cause, ResultStatus resultType) {
        super(message, cause);
        this.resultType = resultType;
    }

    public AdminRunTimeException(String message, ResultStatus resultType) {
        super(message);
        this.resultType = resultType;
    }

    public ResultStatus getResultStatus() {
        return resultType;
    }
}
