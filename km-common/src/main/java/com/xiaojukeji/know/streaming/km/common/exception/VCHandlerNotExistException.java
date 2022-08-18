package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 *
 *
 * @author d06679
 * @date 2019/2/21
 */
public class VCHandlerNotExistException extends AdminOperateException {

    public VCHandlerNotExistException(String message, Throwable cause) {
        super(message, cause, ResultStatus.NO_FIND_METHOD);
    }

    public VCHandlerNotExistException(String message) {
        super(message, ResultStatus.NO_FIND_METHOD);
    }

}
