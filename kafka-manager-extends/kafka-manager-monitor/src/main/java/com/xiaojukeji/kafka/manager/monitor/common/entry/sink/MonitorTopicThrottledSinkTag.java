package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author crius
 * @date 19/5/21.
 */
public class MonitorTopicThrottledSinkTag extends MonitorKafkaBaseSinkTag {
    private String topic;

    private String appId;

    public MonitorTopicThrottledSinkTag(String host, String cluster, String topic, String appId) {
        super(host, cluster);
        this.topic = topic;
        this.appId = appId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public String toString() {
        return "MonitorTopicThrottledSinkTag{" +
                "topic='" + topic + '\'' +
                ", appId='" + appId + '\'' +
                ", cluster='" + cluster + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}
