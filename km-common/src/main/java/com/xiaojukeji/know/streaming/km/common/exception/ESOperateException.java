package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 *
 *
 * @author d06679
 * @date 2019/2/21
 */
public class ESOperateException extends AdminOperateException {

    public ESOperateException(String message, Throwable cause) {
        super(message, cause, ResultStatus.ES_OPERATE_ERROR);
    }

    public ESOperateException(String message) {
        super(message, ResultStatus.ES_OPERATE_ERROR);
    }

}
