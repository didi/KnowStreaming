package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 * 不存在异常
 * @author d06679
 */
public class ParamErrorException extends BaseException {
    public ParamErrorException(String message) {
        super(message, ResultStatus.PARAM_ILLEGAL);
    }
}
