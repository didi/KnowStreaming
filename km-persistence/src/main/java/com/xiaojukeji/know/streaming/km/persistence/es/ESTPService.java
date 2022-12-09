package com.xiaojukeji.know.streaming.km.persistence.es;

import com.xiaojukeji.know.streaming.km.common.utils.FutureWaitUtil;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 处理ES请求的线程池
 */
@Service
@NoArgsConstructor
public class ESTPService {
    @Value("${thread-pool.es.search.thread-num:10}")
    private Integer     esSearchThreadCnt;

    @Value("${thread-pool.es.search.queue-size:5000}")
    private Integer     esSearchThreadQueueSize;

    private FutureWaitUtil<Object> searchESTP;

    @PostConstruct
    private void init() {
        searchESTP = FutureWaitUtil.init(
                "SearchESTP",
                esSearchThreadCnt,
                esSearchThreadCnt,
                esSearchThreadQueueSize
        );
    }

    public void submitSearchTask(String taskName, Integer timeoutUnisMs, Runnable runnable) {
        searchESTP.runnableTask(taskName, timeoutUnisMs, runnable);
    }

    public void waitExecute() {
        searchESTP.waitExecute();
    }
}
