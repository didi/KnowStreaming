package com.xiaojukeji.know.streaming.km.persistence.es;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.persistence.es.dsls.DslLoaderUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 直接操作es集群的dao
 */
public class BaseESDAO {
    protected static final ILog      LOGGER = LogFactory.getLog("ES_LOGGER");

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
