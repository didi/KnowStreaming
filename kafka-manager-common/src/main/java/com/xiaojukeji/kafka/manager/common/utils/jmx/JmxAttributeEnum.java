package com.xiaojukeji.kafka.manager.common.utils.jmx;

import java.util.Arrays;

/**
 * @author zengqiao
 * @date 20/6/18
 */
public enum JmxAttributeEnum {
    RATE_ATTRIBUTE(new String[]{
            "MeanRate",
            "OneMinuteRate",
            "FiveMinuteRate",
            "FifteenMinuteRate"
    }),
    PERCENTILE_ATTRIBUTE(new String[]{
            "Mean",
            "50thPercentile",
            "75thPercentile",
            "95thPercentile",
            "98thPercentile",
            "99thPercentile",
            "999thPercentile"
    }),
    VALUE_ATTRIBUTE(new String[]{
            "Value"
    }),
    VERSION_ATTRIBUTE(new String[]{
            "Version"
    }),
        ;
    private String[] attribute;

    JmxAttributeEnum(String[] attribute) {
        this.attribute = attribute;
    }

    public String[] getAttribute() {
        return attribute;
    }

    public void setAttribute(String[] attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return "JmxAttributeEnum{" +
                "attribute=" + Arrays.toString(attribute) +
                '}';
    }
}