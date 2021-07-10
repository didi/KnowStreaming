package com.xiaojukeji.kafka.manager.monitor.common.entry.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/18
 */
@ApiModel(description = "监控告警")
public class MonitorRuleSummaryVO {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "告警名")
    private String name;

    @ApiModelProperty(value = "应用ID")
    private String appId;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "应用负责任")
    private String principals;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "创建时间")
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
        return "MonitorRuleSummaryVO{" +
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