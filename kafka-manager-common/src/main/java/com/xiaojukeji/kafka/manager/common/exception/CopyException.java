package com.xiaojukeji.kafka.manager.common.exception;

/**
 * @author huangyiminghappy@163.com
 * @date 2019/3/15
 */
public class CopyException extends RuntimeException {
    private final static long serialVersionUID = 1L;

    public CopyException(String message) {
        super(message);
    }

    public CopyException(String message, Throwable cause) {
        super(message, cause);
    }

    public CopyException(Throwable cause) {
        super(cause);
    }

    public CopyException() {
        super();
    }
}
