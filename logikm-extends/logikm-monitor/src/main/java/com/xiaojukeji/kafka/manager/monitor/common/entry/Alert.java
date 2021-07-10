package com.xiaojukeji.kafka.manager.monitor.common.entry;

import com.xiaojukeji.kafka.manager.monitor.common.entry.dto.MetricPoint;

import java.util.List;
import java.util.Properties;

/**
 * @author zengqiao
 * @date 20/5/27
 */
public class Alert {
    /**
     * 告警ID
     */
    private Long id;

    /**
     * 监控ID
     */
    private Long monitorId;

    /**
     * 监控策略ID
     */
    private Long strategyId;

    /**
     * 监控策略名称
     */
    private String strategyName;

    /**
     * 告警类型
     */
    private String type;

    /**
     * 告警优先级
     */
    private Integer priority;

    /**
     * 告警的指标
     */
    private String metric;

    /**
     * 触发告警的曲线tags
     */
    private Properties tags;

    /**
     * 告警开始时间
     */
    private Long startTime;

    /**
     * 告警结束时间
     */
    private Long endTime;

    /**
     * 现场值
     */
    private Double value;

    /**
     * 现场值
     */
    private List<MetricPoint> points;

    /**
     * 告警组
     */
    private List<String> groups;

    /**
     * 表达式
     */
    private String info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(Long monitorId) {
        this.monitorId = monitorId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Properties getTags() {
        return tags;
    }

    public void setTags(Properties tags) {
        this.tags = tags;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<MetricPoint> getPoints() {
        return points;
    }

    public void setPoints(List<MetricPoint> points) {
        this.points = points;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", monitorId=" + monitorId +
                ", strategyId=" + strategyId +
                ", strategyName='" + strategyName + '\'' +
                ", type='" + type + '\'' +
                ", priority=" + priority +
                ", metric='" + metric + '\'' +
                ", tags=" + tags +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", value=" + value +
                ", points=" + points +
                ", groups=" + groups +
                ", info='" + info + '\'' +
                '}';
    }
}