package com.xiaojukeji.kafka.manager.monitor.common.entry.sink;

/**
 * @author zengqiao
 * @date 20/5/24
 */
public abstract class AbstractMonitorKafkaSinkTag extends AbstractMonitorSinkTag {
    protected String cluster;

    public AbstractMonitorKafkaSinkTag(String cluster) {
        this.cluster = cluster;
    }
}