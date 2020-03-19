package com.xiaojukeji.kafka.manager.common.entity.dto.alarm;

/**
 * 告警过滤条件
 * @author zengqiao
 * @date 19/12/16
 */
public class AlarmStrategyFilterDTO {
    private String key;

    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AlarmStrategyFilterModel{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public boolean legal() {
        if (key == null
                || value == null) {
            return false;
        }
        return true;
    }
}