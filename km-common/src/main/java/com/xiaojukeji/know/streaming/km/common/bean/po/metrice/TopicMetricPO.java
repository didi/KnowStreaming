package com.xiaojukeji.know.streaming.km.common.bean.po.metrice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xiaojukeji.know.streaming.km.common.constant.Constant.*;
import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicMetricPO extends BaseMetricESPO {

    private String      topic;

    private Integer     brokerId;

    /**
     * brokerAgg: 1：是由broker聚合而成的topic整体维度指标，0：是broker维度的指标
     * 针对类似枚举和状态类属性，使用字符串来表示，ES的查询效率更高
     */
    private String     brokerAgg = ONE;

    /**
     * defaultTopic: 1：是集群默认索引，0：不是集群默认索引
     * 针对类似枚举和状态类属性，使用字符串来表示，ES的查询效率更高
     */
    private String      defaultTopic = ZERO;

    public TopicMetricPO(String topic, Long clusterPhyId){
        super(clusterPhyId);
        this.topic   = topic;
    }

    @Override
    public String getKey() {
        return ONE.equals(brokerAgg) ? "T@" + clusterPhyId + "@" + topic + "@" + monitorTimestamp2min(timestamp)
                : "T@" + clusterPhyId + "@" + brokerId + "@" + topic + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return topic;
    }
}
