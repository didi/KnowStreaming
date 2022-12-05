package com.xiaojukeji.know.streaming.km.common.bean.event.metric;

import com.xiaojukeji.know.streaming.km.common.bean.entity.metrics.GroupMetrics;
import lombok.Getter;

import java.util.List;

/**
 * @author didi
 */
@Getter
public class GroupMetricEvent extends BaseMetricEvent{

    private final List<GroupMetrics> groupMetrics;

    public GroupMetricEvent(Object source, List<GroupMetrics> groupMetrics) {
        super( source );
        this.groupMetrics = groupMetrics;
    }
}
