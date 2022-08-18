package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 * 已存在异常
 * @author d06679
 */
public class DuplicateException extends BaseException {

    public DuplicateException(String message) {
        super(message, ResultStatus.DUPLICATION);
    }

}
