package com.xiaojukeji.know.streaming.km.persistence.es;

import com.didiglobal.logi.log.ILog;
import com.xiaojukeji.know.streaming.km.common.utils.LoggerUtil;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslLoaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 直接操作es集群的dao
 */
public abstract class BaseESDAO {
    protected static final ILog LOGGER = LoggerUtil.getESLogger();

    /**
     * 加载查询语句工具类
     */
    @Autowired
    protected DslLoaderUtil dslLoaderUtil;

    /**
     * Arius操作es集群的client
     */
    @Autowired
    protected ESOpClient      esOpClient;
}
