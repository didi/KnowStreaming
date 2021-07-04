![kafka-manager-logo](../assets/images/common/logo_name.png)

**一站式`Apache Kafka`集群指标监控与运维管控平台**

---

## 报警策略-监控指标说明

| 指标 | 含义 |备注  |
| --- | --- | --- |
| online-kafka-consumer-lag | 消费时，按照分区的维度进行监控lag数  | lag表示有多少数据没有被消费，因为按照分区的维度监控，所以告警时一般会有分区信息 |
| online-kafka-consumer-maxLag | 消费时，按照整个Topic的维度，监控Topic所有的分区里面的那个最大的lag | 比如每个分区的lag分别是3、5、7，那么maxLag的值就是max(3,5,7)=7 |
| online-kafka-consumer-maxDelayTime | 消费时，按照Topic维度监控预计的消费延迟  | 这块是按照lag和messagesIn之间的关系计算出来的，可能会有误差 |

## 报警策略-报警函数介绍

| 类别 | 函数 | 含义 |函数文案 |备注  |
| --- | --- | --- | --- | --- |
| 发生次数 |all，n  | 最近$n个周期内，全发生 | 连续发生(all) |  |
| 发生次数 | happen, n, m | 最近$n个周期内，发生m次 | 出现(happen) | null点也计算在n内 |
| 数学统计 | 	sum, n | 最近$n个周期取值 的 和 | 求和(sum) | sum_over_time |
| 数学统计 | avg, n | 最近$n个周期取值 的 平均值 | 平均值(avg) | avg_over_time |
| 数学统计 | min, n | 最近$n个周期取值 的 最小值 | 最小值(min) | min_over_time |
| 数学统计	 | max, n | 最近$n个周期取值 的 最大值 | 最大值(max | max_over_time |
| 变化率 | pdiff, n | 最近$n个点的变化率, 有一个满足 则触发 | 突增突降率(pdiff) | 假设, 最近3个周期的值分别为 v, v2, v3（v为最新值）那么计算公式为 any( (v-v2)/v2, (v-v3)/v3 )**区分正负** |
| 变化量 | diff, n | 最近$n个点的变化量, 有一个满足 则触发 | 突增突降值(diff) | 假设, 最近3个周期的值分别为 v, v2, v3（v为最新值）那么计算公式为 any( (v-v2), (v-v3) )**区分正负** |
| 变化量 | ndiff | 最近n个周期，发生m次 v(t) - v(t-1) $OP threshold其中 v(t) 为最新值 | 连续变化（区分正负） - ndiff	 |  |
| 数据中断 | nodata, t | 最近 $t 秒内 无数据上报 | 数据上报中断(nodata) |  |
| 同环比 | c_avg_rate_abs, n | 最近$n个周期的取值，相比 1天或7天前取值 的变化率 的绝对值 | 同比变化率(c_avg_rate_abs) | 假设最近的n个值为 v1, v2, v3历史取到的对应n'个值为 v1', v2'那么计算公式为abs((avg(v1,v2,v3) / avg(v1',v2') -1)* 100%) |
| 同环比 | c_avg_rate, n | 最近$n个周期的取值，相比 1天或7天前取值 的变化率（**区分正负**) | 同比变化率(c_avg_rate) | 假设最近的n个值为 v1, v2, v3历史取到的对应n'个值为 v1', v2'那么计算公式为(avg(v1,v2,v3) / avg(v1',v2') -1)* 100% |
