package com.xiaojukeji.kafka.manager.common.entity.vo.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/8/26
 */
@ApiModel(description = "账号概要信息")
public class AccountSummaryVO {
    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "中文名")
    private String chineseName;

    @ApiModelProperty(value = "部门")
    private String department;

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
        return "AccountSummaryVO{" +
                "username='" + username + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}