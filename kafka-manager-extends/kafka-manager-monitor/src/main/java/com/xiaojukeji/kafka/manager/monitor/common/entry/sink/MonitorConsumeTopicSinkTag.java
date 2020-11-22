package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author huangjw
 * @date 17/5/25.
 */
public class MonitorConsumeTopicSinkTag extends MonitorTopicSinkTag {
    private String consumerGroup;

    public MonitorConsumeTopicSinkTag(String cluster, String topic, String consumerGroup) {
        super(cluster, topic);
        this.consumerGroup = consumerGroup;
    }

    @Override
    public String toString() {
        return "MonitorConsumeTopicSinkTag{" +
                "consumerGroup='" + consumerGroup + '\'' +
                ", topic='" + topic + '\'' +
                ", cluster='" + cluster + '\'' +
                '}';
    }

    @Override
    public String convert2Tags() {
        return String.format("cluster=%s,topic=%s,consumerGroup=%s", cluster, topic, consumerGroup);
    }
}
