package com.xiaojukeji.kafka.manager.common.entity.dto.alarm;

import java.util.Map;

/**
 * @author zengqiao
 * @date 19/12/16
 */
public class AlarmRuleDTO {
    /**
     * 告警ID
     */
    private Long id;

    /**
     * 告警名称
     */
    private String name;

    /**
     * 已持续次数
     */
    private Integer duration;

    /**
     * 集群ID, 过滤条件中必有的, 单独拿出来
     */
    private Long clusterId;

    /**
     * 告警策略表达式
     */
    private AlarmStrategyExpressionDTO strategyExpression;

    /**
     * 告警策略过滤条件
     */
    private Map<String, String> strategyFilterMap;

    /**
     * 告警策略Action方式
     */
    private Map<String, AlarmStrategyActionDTO> strategyActionMap;

    /**
     * 修改时间
     */
    private Long gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public AlarmStrategyExpressionDTO getStrategyExpression() {
        return strategyExpression;
    }

    public void setStrategyExpression(AlarmStrategyExpressionDTO strategyExpression) {
        this.strategyExpression = strategyExpression;
    }

    public Map<String, String> getStrategyFilterMap() {
        return strategyFilterMap;
    }

    public void setStrategyFilterMap(Map<String, String> strategyFilterMap) {
        this.strategyFilterMap = strategyFilterMap;
    }

    public Map<String, AlarmStrategyActionDTO> getStrategyActionMap() {
        return strategyActionMap;
    }

    public void setStrategyActionMap(Map<String, AlarmStrategyActionDTO> strategyActionMap) {
        this.strategyActionMap = strategyActionMap;
    }

    public Long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Long gmtModify) {
        this.gmtModify = gmtModify;
    }

    @Override
    public String toString() {
        return "AlarmRuleDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", clusterId=" + clusterId +
                ", strategyExpression=" + strategyExpression +
                ", strategyFilterMap=" + strategyFilterMap +
                ", strategyActionMap=" + strategyActionMap +
                ", gmtModify=" + gmtModify +
                '}';
    }
}
