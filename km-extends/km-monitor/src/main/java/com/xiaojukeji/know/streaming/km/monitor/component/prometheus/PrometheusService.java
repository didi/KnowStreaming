package com.xiaojukeji.know.streaming.km.monitor.component.prometheus;

import com.alibaba.fastjson.JSON;
import com.didiglobal.logi.log.ILog;
import com.didiglobal.logi.log.LogFactory;
import com.xiaojukeji.know.streaming.km.common.utils.ValidateUtils;
import com.xiaojukeji.know.streaming.km.monitor.common.MetricSinkPoint;
import com.xiaojukeji.know.streaming.km.monitor.common.MonitorSinkTagEnum;
import com.xiaojukeji.know.streaming.km.monitor.component.AbstractMonitorSinkService;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author didi
 */
@Service("prometheusService")
public class PrometheusService extends AbstractMonitorSinkService {
    private static final ILog LOGGER = LogFactory.getLog(PrometheusService.class);

    private final Map<String, Gauge> gaugeMap = new ConcurrentHashMap<>();

    private final List<String> monitorSinkTagNames = new ArrayList<>();

    @Autowired
    public CollectorRegistry collectorRegistry;

    @PostConstruct
    public void init(){
        for(MonitorSinkTagEnum monitorSinkTagEnum : MonitorSinkTagEnum.values()){
            monitorSinkTagNames.add(monitorSinkTagEnum.getName());
        }

        monitorSinkTagNames.sort(Comparator.reverseOrder());
    }

    @Override
    protected String monitorName() {
        return "prometheus";
    }

    @Override
    public Boolean sinkMetrics(List<MetricSinkPoint> pointList) {
        if(ValidateUtils.isEmptyList(pointList)){
            return true;
        }

        for (MetricSinkPoint metricSinkPoint : pointList){
            try {
                String metricName = metricSinkPoint.getName().replace("-", "_");

                Gauge gauge = gaugeMap.get(metricName);

                if(null == gauge) {
                    gauge = Gauge.build().name(metricName)
                            .help(metricName + " metrics")
                            .labelNames(monitorSinkTagNames.toArray(new String[monitorSinkTagNames.size()]))
                            .register(collectorRegistry);
                    gaugeMap.put(metricName, gauge);
                }

                Map<String, Object> tagsMap = metricSinkPoint.getTagsMap();
                List<String> labels = new ArrayList<>();
                for(String metricTagName : monitorSinkTagNames){
                    if (tagsMap.containsKey(metricTagName)){
                        labels.add(tagsMap.get(metricTagName).toString());
                    } else {
                        labels.add("");
                    }
                }

                gauge.labels(labels.toArray(new String[labels.size()])).set(metricSinkPoint.getValue());
            } catch (Exception e){
                LOGGER.error("sink metrics to prometheus exception, metric:{}", JSON.toJSONString(metricSinkPoint), e);
            }
        }

        return true;
    }
}
