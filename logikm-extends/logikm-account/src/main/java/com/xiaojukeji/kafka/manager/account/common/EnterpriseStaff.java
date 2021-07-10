package com.xiaojukeji.kafka.manager.account.common;

/**
 * 企业员工信息
 * @author zengqiao
 * @date 20/8/26
 */
public class EnterpriseStaff {
    private String username;

    private String chineseName;

    private String department;

    public EnterpriseStaff(String username, String chineseName, String department) {
        this.username = username;
        this.chineseName = chineseName;
        this.department = department;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "EnterpriseStaff{" +
                "username='" + username + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}