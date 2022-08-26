package com.xiaojukeji.know.streaming.km.common.exception;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;

/**
 * @author linyunan
 * @date 2021-04-25
 */
public class NotFindSubclassException extends AdminRunTimeException {

	public NotFindSubclassException(String message) {
		super(message, ResultStatus.NO_FIND_SUB_CLASS);
	}
}
