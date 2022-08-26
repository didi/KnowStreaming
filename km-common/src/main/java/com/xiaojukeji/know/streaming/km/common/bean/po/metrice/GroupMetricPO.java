package com.xiaojukeji.know.streaming.km.common.bean.po.metrice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xiaojukeji.know.streaming.km.common.constant.Constant.ONE;
import static com.xiaojukeji.know.streaming.km.common.utils.CommonUtils.monitorTimestamp2min;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMetricPO extends BaseMetricESPO {
    private Integer partitionId;

    private String topic;

    private String group;

    /**
     * 1: group维度的指标， 0: group & topicName & partitionId维度的指标
     * 针对类似枚举和状态类属性，使用字符串来表示比使用数值或者bool类型，ES的查询效率更高
     */
    private String groupMetric = ONE;

    @Override
    public String getKey() {
        return ONE.equals(groupMetric) ? "G@" +clusterPhyId + "@" + group + "@" + monitorTimestamp2min(timestamp)
                : "G@" +clusterPhyId + "@" + group + "@" + partitionId + "@" + topic + "@" + monitorTimestamp2min(timestamp);
    }

    @Override
    public String getRoutingValue() {
        return clusterPhyId + "@" + group;
    }
}
