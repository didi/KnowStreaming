package com.xiaojukeji.know.streaming.km.rebalance.exception;

public class OptimizationFailureException extends Exception {
    public OptimizationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptimizationFailureException(String message) {
        super(message);
    }

    public OptimizationFailureException(Throwable cause) {
        super(cause);
    }
}
