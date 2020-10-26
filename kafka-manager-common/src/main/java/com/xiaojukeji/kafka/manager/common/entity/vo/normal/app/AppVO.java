package com.xiaojukeji.kafka.manager.common.entity.vo.normal.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/4/7
 */
@ApiModel(description="App信息")
public class AppVO {
    @ApiModelProperty(value="AppId")
    private String appId;

    @ApiModelProperty(value="App名称")
    private String name;

    @ApiModelProperty(value="App密码")
    private String password;

    @ApiModelProperty(value="申请人")
    private String applicant;

    @ApiModelProperty(value="App描述")
    private String description;

    @ApiModelProperty(value="App负责人")
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

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    @Override
    public String toString() {
        return "AppVO{" +
                "appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", applicant='" + applicant + '\'' +
                ", description='" + description + '\'' +
                ", principals='" + principals + '\'' +
                '}';
    }
}