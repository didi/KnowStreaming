package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

/**
 * @author zengqiao
 * @date 20/6/16
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseMetrics implements Serializable {
    protected Long  clusterPhyId;

    protected Long  timestamp = monitorTimestamp2min(System.currentTimeMillis());

    protected Map<String, Float> metrics = new ConcurrentHashMap<>();

    public void putMetric(String key, Float value){
        if (value == null || key == null) {
            return;
        }

        metrics.put(key, value);
    }

    public void putMetric(Map<String, Float> metrics){ this.metrics.putAll(metrics);}

    public Float getMetric(String key) {
        return metrics.get(key);
    }

    protected BaseMetrics(Long clusterPhyId) {
        this.clusterPhyId = clusterPhyId;
    }

    public abstract String unique();
}