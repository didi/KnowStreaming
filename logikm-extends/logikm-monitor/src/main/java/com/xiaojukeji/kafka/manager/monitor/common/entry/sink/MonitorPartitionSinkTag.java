package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author zengqiao
 * @date 20/9/3
 */
public class MonitorPartitionSinkTag extends MonitorTopicSinkTag {
    protected String partition;

    public MonitorPartitionSinkTag(String cluster, String topic, Integer partition) {
        super(cluster, topic);
        this.topic = topic;
        this.partition = String.valueOf(partition);
    }

    @Override
    public String toString() {
        return "MonitorPartitionSinkTag{" +
                "topic='" + topic + '\'' +
                ", partition='" + partition + '\'' +
                ", cluster='" + cluster + '\'' +
                '}';
    }

    @Override
    public String convert2Tags() {
        return String.format("cluster=%s,topic=%s,partition=%s", cluster, topic, partition);
    }
}
