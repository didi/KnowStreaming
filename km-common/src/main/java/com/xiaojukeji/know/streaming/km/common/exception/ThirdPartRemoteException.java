package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 * @author d06679
 * @date 2019/3/18
 */
public class ThirdPartRemoteException extends AdminRunTimeException {

    public ThirdPartRemoteException(String message, Throwable cause, ResultStatus resultType) {
        super(message, cause, resultType);
    }

    public ThirdPartRemoteException(String message, ResultStatus resultType) {
        super(message, resultType);
    }

}
