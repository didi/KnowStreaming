package com.xiaojukeji.know.streaming.km.common.bean.event.metric;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.TopicMetrics;
import lombok.Getter;

import java.util.List;

/**
 * @author didi
 */
@Getter
public class TopicMetricEvent extends BaseMetricEvent{

    private final List<TopicMetrics> topicMetrics;

    public TopicMetricEvent(Object source, List<TopicMetrics> topicMetrics) {
        super( source );
        this.topicMetrics = topicMetrics;
    }
}
