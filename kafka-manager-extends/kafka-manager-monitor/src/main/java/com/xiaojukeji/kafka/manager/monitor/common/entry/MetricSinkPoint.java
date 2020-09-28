package com.xiaojukeji.kafka.manager.monitor.common.entry;

import com.xiaojukeji.kafka.manager.monitor.common.entry.sink.MonitorBaseSinkTag;

import java.text.DecimalFormat;

/**
 * @author huangjw
 * @date 17/5/24.
 */
public class MetricSinkPoint {
    /**
     * 指标名
     */
    private String name;

    /**
     * 指标值
     */
    private String value;

    /**
     * 上报周期
     */
    private int step;

    /**
     * 当前时间戳，单位为s
     */
    private long timestamp;

    /**
     * tags
     */
    private MonitorBaseSinkTag tags;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public MetricSinkPoint(String name, double value, int step, long timestamp, MonitorBaseSinkTag tags) {
        if (value <= 0.001) {
            value = 0.0;
        }
        this.name = name;
        this.value = DECIMAL_FORMAT.format(value);
        this.step = step;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MonitorBaseSinkTag getTags() {
        return tags;
    }

    public int getStep() {
        return step;
    }

    @Override
    public String toString() {
        return "MetricPoint{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", timestamp=" + timestamp +
                ", tags=" + tags +
                ", step=" + step +
                '}';
    }
}
