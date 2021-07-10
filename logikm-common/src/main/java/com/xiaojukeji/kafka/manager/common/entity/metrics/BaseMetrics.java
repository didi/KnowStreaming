package com.xiaojukeji.kafka.manager.common.entity.metrics;

import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import com.xiaojukeji.kafka.manager.common.utils.jmx.JmxConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengqiao
 * @date 20/6/16
 */
public class BaseMetrics {
    protected Map<String, Object> metricsMap = new HashMap<>();

    public Map<String, Object> getMetricsMap() {
        return metricsMap;
    }

    public void setMetricsMap(Map<String, Object> metricsMap) {
        this.metricsMap = metricsMap;
    }

    @Override
    public String toString() {
        return "BaseMetrics{" +
                "metricsMap=" + metricsMap +
                '}';
    }

    public Object getSpecifiedMetrics(String metricsName) {
        return metricsMap.get(metricsName);
    }

    public <T> T getSpecifiedMetrics(String metricsName, Class<T> cls) {
        Object data = metricsMap.get(metricsName);
        if (ValidateUtils.isNull(data)) {
            return null;
        }
        return cls.cast(data);
    }

    public Double getTotalProduceRequestsPerSecOneMinuteRate(Double defaultValue) {
        Object data = metricsMap.get("TotalProduceRequestsPerSecOneMinuteRate");
        if (data == null) {
            return defaultValue;
        }
        return Double.valueOf(data.toString());
    }

    public Double getTotalFetchRequestsPerSecOneMinuteRate(Double defaultValue) {
        Object data = metricsMap.get("TotalFetchRequestsPerSecOneMinuteRate");
        if (data == null) {
            return defaultValue;
        }
        return Double.valueOf(data.toString());
    }

    public Double getBytesInPerSecOneMinuteRate(Double defaultValue) {
        Object data = metricsMap.get("BytesInPerSecOneMinuteRate");
        if (data == null) {
            return defaultValue;
        }
        return Double.valueOf(data.toString());
    }

    public Double getBytesOutPerSecOneMinuteRate(Double defaultValue) {
        Object data = metricsMap.get("BytesOutPerSecOneMinuteRate");
        if (data == null) {
            return defaultValue;
        }
        return Double.valueOf(data.toString());
    }

    public Double getBytesRejectedPerSecOneMinuteRate(Double defaultValue) {
        Object data = metricsMap.get("BytesRejectedPerSecOneMinuteRate");
        if (data == null) {
            return defaultValue;
        }
        return Double.valueOf(data.toString());
    }

    public Double getMessagesInPerSecOneMinuteRate(Double defaultValue) {
        Object data = metricsMap.get("MessagesInPerSecOneMinuteRate");
        if (data == null) {
            return defaultValue;
        }
        return Double.valueOf(data.toString());
    }

    public void updateCreateTime(Long timestamp) {
        metricsMap.put(JmxConstant.CREATE_TIME, timestamp);
    }

    public BaseMetrics mergeByAdd(BaseMetrics metrics) {
        if (metrics == null) {
            return this;
        }
        for (Map.Entry<String, Object> entry: metrics.metricsMap.entrySet()) {
            mergeByAdd(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public BaseMetrics mergeByAdd(String objectKey, Object objectValue) {
        if (objectKey == null || objectValue == null) {
            return this;
        }
        if (!this.metricsMap.containsKey(objectKey)) {
            this.metricsMap.put(objectKey, objectValue);
            return this;
        }
        Object object = this.metricsMap.get(objectKey);
        if (object instanceof Integer) {
            this.metricsMap.put(objectKey, (Integer) objectValue + (Integer) object);
        } else if (object instanceof Long) {
            this.metricsMap.put(objectKey, (Long) objectValue + (Long) object);
        } else if (object instanceof Float) {
            this.metricsMap.put(objectKey, (Float) objectValue + (Float) object);
        } else if (object instanceof String) {
            this.metricsMap.put(objectKey, (String) objectValue + "," + (String) object);
        } else {
            this.metricsMap.put(objectKey, (Double) objectValue + (Double) object);
        }
        return this;
    }

    public BaseMetrics mergeByMax(BaseMetrics metrics) {
        if (metrics == null) {
            return this;
        }
        for (Map.Entry<String, Object> entry: metrics.metricsMap.entrySet()) {
            mergeByMax(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public BaseMetrics mergeByMax(String objectKey, Object objectValue) {
        if (objectKey == null || objectValue == null) {
            return this;
        }
        if (!this.metricsMap.containsKey(objectKey)) {
            this.metricsMap.put(objectKey, objectValue);
            return this;
        }

        Object object = this.metricsMap.get(objectKey);
        if (object instanceof Integer) {
            this.metricsMap.put(objectKey, Math.max((Integer) objectValue, (Integer) object));
        } else if (object instanceof Long) {
            this.metricsMap.put(objectKey, Math.max((Long) objectValue, (Long) object));
        } else if (object instanceof Float) {
            this.metricsMap.put(objectKey, Math.max((Float) objectValue, (Float) object));
        } else {
            this.metricsMap.put(objectKey, Math.max((Double) objectValue, (Double) object));
        }
        return this;
    }
}