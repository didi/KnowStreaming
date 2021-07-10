package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/8/26
 */
@ApiModel(description = "账号角色信息")
public class AccountRoleVO {
    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "角色, 0:Normal, 1:RD, 2:OP")
    private Integer role;

    public AccountRoleVO(String username, Integer role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AccountRoleVO{" +
                "username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}