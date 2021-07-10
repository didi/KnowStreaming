package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

import com.xiaojukeji.kafka.manager.common.entity.ao.topic.TopicConnection;

import java.util.List;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
public class OrderDetailDeleteAppDTO extends AbstractOrderDetailData {
    private String appId;

    private String name;

    private String password;

    private String principals;

    private List<TopicConnection> connectionList;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public List<TopicConnection> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<TopicConnection> connectionList) {
        this.connectionList = connectionList;
    }

    @Override
    public String toString() {
        return "OrderDetailDeleteAppDTO{" +
                "appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", principals='" + principals + '\'' +
                ", connectionList=" + connectionList +
                '}';
    }
}