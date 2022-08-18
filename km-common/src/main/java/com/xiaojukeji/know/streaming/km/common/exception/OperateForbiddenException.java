package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 *
 *
 * @author d06679
 * @date 2019/2/21
 */
public class OperateForbiddenException extends AdminRunTimeException {

    public OperateForbiddenException(String message) {
        super(message, ResultStatus.OPERATION_FORBIDDEN);
    }

}
