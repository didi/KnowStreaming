package com.xiaojukeji.know.streaming.km.common.bean.po.metrice;

import com.xiaojukeji.know.streaming.km.common.bean.po.BaseESPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseMetricESPO extends BaseESPO {

    protected Long   clusterPhyId;

    protected long  timestamp = monitorTimestamp2min(System.currentTimeMillis());

    protected Map<String, Float> metrics = new ConcurrentHashMap<>();

    protected BaseMetricESPO(Long clusterPhyId){
        this.clusterPhyId   = clusterPhyId;
    }

    public void putMetric(String key, Float value) {
        metrics.put(key, value);
    }

    public void putMetrics(Map<String, Float> metrics){
        this.metrics.putAll(metrics);
    }

    public Map<String, Float> getMetrics(List<String> metricNames){
        if(CollectionUtils.isEmpty(metricNames)){return metrics;}

        Map<String, Float> retMetrics = new HashMap<>();
        for(String metric : metricNames){
            Float value = metrics.get(metric);
            if(null != value){
                retMetrics.put(metric, value);
            }
        }

        return retMetrics;
    }
}
