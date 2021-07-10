package com.xiaojukeji.kafka.manager.task;

import com.xiaojukeji.kafka.manager.common.utils.factory.DefaultThreadFactory;
import com.xiaojukeji.kafka.manager.dao.gateway.impl.AppDaoImpl;
import com.xiaojukeji.kafka.manager.dao.gateway.impl.AuthorityDaoImpl;
import com.xiaojukeji.kafka.manager.dao.impl.TopicDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 后台任务线程
 * @author zengqiao
 * @date 21/02/02
 */
@Service
public class DaoBackgroundTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaoBackgroundTask.class);

    private static final ScheduledExecutorService SYNC_CACHE_THREAD_POOL = Executors.newSingleThreadScheduledExecutor(new DefaultThreadFactory("syncCacheTask"));

    @PostConstruct
    public void init() {
        SYNC_CACHE_THREAD_POOL.scheduleAtFixedRate(() -> {
            LOGGER.info("class=DaoBackgroundTask||method=init||msg=sync cache start");

            TopicDaoImpl.resetCache();

            AppDaoImpl.resetCache();

            AuthorityDaoImpl.resetCache();

            LOGGER.info("class=DaoBackgroundTask||method=init||msg=sync cache finished");
        }, 1, 10, TimeUnit.MINUTES);
    }
}
