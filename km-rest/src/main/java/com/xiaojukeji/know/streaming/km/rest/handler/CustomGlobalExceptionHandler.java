package com.xiaojukeji.know.streaming.km.rest.handler;

import com.xiaojukeji.know.streaming.km.common.bean.entity.result.Result;
import com.xiaojukeji.know.streaming.km.common.bean.entity.result.ResultStatus;
import com.xiaojukeji.know.streaming.km.common.utils.ConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomGlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomGlobalExceptionHandler.class);

    /**
     * 处理参数异常并返回
     * @param me 异常
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> methodArgumentNotValidException(MethodArgumentNotValidException me) {
        List<FieldError> fieldErrorList = me.getBindingResult().getFieldErrors();

        List<String> errorList = fieldErrorList.stream().map(elem -> elem.getDefaultMessage()).collect(Collectors.toList());

        return Result.buildFromRSAndMsg(ResultStatus.PARAM_ILLEGAL, ConvertUtil.list2String(errorList, ","));
    }

    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNullPointerException(Exception e) {
        LOGGER.error("method=handleNullPointerException||errMsg=exception", e);

        return Result.buildFromRSAndMsg(ResultStatus.FAIL, "服务空指针异常");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        LOGGER.error("method=handleException||errMsg=exception", e);

        return Result.buildFromRSAndMsg(ResultStatus.FAIL, e.getMessage());
    }
}
