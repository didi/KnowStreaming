package com.xiaojukeji.kafka.manager.common.entity.dto.rd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaojukeji.kafka.manager.common.utils.ValidateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 19/5/3
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "用户")
public class AccountDTO {
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "角色")
    private Integer role;

    @ApiModelProperty(value = "用户姓名")
    private String displayName;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "邮箱")
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
        return "AccountDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", displayName='" + displayName + '\'' +
                ", department='" + department + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }

    public boolean legal() {
        if (ValidateUtils.isNull(username)
                || !(role == 0 || role == 1 || role == 2)) {
            return false;
        }
        if (ValidateUtils.isNull(password)) {
            password = "";
        }
        return true;
    }
}