package com.xiaojukeji.kafka.manager.bpm.common.entry.detail;

/**
 * @author zhongyuankai
 * @date 2020/5/18
 */
public class OrderDetailApplyAppDTO extends AbstractOrderDetailData {
    private String appId;

    private String name;

    private String password;

    private String principals;

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

    @Override
    public String toString() {
        return "OrderDetailApplyAppDTO{" +
                "appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", principals='" + principals + '\'' +
                '}';
    }
}