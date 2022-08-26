package com.xiaojukeji.know.streaming.km.common.bean.event.kafka.zk;

import lombok.Getter;

@Getter
public class ControllerChangeEvent extends BaseKafkaZKEvent {
    public ControllerChangeEvent(Long eventTime, Long clusterPhyId) {
        super(eventTime, clusterPhyId);
    }
}
