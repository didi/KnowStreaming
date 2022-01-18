package com.xiaojukeji.kafka.manager.common.entity.pojo;

import java.util.Date;

/**
 * @author zengqiao
 * @date 19/5/3
 */
public class AccountDO {
    protected Long id;

    protected Integer status;

    protected Date gmtCreate;

    protected Date gmtModify;

    private String username;

    private String password;

    private Integer role;

    private String displayName;

    private String department;

    private String mail;

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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "AccountDO{" +
                "id=" + id +
                ", status=" + status +
                ", gmtCreate=" + gmtCreate +
                ", gmtModify=" + gmtModify +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", displayName='" + displayName + '\'' +
                ", department='" + department + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}