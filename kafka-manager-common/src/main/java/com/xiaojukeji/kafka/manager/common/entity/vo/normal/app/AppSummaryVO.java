package com.xiaojukeji.kafka.manager.common.entity.vo.normal.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zengqiao
 * @date 20/5/22
 */
@ApiModel(description="App概览信息")
public class AppSummaryVO {
    @ApiModelProperty(value="AppId")
    private String appId;

    @ApiModelProperty(value="App名称")
    private String name;

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

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    @Override
    public String toString() {
        return "AppSummaryVO{" +
                "appId='" + appId + '\'' +
                ", name='" + name + '\'' +
                ", principals='" + principals + '\'' +
                '}';
    }
}