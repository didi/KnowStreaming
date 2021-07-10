package com.xiaojukeji.kafka.manager.common.zookeeper.znode.didi;

/**
 * @author zengqiao
 * @date 20/8/21
 */
public class TopicJmxSwitch {
    private Boolean openTopicRequestMetrics = Boolean.FALSE;

    private Boolean openAppIdTopicMetrics = Boolean.FALSE;

    private Boolean openClientRequestMetrics = Boolean.FALSE;

    public TopicJmxSwitch(Boolean openTopicRequestMetrics,
                          Boolean openAppIdTopicMetrics,
                          Boolean openClientRequestMetrics) {
        this.openTopicRequestMetrics = openTopicRequestMetrics;
        this.openAppIdTopicMetrics = openAppIdTopicMetrics;
        this.openClientRequestMetrics = openClientRequestMetrics;
    }

    public Boolean getOpenTopicRequestMetrics() {
        return openTopicRequestMetrics;
    }

    public void setOpenTopicRequestMetrics(Boolean openTopicRequestMetrics) {
        this.openTopicRequestMetrics = openTopicRequestMetrics;
    }

    public Boolean getOpenAppIdTopicMetrics() {
        return openAppIdTopicMetrics;
    }

    public void setOpenAppIdTopicMetrics(Boolean openAppIdTopicMetrics) {
        this.openAppIdTopicMetrics = openAppIdTopicMetrics;
    }

    public Boolean getOpenClientRequestMetrics() {
        return openClientRequestMetrics;
    }

    public void setOpenClientRequestMetrics(Boolean openClientRequestMetrics) {
        this.openClientRequestMetrics = openClientRequestMetrics;
    }

    @Override
    public String toString() {
        return "TopicJmxSwitch{" +
                "openTopicRequestMetrics=" + openTopicRequestMetrics +
                ", openAppIdTopicMetrics=" + openAppIdTopicMetrics +
                ", openClientRequestMetrics=" + openClientRequestMetrics +
                '}';
    }
}