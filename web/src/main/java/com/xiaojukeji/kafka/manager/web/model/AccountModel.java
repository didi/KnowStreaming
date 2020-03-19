package com.xiaojukeji.kafka.manager.web.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

/**
 * @author zengqiao
 * @date 19/5/3
 */
@ApiModel(value = "AccountModel", description = "用户")
public class AccountModel {
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "新密码, 1.创建账号时, 可不传")
    private String password;

    @ApiModelProperty(value = "旧密码")
    private String oldPassword;

    @ApiModelProperty(value = "角色[0:普通, 1:运维, 2:管理员]")
    private Integer role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
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

    @Override
    public String toString() {
        return "AccountModel{" +
                "username='" + username + '\'' +
                ", oldPassword='" + oldPassword + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    public boolean insertLegal() {
        if (StringUtils.isEmpty(username)
                || StringUtils.isEmpty(password)
                || !(role == 0 || role == 1 || role == 2)) {
            return false;
        }
        return true;
    }

    public boolean modifyLegal() {
        if (StringUtils.isEmpty(username)
                || !(role == 0 || role == 1 || role == 2)) {
            return false;
        }
        return true;
    }
}