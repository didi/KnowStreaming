package com.xiaojukeji.kafka.manager.common.utils.jmx;

public class JmxConfig {
    /**
     * 单台最大连接数
     */
    private Integer maxConn;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 开启SSL
     */
    private Boolean openSSL;

    public Integer getMaxConn() {
        return maxConn;
    }

    public void setMaxConn(Integer maxConn) {
        this.maxConn = maxConn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isOpenSSL() {
        return openSSL;
    }

    public void setOpenSSL(Boolean openSSL) {
        this.openSSL = openSSL;
    }

    @Override
    public String toString() {
        return "JmxConfig{" +
                "maxConn=" + maxConn +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", openSSL=" + openSSL +
                '}';
    }
}
