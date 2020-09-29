package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/21
 */
@ApiModel(description = "账号信息")
public class AccountVO {
    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "中文名")
    private String chineseName;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "角色, 0:Normal, 1:RD, 2:OP")
    private Integer role;

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

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AccountVO{" +
                "username='" + username + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", department='" + department + '\'' +
                ", role=" + role +
                '}';
    }
}