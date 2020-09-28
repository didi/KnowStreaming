package com.xiaojukeji.kafka.manager.common.entity.vo.thirdpart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhongyuankai
 * @date 2020/6/18
 */
@Deprecated
@ApiModel(description="AppID基本信息")
public class AppBasicInfoVO {
    @ApiModelProperty(value="appId")
    private String appId;

    @ApiModelProperty(value="app密码")
    private String password;

    @ApiModelProperty(value="app名称")
    private String name;

    @ApiModelProperty(value="申请人")
    private String applicant;

    @ApiModelProperty(value="appId负责人")
    private String principal;

    @ApiModelProperty(value="描述信息")
    private String description;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AppBasicInfoVO{" +
                "appId='" + appId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", applicant='" + applicant + '\'' +
                ", principal='" + principal + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
