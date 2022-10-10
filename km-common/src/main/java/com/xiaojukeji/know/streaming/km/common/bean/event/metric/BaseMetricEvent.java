package com.xiaojukeji.know.streaming.km.common.bean.event.metric;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author didi
 */
@Getter
public class BaseMetricEvent extends ApplicationEvent {
    public BaseMetricEvent(Object source) {
        super( source );
    }
}
