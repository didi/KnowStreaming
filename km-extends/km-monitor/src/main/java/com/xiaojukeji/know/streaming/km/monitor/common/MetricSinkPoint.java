package com.xiaojukeji.know.streaming.km.monitor.common;

import lombok.Data;

import java.util.Map;

@Data
public class MetricSinkPoint {
    /**
     * 指标名
     */
    private String name;

    /**
     * 指标值
     */
    private Float value;

    /**
     * 上报周期，单位秒
     */
    private int step;

    /**
     * 当前时间戳，单位为s
     */
    private long timestamp;

    /**
     * tags
     */
    private Map<String, Object> tagsMap;

    public MetricSinkPoint() {}
}
