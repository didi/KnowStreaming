package com.xiaojukeji.know.streaming.km.common.utils;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.exception.BaseException;

public class RetryExecutor {
    private static final ILog LOGGER     = LogFactory.getLog(RetryExecutor.class);

    /**
     * 最多的重试次数
     */
    private static final int        RETRY_MAX  = 10;
    /**
     * 操作名字
     */
    private String name       = "";
    /**
     * es操作内容
     */
    private Handler           handler;

    /**
     * 重试次数
     */
    private Integer retryCount = 0;

    /**
     * es操作
     * Created by d06679 on 2017/8/24.
     */

    public interface Handler {
        /**
         * 处理方法
         * @return
         * @throws Throwable
         */
        boolean process() throws BaseException;

        /***
         * 是否重试
         * @param e 异常
         * @return
         */
        default boolean needRetry(Exception e) {
            return true;
        }

        /**
         * 重试Sleep时间间隔
         * @param retryTimes 重试次数
         * @return
         */
        default int retrySleepTime(int retryTimes) {return 0;}
    }

    public static RetryExecutor builder() {
        return new RetryExecutor();
    }

    public RetryExecutor name(String name) {
        this.name = name;
        return this;
    }

    public RetryExecutor handler(Handler handler) {
        this.handler = handler;
        return this;
    }

    public RetryExecutor retryCount(Integer retryCount) {
        this.retryCount = (retryCount > RETRY_MAX) ? RETRY_MAX : retryCount;
        return this;
    }

    /**
     * 重试操作，要么handler执行成功有返回值,要么报异常
     * @throws Exception 操作的异常
     */
    public boolean execute() throws Exception {
        boolean succ = false;
        int tryCount = 0;
        do {
            try {
                int retrySleepTime = handler.retrySleepTime(tryCount);
                if(retrySleepTime > 0){
                    Thread.sleep(retrySleepTime);
                }

                succ = handler.process();
                if (succ) {
                    return succ;
                }
            } catch (Exception e) {
                if (!handler.needRetry(e) || tryCount == retryCount) {
                    LOGGER.warn("method=execute||errMsg={}||handlerName={}||tryCount={}",
                        e.getMessage(), name, tryCount, e);
                    throw e;
                }

                LOGGER.warn("method=execute||errMsg={}||handlerName={}||tryCount={}||maxTryCount={}",
                        e.getMessage(), name, tryCount,retryCount);
            }
        } while (tryCount++ < retryCount);

        return succ;
    }
}
