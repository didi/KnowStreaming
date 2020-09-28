package com.xiaojukeji.kafka.manager.monitor.component.n9e;

import com.xiaojukeji.kafka.manager.monitor.common.entry.MetricSinkPoint;
import com.xiaojukeji.kafka.manager.monitor.component.n9e.entry.N9eMetricSinkPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/8/26
 */
public class N9eConverter {
    public static List<N9eMetricSinkPoint> convert2N9eMetricSinkPointList(List<MetricSinkPoint> pointList) {
        if (pointList == null || pointList.isEmpty()) {
            return new ArrayList<>();
        }
        List<N9eMetricSinkPoint> n9ePointList = new ArrayList<>();
        for (MetricSinkPoint sinkPoint: pointList) {
            n9ePointList.add(new N9eMetricSinkPoint(
                    sinkPoint.getName(),
                    sinkPoint.getValue(),
                    sinkPoint.getStep(),
                    sinkPoint.getTimestamp(),
                    sinkPoint.getTags()
            ));
        }
        return n9ePointList;
    }
}