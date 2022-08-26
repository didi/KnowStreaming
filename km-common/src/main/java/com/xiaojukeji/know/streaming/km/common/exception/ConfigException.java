package com.xiaojukeji.know.streaming.km.common.exception;

/**
 * @author limeng
 * @date 2017/12/22
 */
public class ConfigException extends Exception {

    private static final long serialVersionUID = -3670649722021947735L;

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException() {
    }
}
