package com.xiaojukeji.kafka.manager.common.constant.monitor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知类型
 * @author huangyiminghappy@163.com
 * @date 2019-05-06
 */
public enum MonitorNotifyType {
    KAFKA_MESSAGE("KAFKA", "告警发送到KAFKA");

    String name;

    String message;

    MonitorNotifyType(String name, String message){
        this.name = name;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public static boolean legal(String name) {
        for (MonitorNotifyType elem: MonitorNotifyType.values()) {
            if (elem.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "NotifyType{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static List<AbstractMap.SimpleEntry<String, String>> toList() {
        List<AbstractMap.SimpleEntry<String, String>> notifyTypeList = new ArrayList<>();
        for (MonitorNotifyType elem: MonitorNotifyType.values()) {
            notifyTypeList.add(new AbstractMap.SimpleEntry<>(elem.name, elem.message));
        }
        return notifyTypeList;
    }
}
