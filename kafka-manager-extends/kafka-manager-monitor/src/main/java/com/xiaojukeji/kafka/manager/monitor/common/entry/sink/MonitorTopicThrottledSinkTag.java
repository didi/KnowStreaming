package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author crius
 * @date 19/5/21.
 */
public class MonitorTopicThrottledSinkTag extends MonitorTopicSinkTag {
    private String appId;

    public MonitorTopicThrottledSinkTag(String cluster, String topic, String appId) {
        super(cluster, topic);
        this.appId = appId;
    }

    @Override
    public String convert2Tags() {
        return String.format("cluster=%s,topic=%s,appId=%s", cluster, topic, appId);
    }
}
