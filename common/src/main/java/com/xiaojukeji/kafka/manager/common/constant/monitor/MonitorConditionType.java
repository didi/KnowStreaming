package com.xiaojukeji.kafka.manager.common.constant.monitor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * 条件类型
 * @author zengqiao
 * @date 19/5/12
 */
public enum MonitorConditionType {
    BIGGER(">", "大于"),
    EQUAL("=", "等于"),
    LESS("<", "小于"),
    NOT_EQUAL("!=", "不等于");

    private String name;

    private String message;

    MonitorConditionType(String name, String message) {
        this.name = name;
        this.message = message;
    }

    public static boolean legal(String name) {
        for (MonitorConditionType elem: MonitorConditionType.values()) {
            if (elem.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ConditionType{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static List<AbstractMap.SimpleEntry<String, String>> toList() {
        List<AbstractMap.SimpleEntry<String, String>> conditionTypeList = new ArrayList<>();
        for (MonitorConditionType elem: MonitorConditionType.values()) {
            conditionTypeList.add(new AbstractMap.SimpleEntry<>(elem.name, elem.message));
        }
        return conditionTypeList;
    }

    /**
     * 计算 operation(data1, data2) 是否为true
     * @param data1
     * @param data2
     * @param operation
     * @author zengqiao
     * @date 19/5/12
     * @return boolean
     */
    public static boolean matchCondition(Double data1, Double data2, String operation) {
        switch (operation) {
            case ">": return data1 > data2;
            case "<": return data1 < data2;
            case "=": return data1.equals(data2);
            case "!=": return !data1.equals(data2);
            default:
        }
        return false;
    }
}