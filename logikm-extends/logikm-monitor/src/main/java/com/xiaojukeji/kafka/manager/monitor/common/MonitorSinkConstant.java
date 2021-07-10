package com.xiaojukeji.kafka.manager.monitor.common;

/**
 * @author huangjw
 * @date 17/5/24.
 */
public class MonitorSinkConstant {
    /**
     * 指标名前缀
     */
    public static final String METRIC_NAME_PRE_STR = "online-";

    /**
     * 单次上报大小
     */
    public static final Integer MONITOR_SYSTEM_SINK_THRESHOLD = 2000;

    /**
     * 上报周期
     */
    public static final Integer MONITOR_SYSTEM_SINK_STEP = 60;

    public static final Integer MONITOR_SYSTEM_METRIC_VALUE_EFFECTIVE = 1;
}
