package com.xiaojukeji.kafka.manager.common.utils.jmx;

import com.xiaojukeji.kafka.manager.common.entity.KafkaVersion;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author zengqiao
 * @date 20/6/15
 */
public class MbeanV2 {
    private String fieldName;

    /**
     * mbean对象被监控的属性名称
     */
    private JmxAttributeEnum attributeEnum;

    /**
     * mbean的对象名称
     */
    private Map<Long, String> versionObjectNameMap = new TreeMap<>();

    public MbeanV2(String fieldName, JmxAttributeEnum attributeEnum, String objectName) {
        this.fieldName = fieldName;
        this.attributeEnum = attributeEnum;
        this.versionObjectNameMap.put(KafkaVersion.VERSION_MAX, objectName);
    }

    public MbeanV2(String fieldName, JmxAttributeEnum attributeEnum, List<Map.Entry<Long, String>> versionObjectNames) {
        this.fieldName = fieldName;
        this.attributeEnum = attributeEnum;
        for (Map.Entry<Long, String> entry: versionObjectNames) {
            this.versionObjectNameMap.put(entry.getKey(), entry.getValue());
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public JmxAttributeEnum getAttributeEnum() {
        return attributeEnum;
    }

    public String getObjectName(Long versionNum) {
        if (ValidateUtils.isNull(versionNum)) {
            return versionObjectNameMap.get(KafkaVersion.VERSION_MAX);
        }
        for (Map.Entry<Long, String> entry: versionObjectNameMap.entrySet()) {
            if (entry.getKey() >= versionNum) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "MbeanV2{" +
                "fieldName='" + fieldName + '\'' +
                ", attributeEnum=" + attributeEnum +
                ", versionObjectNameMap=" + versionObjectNameMap +
                '}';
    }
}