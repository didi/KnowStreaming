package com.xiaojukeji.kafka.manager.common.constant.monitor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * 指标类型
 * @author zengqiao
 * @date 19/5/12
 */
public enum MonitorMetricsType {
    BYTES_IN("BytesIn", "流入流量"),
    BYTES_OUT("BytesOut", "流出流量"),
    LAG("Lag", "消费组Lag");

    private String name;

    private String message;

    MonitorMetricsType(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public static boolean legal(String name) {
        for (MonitorMetricsType elem: MonitorMetricsType.values()) {
            if (elem.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MetricType{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static List<AbstractMap.SimpleEntry<String, String>> toList() {
        List<AbstractMap.SimpleEntry<String, String>> metricTypeList = new ArrayList<>();
        for (MonitorMetricsType elem: MonitorMetricsType.values()) {
            metricTypeList.add(new AbstractMap.SimpleEntry<>(elem.name, elem.message));
        }
        return metricTypeList;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

}
