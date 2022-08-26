package com.xiaojukeji.know.streaming.km.common.bean.entity.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.xiaojukeji.know.streaming.km.common.constant.Constant.ONE;
import static com.xiaojukeji.know.streaming.km.common.constant.Constant.ZERO;

/**
 * Consumer实体类
 * @author tukun
 * @date 2015/11/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupMetrics extends BaseMetrics{
    private String group;

    private String topic;

    private Integer partitionId;

    /**
     * true: group维度的指标, topicName & partitionId 为null; false: group & topicName & partitionId维度的指标
     */
    private boolean bGroupMetric;

    /**
     * 1: group维度的指标， 0: group & topicName & partitionId维度的指标
     * 针对类似枚举和状态类属性，使用字符串来表示比使用数值或者bool类型，ES的查询效率更高
     */
    private String  groupMetric;

    public GroupMetrics(Long clusterPhyId, String group, boolean bGroupMetric){
        this.clusterPhyId   = clusterPhyId;
        this.group          = group;
        this.bGroupMetric   = bGroupMetric;
        this.groupMetric    = bGroupMetric ? ONE : ZERO;
    }

    public GroupMetrics(Long clusterPhyId, String group, String topic, Integer partitionId) {
        this.clusterPhyId   = clusterPhyId;
        this.group          = group;
        this.topic          = topic;
        this.partitionId    = partitionId;
        this.bGroupMetric   = false;
        this.groupMetric    = ZERO;
    }

    public GroupMetrics(Long clusterPhyId, Integer partitionId, String topic, String group, boolean bGroupMetric) {
        super(clusterPhyId);
        this.partitionId    = partitionId;
        this.topic          = topic;
        this.group          = group;
        this.bGroupMetric   = bGroupMetric;
        this.groupMetric    = bGroupMetric ? ONE : ZERO;
    }

    @Override
    public String unique() {
        return ONE.equals( bGroupMetric ) ? "G@" +clusterPhyId + "@" + group
                : "G@" +clusterPhyId + "@" + group + "@" + partitionId + "@" + topic;
    }
}
