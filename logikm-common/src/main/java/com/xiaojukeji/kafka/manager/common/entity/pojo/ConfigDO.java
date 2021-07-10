package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 20/3/19
 */
public class ConfigDO {
    private Long id;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String configKey;

    private String configValue;

    private String configDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }

    @Override
    public String toString() {
        return "ConfigDO{" +
                "id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", configKey='" + configKey + '\'' +
                ", configValue='" + configValue + '\'' +
                ", configDescription='" + configDescription + '\'' +
                '}';
    }
}