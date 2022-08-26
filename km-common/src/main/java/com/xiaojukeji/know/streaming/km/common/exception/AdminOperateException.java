package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 *
 *
 * @author d06679
 * @date 2019/2/21
 */
public class AdminOperateException extends BaseException {

    public AdminOperateException(String message) {
        super(message, ResultStatus.OPERATION_FAILED);
    }

    public AdminOperateException(String message, Throwable cause) {
        super(message, cause, ResultStatus.OPERATION_FAILED);
    }

    public AdminOperateException(String message, ResultStatus resultStatus) {
        super(message, resultStatus);
    }

    public AdminOperateException(String message, Throwable cause, ResultStatus resultStatus) {
        super(message, cause, resultStatus);
    }

}
