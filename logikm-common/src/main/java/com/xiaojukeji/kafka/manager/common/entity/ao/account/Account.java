package com.xiaojukeji.kafka.manager.common.entity.ao.account;

import com.xiaojukeji.kafka.manager.common.bizenum.AccountRoleEnum;

/**
 * 用户信息
 * @author zengqiao
 * @date 20/6/10
 */
public class Account {
    private String username;

    private String chineseName;

    private String department;

    private AccountRoleEnum accountRoleEnum;

    public Account(String username, String chineseName, String department, AccountRoleEnum accountRoleEnum) {
        this.username = username;
        this.chineseName = chineseName;
        this.department = department;
        this.accountRoleEnum = accountRoleEnum;
    }

    public Account() {
        super();
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

    public AccountRoleEnum getAccountRoleEnum() {
        return accountRoleEnum;
    }

    public void setAccountRoleEnum(AccountRoleEnum accountRoleEnum) {
        this.accountRoleEnum = accountRoleEnum;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", department='" + department + '\'' +
                ", accountRoleEnum=" + accountRoleEnum +
                '}';
    }
}