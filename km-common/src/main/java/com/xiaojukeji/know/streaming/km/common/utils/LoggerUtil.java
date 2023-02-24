package com.xiaojukeji.know.streaming.km.common.utils;

import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;

public class LoggerUtil {
    private static final ILog MetricCollectedLogger = LogFactory.getLog("METRIC_COLLECTED_LOGGER");

    private static final ILog ESLogger = LogFactory.getLog("ES_LOGGER");

    public static ILog getMetricCollectedLogger() {
        return MetricCollectedLogger;
    }

    public static ILog getESLogger() {
        return ESLogger;
    }

    private LoggerUtil() {
    }
}
