package com.xiaojukeji.kafka.manager.common.entity.vo.normal.topic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/8/14
 */
@ApiModel(description="Topic流量统计信息")
public class TopicStatisticMetricsVO {
    @ApiModelProperty(value="峰值流入流量(B/s)")
    private Double peakBytesIn;

    public TopicStatisticMetricsVO(Double peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    public Double getPeakBytesIn() {
        return peakBytesIn;
    }

    public void setPeakBytesIn(Double peakBytesIn) {
        this.peakBytesIn = peakBytesIn;
    }

    @Override
    public String toString() {
        return "TopicStatisticMetricsVO{" +
                "peakBytesIn=" + peakBytesIn +
                '}';
    }
}