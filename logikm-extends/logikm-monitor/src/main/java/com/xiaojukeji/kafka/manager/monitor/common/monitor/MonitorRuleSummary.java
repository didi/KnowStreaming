package com.xiaojukeji.kafka.manager.monitor.common.monitor;

/**
 * @author zengqiao
 * @date 20/5/22
 */
public class MonitorRuleSummary {
    private Long id;

    private String name;

    private String appId;

    private String appName;

    private String principals;

    private String operator;

    private Long createTime;

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

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "MonitorRuleSummary{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", appId='" + appId + '\'' +
                ", appName='" + appName + '\'' +
                ", principals='" + principals + '\'' +
                ", operator='" + operator + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}