package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/5/12
 */
public class KafkaBillDO {
    private Long id;

    private Long clusterId;

    private String topicName;

    private String principal;

    private Double quota;

    private Double cost;

    private String gmtDay;

    private Date gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Double getQuota() {
        return quota;
    }

    public void setQuota(Double quota) {
        this.quota = quota;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getGmtDay() {
        return gmtDay;
    }

    public void setGmtDay(String gmtDay) {
        this.gmtDay = gmtDay;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    @Override
    public String toString() {
        return "KafkaBillDO{" +
                "id=" + id +
                ", clusterId=" + clusterId +
                ", topicName='" + topicName + '\'' +
                ", principal='" + principal + '\'' +
                ", quota=" + quota +
                ", cost=" + cost +
                ", gmtDay='" + gmtDay + '\'' +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}