package com.xiaojukeji.know.streaming.km.common.bean.event.metric.mm2;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.mm2.MirrorMakerMetrics;
import com.xiaojukeji.know.streaming.km.common.bean.event.metric.BaseMetricEvent;
import lombok.Getter;

import java.util.List;

/**
 * @author zengqiao
 * @date 2022/12/20
 */
@Getter
public class MirrorMakerMetricEvent extends BaseMetricEvent {
    private final List<MirrorMakerMetrics> metricsList;

    public MirrorMakerMetricEvent(Object source, List<MirrorMakerMetrics> metricsList) {
        super(source);
        this.metricsList = metricsList;
    }
}
