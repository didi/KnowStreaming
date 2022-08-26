package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.xiaojukeji.know.streaming.km.common.constant.Constant.ONE;
import static com.xiaojukeji.know.streaming.km.common.constant.Constant.ZERO;

/**
 * @author zengqiao
 * @date 20/6/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TopicMetrics extends BaseMetrics {
    private String  topic;

    private Integer brokerId;

    /**
     * topic的指标是否是有broker上的指标聚合而来，true的时候brokerId为null
     */
    private boolean bBrokerAgg = true;

    /**
     * brokerAgg: 1：是由broker聚合而成的topic整体维度指标，0：是broker维度的指标
     * 针对类似枚举和状态类属性，使用字符串来表示，ES的查询效率更高
     */
    private String  brokerAgg = ONE;

    public TopicMetrics(String topic, Long clusterPhyId){
        this.topic          = topic;
        this.clusterPhyId   = clusterPhyId;
        this.bBrokerAgg     = true;
        this.brokerAgg      = bBrokerAgg ? ONE : ZERO;
    }

    public TopicMetrics(String topic, Long clusterPhyId, boolean bBrokerAgg){
        this.topic          = topic;
        this.clusterPhyId   = clusterPhyId;
        this.bBrokerAgg     = bBrokerAgg;
        this.brokerAgg      = bBrokerAgg ? ONE : ZERO;
    }

    public TopicMetrics(String topic, Long clusterPhyId, Integer brokerId, boolean bBrokerAgg){
        this.topic          = topic;
        this.clusterPhyId   = clusterPhyId;
        this.brokerId       = brokerId;
        this.bBrokerAgg     = bBrokerAgg;
        this.brokerAgg      = bBrokerAgg ? ONE : ZERO;
    }

    @Override
    public String unique() {
        return ONE.equals( bBrokerAgg ) ? "T@" + clusterPhyId + "@" + topic
                : "T@" + clusterPhyId + "@" + brokerId + "@" + topic;
    }
}